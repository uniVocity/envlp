package com.univocity.envlp;

import com.github.weisj.darklaf.*;
import com.github.weisj.darklaf.settings.*;
import com.github.weisj.darklaf.theme.*;
import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.cardano.wallet.embedded.services.*;
import com.univocity.envlp.database.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.*;
import com.univocity.envlp.ui.wallet.management.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;

import javax.swing.*;
import javax.swing.plaf.nimbus.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

@org.springframework.stereotype.Component
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	static boolean themeManagerLoaded = false;
	private static Theme defaultTheme;

	private WalletConfiguration config;

	private static void initUI() {
		log.info("\n=================================\nStarting up ENVLP wallet\n=================================");

		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		Locale.setDefault(new Locale("en", "US"));
		System.setProperty("user.language", "en");
		System.setProperty("user.country", "US");

		try {
			defaultTheme = new OneDarkTheme();
			LafManager.install(defaultTheme);
			LafManager.enabledPreferenceChangeReporting(true);

			Theme[] themes = LafManager.getRegisteredThemes();
			for (Theme theme : themes) {
				if (theme.getName().toLowerCase().contains("intellij")) {
					LafManager.replaceTheme(theme, theme.withDisplayName("Default light"));
					break;
				}
			}
			themeManagerLoaded = true;
		} catch (Exception e) {
			log.warn("Error initializing darcula theme");
			try {
				UIManager.setLookAndFeel(new NimbusLookAndFeel());
			} catch (Exception ex) {
				log.warn("Error initializing looking and feel", e);
			}
		}

		boolean isMacOs = (System.getProperty("os.name").toLowerCase().contains("mac"));
		if (isMacOs) {
			InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.copyAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.pasteAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), DefaultEditorKit.cutAction);
		}
	}

	private JFrame frame;

	private JTabbedPane applicationTabs = new JTabbedPane();

	private final LogPanel applicationLogPanel = new LogPanel();
	ProcessControlPanel cardanoNodeControlPanel;
	ProcessControlPanel cardanoWalletControlPanel;

	private WalletManagementPanel walletPanel;
	private JPanel themeSettingsPanel;

	private static boolean running = false;
	private EpochDetailsPanel epochDetailsPanel;
	private static Main instance;

	private WalletService walletService;

	private RemoteWalletServer walletServer;

	@Autowired
	Main(WalletConfiguration configuration, WalletService walletService, RemoteWalletServer walletServer) {
		this.config = configuration;
		this.walletService = walletService;
		this.walletServer = walletServer;
	}

	private void initialize(){
		if(frame != null){
			return;
		}
		frame = new JFrame();
		frame.setTitle("ENVLP wallet manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setContentPane(new ImagePanel("images/cardano.png"));
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.add(getApplicationTabs(), BorderLayout.CENTER);

		Image image = Utils.getImage("images/envlp.png");
		if (image != null) {
			frame.setIconImage(image);
		}
		openWalletsTab();
		frame.setJMenuBar(new MainMenu(this));
		frame.add(getEpochDetailsPanel(), BorderLayout.SOUTH);

		if (walletServer instanceof EmbeddedWalletServer) {
			EmbeddedWalletServer server = (EmbeddedWalletServer) walletServer;
			cardanoNodeControlPanel = intializeProcess(server.getNodeManager());
			cardanoWalletControlPanel = intializeProcess(server.getWalletManager());
		}
	}


	private ProcessControlPanel intializeProcess(ProcessManager process) {
		ProcessControlPanel out = new ProcessControlPanel(process);
		out.startProcess();
		return out;
	}

	private JTabbedPane getApplicationTabs() {
		if (applicationTabs == null) {
			applicationTabs = new JTabbedPane();
		}
		return applicationTabs;
	}

	private JComponent getThemeSettingsPanel() {
		if (themeSettingsPanel == null) {
			ThemeSettings settings = ThemeSettings.getInstance();
			settings.setSystemPreferencesEnabled(true);

			themeSettingsPanel = new JPanel(new BorderLayout());

			themeSettingsPanel.add(settings.getSettingsPanel(), BorderLayout.CENTER);

			JButton apply = new JButton("Apply");
			apply.addActionListener((e) -> settings.apply());

			JButton reset = new JButton("Reset");
			reset.addActionListener(e -> LafManager.install(defaultTheme));

			JPanel controls = new JPanel(new FlowLayout());
			controls.add(apply);
			controls.add(reset);

			themeSettingsPanel.add(controls, BorderLayout.SOUTH);
		}
		return themeSettingsPanel;
	}

	WalletManagementPanel getWalletPanel() {
		if (walletPanel == null) {
			walletPanel = new WalletManagementPanel();
			walletService.loadWallets().forEach(wallet -> walletPanel.getWalletList().addWallet(wallet, false));
		}
		return walletPanel;
	}

	void openWalletsTab() {
		switchToTab("Wallets", "images/wallet-ic.inline.png", this::getWalletPanel);
	}

	void openApplicationLogTab() {
		switchToTab("Application log", null, () -> applicationLogPanel);
	}

	void openCardanoNodeLogTab() {
		switchToTab("Cardano node log", null, () -> cardanoNodeControlPanel);
	}

	void openCardanoWalletLogTab() {
		switchToTab("Cardano wallet log", null, () -> cardanoWalletControlPanel);
	}

	void changeTheme() {
		switchToTab("Theme selection", null, this::getThemeSettingsPanel);
	}

	private void switchToTab(String tabTitle, String pathToIcon, Supplier<Component> componentSupplier) {
		Utils.switchToTab(getApplicationTabs(), tabTitle, pathToIcon, true, componentSupplier);
	}

	public LogPanel getApplicationLogPanel() {
		return applicationLogPanel;
	}

	public EpochDetailsPanel getEpochDetailsPanel() {
		if (epochDetailsPanel == null) {
			epochDetailsPanel = new EpochDetailsPanel(walletServer);
		}
		return epochDetailsPanel;
	}

	public static boolean isRunning() {
		return running;
	}

	public static Main getInstance(){
		return instance;
	}

	public void run() {
		initialize();

		SwingUtilities.invokeLater(() -> {
			running = true;
			frame.setVisible(true);
			log.info("Wallet ready");
		});
	}

	public static void main(String... args) {
		initUI();
		Database.initLocal();

		Main.instance = App.get(Main.class);
		instance.run();
	}
}

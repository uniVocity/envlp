package com.univocity.freecommerce;

import com.github.weisj.darklaf.*;
import com.github.weisj.darklaf.settings.*;
import com.github.weisj.darklaf.theme.*;
import com.univocity.cardano.wallet.embedded.services.*;
import com.univocity.freecommerce.database.*;
import com.univocity.freecommerce.ui.*;
import com.univocity.freecommerce.ui.components.*;
import com.univocity.freecommerce.ui.wallet.management.*;
import com.univocity.freecommerce.utils.*;
import com.univocity.freecommerce.wallet.*;
import org.slf4j.*;

import javax.swing.*;
import javax.swing.plaf.nimbus.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	static boolean themeManagerLoaded = false;
	private static Theme defaultTheme;

	private static void initUI() {
		log.info("\n=================================\nStarting up free-commerce wallet\n=================================");

		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		Locale.setDefault(new Locale("en", "US"));
		System.setProperty("user.language", "en");
		System.setProperty("user.country", "US");

		try {
			defaultTheme = new DarculaTheme();
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

	private final JFrame frame;

	private JTabbedPane applicationTabs = new JTabbedPane();

	private final LogPanel applicationLogPanel = new LogPanel();
	private final ProcessControlPanel cardanoNodeControlPanel;
	private final ProcessControlPanel cardanoWalletControlPanel;

	private WalletManagementPanel walletPanel;
	private JPanel themeSettingsPanel;
	private WalletDAO walletDAO;

	private static Main instance;
	private static boolean running = false;

	private Main() {
		frame = new JFrame();
		frame.setTitle("ENVLP wallet manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setContentPane(new ImagePanel("images/cardano.png"));
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.add(getApplicationTabs(), BorderLayout.CENTER);
		frame.setJMenuBar(new MainMenu(this));

		Image image = Utils.getImage("images/envlp.png");
		if (image != null) {
			frame.setIconImage(image);
		}

		openWalletsTab();

		String toolsDir = Configuration.getInstance().getCardanoToolsDirPath();
		cardanoNodeControlPanel = intializeProcess(new CardanoNodeManager(toolsDir).setStartupCommand(
				"run" +
						" --topology {config.dir}/mainnet-topology.json" +
						" --database-path {blockchain.dir}" +
						" --socket-path {blockchain.dir}/node.socket" +
						" --host-addr 0.0.0.0" +
						" --port {cardano.node.port}" +
						" --config {config.dir}/mainnet-config.json"
		));
		cardanoWalletControlPanel = intializeProcess(new CardanoWalletManager(toolsDir).setStartupCommand(
				"serve" +
						" --mainnet" +
						" --database {blockchain.dir}" +
						" --node-socket {blockchain.dir}/node.socket" +
						" --port {cardano.wallet.port}")
		);
	}

	private ProcessControlPanel intializeProcess(ProcessManager process) {
		Configuration.getInstance().replaceParameters(process);
		ProcessControlPanel out = new ProcessControlPanel(process);
		out.startProcess();
		return out;
	}

	public static synchronized Main getInstance() {
		if (instance == null) {
			instance = new Main();
		}
		return instance;
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

	private WalletDAO getWalletDAO() {
		if (walletDAO == null) {
			walletDAO = new WalletDAO();
		}
		return walletDAO;
	}

	WalletManagementPanel getWalletPanel() {
		if (walletPanel == null) {
			walletPanel = new WalletManagementPanel();
			getWalletDAO().loadWallets().forEach(wallet -> walletPanel.getWalletList().addWallet(wallet, false));
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

	public static boolean isRunning() {
		return running;
	}

	public void run() {
		SwingUtilities.invokeLater(() -> {
			running = true;
			frame.setVisible(true);
			log.info("Wallet ready");
		});
	}

	public static void main(String... args) {
		initUI();
		Database.initLocal();
		Main.getInstance().run();
	}
}

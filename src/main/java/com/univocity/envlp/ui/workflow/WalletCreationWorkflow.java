package com.univocity.envlp.ui.workflow;

import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.cardano.wallet.common.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.labels.*;
import com.univocity.envlp.utils.Utils;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.Wallet;
import org.apache.commons.lang3.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.*;

public class WalletCreationWorkflow extends JPanel {

	private final CardLayout cards = new CardLayout();

	private static final String RESTORE_OR_CREATE = "RESTORE_OR_CREATE";
	private String[] STEPS = new String[0];

	private static final String WALLET_DETAILS = "WALLET_DETAILS";
	private static final String WALLET_TYPE = "WALLET_TYPE";
	private static final String WORD_COUNT = "WORD_COUNT";
	private static final String GENERATE_SEED = "GENERATE_SEED";
	private static final String INPUT_SEED = "INPUT_SEED";

	private static final String[] RESTORE_STEPS = {WALLET_TYPE, WORD_COUNT, WALLET_DETAILS, INPUT_SEED};
	private static final String[] CREATE_STEPS = {WALLET_DETAILS, GENERATE_SEED};

	private JPanel cardPanel;
	private JPanel restoreOrCreatePanel;
	private JButton btCreateNewWallet;
	private JButton btRestoreWallet;

	private JButton nextButton;
	private JButton backButton;
	private JPanel navigationPanel;
	private final Stack<String> steps = new Stack<>();
	private final Consumer<Wallet> workflowEndAction;
	private final BiConsumer<String, String> workflowStepDescription;
	private WalletSetupPanel walletSetupPanel;
	private SeedGenerationPanel seedGenerationPanel;
	private WalletTypeSelectionPanel walletTypeSelectionPanel;
	private SeedWordCountSelectionPanel wordCountSelectionPanel;
	private SeedInputPanel seedInputPanel;

	private Map<String, WorkflowPanel> panels = new HashMap<>();
	private DisabledGlassPane glassPane;

	public WalletCreationWorkflow(Consumer<Wallet> workflowEndAction, BiConsumer<String, String> workflowStepDescription) {
		super(new BorderLayout());
		this.add(getCardPanel(), BorderLayout.CENTER);
		this.add(getNavigationPanel(), BorderLayout.SOUTH);
		this.workflowEndAction = workflowEndAction;
		this.workflowStepDescription = workflowStepDescription;
		initialize();
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel(cards);
			cardPanel.add(getRestoreOrCreatePanel(), RESTORE_OR_CREATE);

			panels.put(WALLET_DETAILS, walletSetupPanel = new WalletSetupPanel());
			panels.put(GENERATE_SEED, seedGenerationPanel = new SeedGenerationPanel());
			panels.put(WALLET_TYPE, walletTypeSelectionPanel = new WalletTypeSelectionPanel());
			panels.put(WORD_COUNT, wordCountSelectionPanel = new SeedWordCountSelectionPanel(walletTypeSelectionPanel));
			panels.put(INPUT_SEED, seedInputPanel = new SeedInputPanel(wordCountSelectionPanel));

			for (Map.Entry<String, WorkflowPanel> e : panels.entrySet()) {
				cardPanel.add(e.getValue(), e.getKey());
			}
		}
		return cardPanel;
	}

	private WorkflowPanel getCurrentPanel() {
		if (steps.isEmpty()) {
			return null;
		}
		return panels.get(steps.peek());
	}

	private JPanel getRestoreOrCreatePanel() {
		if (restoreOrCreatePanel == null) {
			restoreOrCreatePanel = new JPanel(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(0, 10, 0, 10);
			c.gridx = 0;
			restoreOrCreatePanel.add(getBtCreateNewWallet(), c);

			c.gridx = 1;
			restoreOrCreatePanel.add(getBtRestoreWallet(), c);
		}
		return restoreOrCreatePanel;
	}

	public JButton getBtCreateNewWallet() {
		if (btCreateNewWallet == null) {
			btCreateNewWallet = newActionButton("images/create-ic.inline.png","Create new wallet", "Generate a 24 words seed phrase to create a new wallet");
			btCreateNewWallet.addActionListener(e -> beginWalletCreation());
		}
		return btCreateNewWallet;
	}

	public JButton getBtRestoreWallet() {
		if (btRestoreWallet == null) {
			btRestoreWallet = newActionButton("images/restore-ic.inline.png","Restore wallet", "Restore an existing wallet using your seed phrase");
			btRestoreWallet.addActionListener(e -> beginWalletRestoration());
		}
		return btRestoreWallet;
	}

	private void beginWalletCreation() {
		STEPS = CREATE_STEPS;
		workflowStepDescription.accept("Creating wallet", "Setting up a brand new wallet with a new 24 words seed phrase");
		next();
	}

	private void beginWalletRestoration() {
		STEPS = RESTORE_STEPS;
		workflowStepDescription.accept("Restoring wallet", "Use an existing seed phrase to restore a wallet");
		next();
	}

	private JButton newActionButton(String iconPath, String label, String description) {
		JButton out = new JButton();
		out.setLayout(new GridBagLayout());
		out.setPreferredSize(new Dimension(300, 250));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 25, 0, 25);
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;

		JLabel icon = new JLabel(Utils.getImageIcon(iconPath, 50,50), JLabel.CENTER);
		out.add(icon, c);

		c.insets = new Insets(10, 25, 10, 25);
		c.gridy = 1;

		TitleLabel title = new TitleLabel(label, JLabel.CENTER);
		out.add(title, c);

		c.insets = new Insets(0, 25, 10, 25);
		c.gridy = 2;
		out.add(new JLabel("<html><p style=\"text-align:center;\">" + description + "</p></html>", JLabel.CENTER), c);
		return out;
	}

	private JPanel getNavigationPanel() {
		if (navigationPanel == null) {
			navigationPanel = new JPanel();
			navigationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			navigationPanel.add(getBackButton());
		}
		return navigationPanel;
	}

	protected JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton("Next");
			nextButton.addActionListener(e -> next());
		}
		return nextButton;
	}

	protected JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton("Back");
			backButton.addActionListener(e -> back());
		}
		return backButton;
	}

	private void back() {
		if (steps.isEmpty()) {
			STEPS = new String[0];
			workflowEndAction.accept(null);
			return;
		}
		steps.pop();
		cards.show(getCardPanel(), steps.isEmpty() ? RESTORE_OR_CREATE : steps.peek());

		initialize();
	}

	private void resetWorkflow() {
		steps.clear();
		panels.values().forEach(WorkflowPanel::clear);
		initialize();
	}

	public void initialize() {
		if (steps.isEmpty()) {
			workflowStepDescription.accept("New wallet wizard", "Please select one of the options below");
			panels.values().forEach(WorkflowPanel::clear);
			cards.show(getCardPanel(), RESTORE_OR_CREATE);
		}
		updateNavigationButtons();
	}

	private void next() {
		if (!steps.isEmpty() && !getCurrentPanel().validateInput()) {
			revalidate();
			repaint();
			return;
		}

		if (!hasNext()) {
			createWallet();
			return;
		}

		int index = steps.isEmpty() ? -1 : ArrayUtils.indexOf(STEPS, steps.peek());

		String next = STEPS[index + 1];

		cards.show(getCardPanel(), next);
		steps.push(next);

		updateNavigationButtons();
	}

	private DisabledGlassPane getDisabledGlassPane() {
		if (glassPane == null) {
			glassPane = new DisabledGlassPane();
			glassPane.installOnParentWindow(this);
		}
		return glassPane;
	}

	private void createWallet() {
		Thread thread = new Thread(() -> {
			Wallet[] wallet = new Wallet[1];
			try {
				String walletName = walletSetupPanel.getWalletName();
				String seed = String.valueOf(panels.get(steps.peek()).getValue());
				seed = Seed.cleanSeedPhrase(seed);

				getDisabledGlassPane().activate("Setting up wallet " + walletName + "...");

				WalletService service = new WalletService();
				wallet[0] = service.createNewWallet(walletName, seed);

				service.addAccountsFromSeed(wallet[0], seed, 10);

				//allocate one address for account 0
				service.allocateNextPaymentAddress(wallet[0], 0);
			} catch (Exception error) {
				JOptionPane.showMessageDialog(this, "Error creating wallet.", "Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				SwingUtilities.invokeLater(() -> {
					resetWorkflow();
					workflowEndAction.accept(wallet[0]);
				});
				getDisabledGlassPane().deactivate();
			}
		});
		thread.start();
	}

	private void clearNavigationPanel() {
		getNavigationPanel().remove(getBackButton());
		getNavigationPanel().remove(getNextButton());
	}

	private boolean hasNext() {
		return STEPS.length > 0 && (steps.isEmpty() || !steps.peek().equals(STEPS[STEPS.length - 1]));
	}

	private void updateNavigationButtons() {
		clearNavigationPanel();

		getNavigationPanel().add(getBackButton());
		if (steps.size() > 0) {
			getNavigationPanel().add(getNextButton());
		} else {
			getNavigationPanel().remove(getNextButton());
		}

		if (!hasNext()) {
			getNextButton().setText("Finish");
		} else {
			getNextButton().setText("Next");
		}

		WorkflowPanel panel = getCurrentPanel();
		if (panel != null) {
			panel.activate();
		}

		revalidate();
		repaint();
	}
}

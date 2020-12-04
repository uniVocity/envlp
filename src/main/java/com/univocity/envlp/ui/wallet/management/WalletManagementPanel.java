package com.univocity.envlp.ui.wallet.management;

import com.github.weisj.darklaf.components.border.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.labels.*;
import com.univocity.envlp.ui.wallet.actions.*;
import com.univocity.envlp.ui.workflow.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class WalletManagementPanel extends JPanel {

	private WalletList walletList;

	private JPanel topPanel;
	private JLabel titleHeader;
	private JLabel descriptionHeader;


	private static final String WALLET_DETAILS = "WALLET_DETAILS";
	private static final String CREATE_WALLET = "CREATE_WALLET";

	private final CardLayout cards = new CardLayout();
	private JPanel centralPanel;
	private JPanel walletSelectionPanel;
	private WalletDetailsPanel walletDetailsPanel;
	private WalletCreationWorkflow walletCreationWorkflowPanel;

	private JPanel leftContextPanel;
	private JPanel rightContextPanel;

	private final Map<String, JLabel> qrCodes = new HashMap<>();

	public WalletManagementPanel() {
		super(new BorderLayout());
		add(getTopPanel(), BorderLayout.NORTH);
		add(getCentralPanel(), BorderLayout.CENTER);
		walletSelected(null);
	}

	public WalletList getWalletList() {
		if (walletList == null) {
			walletList = new WalletList();
			walletList.addWalletSelectionListener(this::walletSelected);
			walletList.getBtAddWallet().addActionListener(e -> beginWalletCreationWorkflow());
		}
		return walletList;
	}

	private void beginWalletCreationWorkflow() {
		getWalletList().getBtAddWallet().setEnabled(false);
		getWalletCreationWorkflowPanel().initialize();
		cards.show(getCentralPanel(), CREATE_WALLET);
	}

	private void setHeaderAndDescription(String header, String description) {
		getTitleHeader().setText(header);
		getDescriptionHeader().setText(description);
	}

	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel(new GridLayout(1, 3));
			topPanel.setBorder(DarkBorders.createLineBorder(1, 1, 1, 1));

			//left
			topPanel.add(getLeftContextPanel());

			JPanel center = new JPanel(new GridBagLayout());
			//center
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(20, 10, 10, 10);
			c.weightx = 1;
			c.weighty = 0.5;
			c.fill = GridBagConstraints.BOTH;
			c.gridy = 0;
			center.add(getTitleHeader(), c);

			c.gridy = 1;
			c.insets = new Insets(10, 10, 20, 10);
			center.add(getDescriptionHeader(), c);

			topPanel.add(center, c);

			//right
			c.gridx = 2;
			topPanel.add(getRightContextPanel(), c);
		}
		return topPanel;
	}

	private JPanel getLeftContextPanel() {
		if (leftContextPanel == null) {
			leftContextPanel = new JPanel(new BorderLayout());
		}
		return leftContextPanel;
	}

	private JPanel getRightContextPanel() {
		if (rightContextPanel == null) {
			rightContextPanel = new JPanel(new BorderLayout());
		}
		return rightContextPanel;
	}

	private JLabel getTitleHeader() {
		if (titleHeader == null) {
			titleHeader = new TitleLabel("", JLabel.CENTER);
		}
		return titleHeader;
	}

	private JLabel getDescriptionHeader() {
		if (descriptionHeader == null) {
			descriptionHeader = new JLabel("", JLabel.CENTER);
		}
		return descriptionHeader;
	}

	private WalletDetailsPanel getWalletDetailsPanel() {
		if (walletDetailsPanel == null) {
			walletDetailsPanel = new WalletDetailsPanel();
			walletDetailsPanel.getReceivingPanel().addAddressSelectionListener(this::onAddressSelected);
		}
		return walletDetailsPanel;
	}

	private void onAddressSelected(String s) {
		getRightContextPanel().removeAll();
		if (s != null) {
			JLabel qrCode = qrCodes.get(s);
			if (qrCode == null) {
				int h = getRightContextPanel().getSize().height;
				qrCode = new JLabel(new ImageIcon(QrCode.generateQRCodeImageBytes(s, h, h)), SwingConstants.RIGHT);
				qrCodes.put(s, qrCode);
			}
			getRightContextPanel().add(qrCode, BorderLayout.CENTER);
		}
		revalidate();
		repaint();
	}

	private JPanel getWalletSelectionPanel() {
		if (walletSelectionPanel == null) {
			walletSelectionPanel = new JPanel(new BorderLayout());
			walletSelectionPanel.add(getWalletList(), BorderLayout.WEST);
			walletSelectionPanel.add(getWalletDetailsPanel());
		}
		return walletSelectionPanel;
	}

	private JPanel getCentralPanel() {
		if (centralPanel == null) {
			centralPanel = new JPanel(cards);
			centralPanel.add(getWalletSelectionPanel(), WALLET_DETAILS);
			centralPanel.add(getWalletCreationWorkflowPanel(), CREATE_WALLET);
		}
		return centralPanel;
	}

	private WalletCreationWorkflow getWalletCreationWorkflowPanel() {
		if (walletCreationWorkflowPanel == null) {
			walletCreationWorkflowPanel = new WalletCreationWorkflow(this::walletCreationEnded, this::setHeaderAndDescription);
		}
		return walletCreationWorkflowPanel;
	}

	private void walletCreationEnded(ColdWallet wallet) {
		walletList.addWallet(wallet, true);
		cards.show(centralPanel, WALLET_DETAILS);
		walletList.getBtAddWallet().setEnabled(true);
	}

	private void walletSelected(ColdWallet wallet) {
		setHeaderAndDescription(wallet != null ? wallet.getName() : "No wallet selected", wallet != null ? "0.000000 ADA" : "Select or create a new wallet to begin");
		getWalletDetailsPanel().setWallet(wallet);
		if(wallet == null) {
			onAddressSelected(null);
		}
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new WalletManagementPanel());
	}
}


package com.univocity.envlp.ui.wallet.actions;

import com.github.weisj.darklaf.components.border.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;

public class WalletDetailsPanel extends JTabbedPane {

	private ColdWalletService walletService;

	private ColdWallet wallet;

	private ReceivingPanel receivingPanel;
	private PublicRootKeysPanel publicRootKeysPanel;

	public WalletDetailsPanel() {
		this.setBorder(DarkBorders.createWidgetLineBorder(1, 1, 1, 1));
	}

	private void addTabs() {
		//add all tabs
		openReceiveTab();
		openPublicRootKeysTab();

		//switch back to first tab
		openReceiveTab();
	}

	private void removeTabs() {
		removeAll();
		revalidate();
		repaint();
	}

	private ColdWalletService getWalletService() {
		if (walletService == null) {
			walletService = new ColdWalletService();
		}
		return walletService;
	}

	public void setWallet(ColdWallet wallet) {
		this.wallet = wallet;
		getReceivingPanel().setWallet(wallet);
		getPublicRootKeysPanel().setWallet(wallet);

		if (wallet == null) {
			removeTabs();
		} else if (getTabCount() == 0) {
			addTabs();
		}


	}

	private void openReceiveTab() {
		Utils.switchToTab(this, "Receive", "images/receive-ic.inline.png", false, this::getReceivingPanel);
	}

	private void openPublicRootKeysTab() {
		Utils.switchToTab(this, "Root keys", "images/protected-off.inline.png", false, this::getPublicRootKeysPanel);
	}

	public ReceivingPanel getReceivingPanel() {
		if (receivingPanel == null) {
			receivingPanel = new ReceivingPanel(getWalletService());
		}
		return receivingPanel;
	}

	public PublicRootKeysPanel getPublicRootKeysPanel() {
		if (publicRootKeysPanel == null) {
			publicRootKeysPanel = new PublicRootKeysPanel();
		}
		return publicRootKeysPanel;
	}
}

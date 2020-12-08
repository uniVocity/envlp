package com.univocity.envlp.ui.wallet.actions;

import com.github.weisj.darklaf.components.border.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.swing.*;

public class WalletDetailsPanel extends JTabbedPane {

	private WalletSnapshot wallet;

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

	public void setWallet(WalletSnapshot wallet) {
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
			receivingPanel = new ReceivingPanel();
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

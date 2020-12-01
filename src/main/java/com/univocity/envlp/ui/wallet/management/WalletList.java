package com.univocity.envlp.ui.wallet.management;

import com.github.weisj.darklaf.components.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;

public class WalletList extends JPanel {

	private final JPanel spacer = new JPanel();
	private JButton btAddWallet;
	private JPanel walletListPanel;
	private JScrollPane walletListScroll;
	private final List<Consumer<Wallet>> walletSelectionListeners = new ArrayList<>();

	private List<WalletListEntry> wallets = new ArrayList<>();

	public WalletList() {
		super(new BorderLayout());
		add(new OverlayScrollPane(getWalletListScroll()), BorderLayout.CENTER);
		add(getBtAddWallet(), BorderLayout.SOUTH);
	}

	private JScrollPane getWalletListScroll() {
		if (walletListScroll == null) {
			walletListScroll = new JScrollPane(getWalletListPanel());
			walletListScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			walletListScroll.setPreferredSize(new Dimension(250, 100));

			walletListScroll.addPropertyChangeListener("font", evt -> {
				Font f = getFont();
				if (f != null) {
					int width = f.getSize() > 14 ? f.getSize() > 18 ? 450 : 350 : 250;
					setPreferredSize(new Dimension(width, 100));
				}
			});
		}
		return walletListScroll;
	}

	private JPanel getWalletListPanel() {
		if (walletListPanel == null) {
			walletListPanel = new JPanel(new GridBagLayout());
		}
		return walletListPanel;
	}

	JButton getBtAddWallet() {
		if (btAddWallet == null) {
			btAddWallet = new JButton("Add wallet", Utils.getImageIcon("images/create-ic.inline.png", 25, 25));
			btAddWallet.setVerticalTextPosition(SwingConstants.BOTTOM);
			btAddWallet.setHorizontalTextPosition(SwingConstants.CENTER);
		}
		return btAddWallet;
	}

	public void addWallet(Wallet wallet, boolean select) {
		if(wallet == null){
			walletSelectionListeners.forEach(l -> l.accept(getSelectedWallet()));
			return;
		}
		WalletListEntry entry = new WalletListEntry(wallet);
		wallets.add(entry);
		entry.addItemListener(e -> setSelectedWallet(entry));
		updateList();

		if(select) {
			entry.setSelected(true);
		}
	}

	public void addWalletSelectionListener(Consumer<Wallet> c) {
		walletSelectionListeners.add(c);
	}

	private void updateList() {
		getWalletListPanel().removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		for (int i = 0; i < wallets.size(); i++) {
			c.gridy = i;
			getWalletListPanel().add(wallets.get(i), c);
		}
		c.gridy++;
		c.weighty = 1.0;
		getWalletListPanel().add(spacer, c);

		revalidate();
		repaint();
	}

	private void setSelectedWallet(WalletListEntry entry) {
		Wallet selected;
		if (entry.isSelected()) {
			selected = entry.wallet;
			for (WalletListEntry e : wallets) {
				if (e != entry && e.isSelected()) {
					e.setSelected(false);
				}
			}
		} else {
			selected = null;
		}
		walletSelectionListeners.forEach(l -> l.accept(selected));

	}

	public Wallet getSelectedWallet() {
		for (WalletListEntry e : wallets) {
			if (e.isSelected()) {
				return e.wallet;
			}
		}
		return null;
	}
}

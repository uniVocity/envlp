package com.univocity.envlp.ui.wallet.actions;

import com.github.weisj.darklaf.components.*;
import com.univocity.envlp.database.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.*;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.report.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class PublicRootKeysPanel extends JPanel {

	private AutoAdjustingTable rootKeyTable;
	private DefaultTableModel rootKeyTableModel;
	private JScrollPane rootKeyTableScroll;
	private ReportDAO reportDAO;


	public PublicRootKeysPanel() {
		super(new BorderLayout());
		this.add(new OverlayScrollPane(getRootKeyTableScroll()), BorderLayout.CENTER);
		this.reportDAO = new ReportDAO();
	}

	private JScrollPane getRootKeyTableScroll() {
		if (rootKeyTableScroll == null) {
			rootKeyTableScroll = new JScrollPane(getRootKeyTable());
		}
		return rootKeyTableScroll;
	}

	private AutoAdjustingTable getRootKeyTable() {
		if (rootKeyTable == null) {
			rootKeyTable = new AutoAdjustingTable(getRootKeyTableModel());
			rootKeyTable.enableCopyFromColumn(1);
		}
		return rootKeyTable;
	}

	private DefaultTableModel getRootKeyTableModel() {
		if (rootKeyTableModel == null) {
			rootKeyTableModel = new ReadOnlyTableModel(new String[]{"#", "Public root key", "Total received", "Addresses", "Used", "Created"}, 0);
		}
		return rootKeyTableModel;
	}

	public void setWallet(Wallet wallet) {
		while (getRootKeyTableModel().getRowCount() > 0) {
			getRootKeyTableModel().removeRow(0);
		}

		if (wallet != null) {
			reportDAO.getAccountReport(wallet).forEach(row -> getRootKeyTableModel().addRow(row));
		}
	}

	public static void main(String... args) {
		Database.initTest();

		WalletService service = new WalletService();
		String seed = service.generateSeed();
		Wallet wallet = service.createNewWallet("test", seed);

		service.addAccountFromSeed(wallet, seed, 1);
		service.addAccountFromSeed(wallet, seed, 2);
		service.addAccountFromSeed(wallet, seed, 3);
		service.addAccountFromSeed(wallet, seed, 4);

		for (int i = 0; i < 17; i++) {
			service.allocateNextPaymentAddress(wallet);
		}

		PublicRootKeysPanel panel = new PublicRootKeysPanel();
		panel.setWallet(wallet);

		WindowUtils.launchTestWindow(panel);
	}
}


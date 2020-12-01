package com.univocity.envlp.ui.wallet.actions;

import com.github.weisj.darklaf.components.*;
import com.univocity.envlp.database.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.math.*;
import java.util.List;
import java.util.*;
import java.util.function.*;

public class ReceivingPanel extends JPanel {

	private final WalletService walletService;
	private AutoAdjustingTable addressTable;
	private ReadOnlyTableModel addressTableModel;
	private JScrollPane addressTableScroll;
	private List<Consumer<String>> addressSelectionListeners = new ArrayList<>();
	private boolean selectionIsAdjusting = false;
	private LinkLabel addAddressLink;
	private Wallet wallet;

	private JPanel commandsPanel;

	public ReceivingPanel(WalletService walletService) {
		super(new BorderLayout());
		this.add(getCommandsPanel(), BorderLayout.NORTH);
		this.add(new OverlayScrollPane(getAddressTableScroll()), BorderLayout.CENTER);
		this.walletService = walletService;
		this.addHierarchyListener(e -> updateAddressSelection());
	}

	private JScrollPane getAddressTableScroll() {
		if (addressTableScroll == null) {
			addressTableScroll = new JScrollPane(getAddressTable());
		}
		return addressTableScroll;
	}

	private AutoAdjustingTable getAddressTable() {
		if (addressTable == null) {
			addressTable = new AutoAdjustingTable(getAddressTableModel());
			addressTable.enableCopyFromColumn(1);
			addressTable.getSelectionModel().addListSelectionListener(e -> updateAddressSelection());
		}
		return addressTable;
	}

	private JPanel getCommandsPanel() {
		if (commandsPanel == null) {
			WrapLayout layout = new WrapLayout();
			layout.setHgap(0);
			commandsPanel = new JPanel(layout);
			commandsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
			commandsPanel.add(new JLabel("Share these addresses "));
			commandsPanel.add(new JLabel("to receive payments. "));
			commandsPanel.add(new JLabel("To protect your privacy "));
			commandsPanel.add(getAddAddressLink());
			commandsPanel.add(new JLabel("every time you need to "));
			commandsPanel.add(new JLabel("request funds."));


		}
		return commandsPanel;
	}

	public void addAddressSelectionListener(Consumer<String> listener) {
		addressSelectionListeners.add(listener);
	}

	private void updateAddressSelection() {
		if (selectionIsAdjusting) {
			return;
		}
		String address = null;
		int row = addressTable.getSelectedRow();
		if (isVisible() && isShowing() && row > -1 && row < addressTable.getRowCount()) {
			address = String.valueOf(addressTable.getValueAt(row, 1));
		}
		addressSelected(address);
	}

	private void addressSelected(String address) {
		addressSelectionListeners.forEach(a -> a.accept(address));
	}

	private ReadOnlyTableModel getAddressTableModel() {
		if (addressTableModel == null) {
			addressTableModel = new ReadOnlyTableModel(new String[]{"#", "Receiving address", "Total received", "Date", "Transactions"}, 0);
		}
		return addressTableModel;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
		getAddAddressLink().setEnabled(wallet != null);
		selectionIsAdjusting = true;
		int selection = getAddressTable().getSelectedRow();
		while (getAddressTableModel().getRowCount() > 0) {
			getAddressTableModel().removeRow(0);
		}

		if (wallet != null) {
			List<AddressAllocation> addresses = walletService.getAddressesForDefaultAccount(wallet);
			addresses.forEach(this::toReceivingAddressHistoryItem);
		}

		if (selection > getAddressTable().getRowCount()) {
			selection = getAddressTable().getRowCount() - 1;
		}
		if (selection >= 0) {
			getAddressTable().getSelectionModel().setSelectionInterval(selection, selection);
		}

		selectionIsAdjusting = false;
		updateAddressSelection();
	}

	private void toReceivingAddressHistoryItem(AddressAllocation allocation) {
		getAddressTableModel().addRow(new Object[]{allocation.getDerivationIndex(), allocation.getPaymentAddress(), BigDecimal.ZERO, allocation.getCreatedAt(), 0});
	}

	private LinkLabel getAddAddressLink() {
		if (addAddressLink == null) {
			addAddressLink = new LinkLabel("create a new address ");
			addAddressLink.addOnClickListener(this::addAddress);

		}
		return addAddressLink;
	}

	private void addAddress() {
		if (wallet != null) {
			walletService.allocateNextPaymentAddress(wallet, 0L);
			setWallet(wallet);

			//newer addresses show up at the top.
			getAddressTable().getSelectionModel().setSelectionInterval(0, 0);
			updateAddressSelection();
		}
	}

	public static void main(String... args) {
		Database.initTest();

		WalletService service = new WalletService();
		String seed = service.generateSeed();
		Wallet wallet = service.createNewWallet("test", seed);

		service.allocateNextPaymentAddress(wallet);
		service.allocateNextPaymentAddress(wallet);
		service.allocateNextPaymentAddress(wallet);

		ReceivingPanel panel = new ReceivingPanel(new WalletService());
		panel.setWallet(wallet);

		WindowUtils.launchTestWindow(panel);
	}
}

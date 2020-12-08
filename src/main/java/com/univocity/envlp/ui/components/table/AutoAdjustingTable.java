package com.univocity.envlp.ui.components.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.Date;

public class AutoAdjustingTable extends JTable {

	private TableColumnAdjuster tca = new TableColumnAdjuster(this);

	{
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowHeight(getFont().getSize() * 3);
		setShowGrid(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JTableHeader header = getTableHeader();
		header.setDefaultRenderer(new TableHeaderRenderer(header.getDefaultRenderer()));

		this.setDefaultRenderer(String.class, new DefaultCellRenderer());
		this.setDefaultRenderer(Long.class, new DefaultCellRenderer());
		this.setDefaultRenderer(Integer.class, new DefaultCellRenderer());
		this.setDefaultRenderer(Date.class, new DateTimeCellRenderer());
		this.setDefaultRenderer(LocalDateTime.class, new DateTimeCellRenderer());
		this.setDefaultRenderer(BigDecimal.class, new MoneyCellRenderer(6));
		this.getTableHeader().setReorderingAllowed(false);

		addPropertyChangeListener("font", evt -> {
			Font f = getFont();
			if (f != null) {
				setRowHeight((f.getSize() * 3));
			}
		});
	}

	public AutoAdjustingTable() {
	}

	public AutoAdjustingTable(TableModel dm) {
		super(dm);
	}

	public AutoAdjustingTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public AutoAdjustingTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public AutoAdjustingTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public AutoAdjustingTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	public AutoAdjustingTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	private boolean adjusting;

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component out = super.prepareRenderer(renderer, row, column);

		if (!adjusting) {
			adjusting = true;
			SwingUtilities.invokeLater(() -> {
				tca.adjustColumns();
				adjusting = false;
			});
		}

		return out;
	}

	public void enableCopyFromColumn(int columnIndex){
		getColumnModel().getSelectionModel().addListSelectionListener((e) -> setColumnSelectionInterval(0, columnIndex));
		getActionMap().put("copy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String paymentAddress = getModel().getValueAt(getSelectedRow(), columnIndex).toString();
				StringSelection stringSelection = new StringSelection(paymentAddress);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
			}
		});
	}
}



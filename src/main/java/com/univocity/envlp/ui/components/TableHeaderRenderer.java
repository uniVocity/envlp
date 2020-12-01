package com.univocity.envlp.ui.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TableHeaderRenderer implements TableCellRenderer {

	private TableCellRenderer delegate;

	public TableHeaderRenderer(TableCellRenderer delegate) {
		this.delegate = delegate;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		}
		return c;
	}
}
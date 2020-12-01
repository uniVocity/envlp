package com.univocity.freecommerce.ui.components;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class DefaultCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
		Component out = super.getTableCellRendererComponent(tab, value, isSelected, hasFocus, r, c);

		if (value instanceof Number) {
			setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return out;
	}
}


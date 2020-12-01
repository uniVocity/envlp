package com.univocity.envlp.ui.components;

import javax.swing.*;
import java.awt.*;
import java.text.*;

public class MoneyCellRenderer extends DefaultCellRenderer {

	private final NumberFormat numberFormat;

	public MoneyCellRenderer(int decimals) {
		numberFormat = NumberFormat.getCurrencyInstance();
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumFractionDigits(decimals);

		DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
		decimalFormatSymbols.setCurrencySymbol("");
		((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
		if (value instanceof Number) {
			value = numberFormat.format(value);
		}
		return super.getTableCellRendererComponent(tab, value, isSelected, hasFocus, r, c);
	}
}
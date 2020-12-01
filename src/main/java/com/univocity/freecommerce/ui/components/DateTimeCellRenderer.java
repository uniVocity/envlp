package com.univocity.freecommerce.ui.components;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class DateTimeCellRenderer extends DefaultCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
		if(value instanceof Date){
			value = LocalDateTime.ofInstant(((Date)value).toInstant(), ZoneId.systemDefault());
		}
		if (value instanceof LocalDateTime) {
			value = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
		}
		return super.getTableCellRendererComponent(tab, value, isSelected, hasFocus, r, c);
	}
}
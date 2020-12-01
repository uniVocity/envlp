package com.univocity.envlp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.util.*;
import java.util.List;

public class LinkLabel extends TitleLabel {

	private final List<Runnable> onClickListeners = new ArrayList<>();

	{
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				removePropertyChangeListener("font", listener);
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				Font font = getFont();
				Map attributes = font.getAttributes();
				attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
				setFont(font.deriveFont(attributes));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				Font font = getFont();
				Map attributes = font.getAttributes();
				attributes.put(TextAttribute.UNDERLINE, -1);
				setFont(font.deriveFont(attributes));
				addPropertyChangeListener("font", listener);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				onClickListeners.forEach(Runnable::run);
			}
		});
	}

	public LinkLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public LinkLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public LinkLabel(String text) {
		super(text);
	}

	public LinkLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public LinkLabel(Icon image) {
		super(image);
	}

	public LinkLabel() {
	}

	@Override
	protected int getIncrement() {
		return 1;
	}

	@Override
	protected int getStyle() {
		return Font.BOLD;
	}

	public void addOnClickListener(Runnable onClick){
		this.onClickListeners.add(onClick);
	}
}

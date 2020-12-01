package com.univocity.envlp.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.*;

public class TitleLabel extends JLabel {

	protected final PropertyChangeListener listener = evt -> onFontUpdate(evt);

	{
		setFont(getFont().deriveFont(getStyle(), getFont().getSize() + getIncrement()));
		addPropertyChangeListener("font", listener);
	}

	public TitleLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public TitleLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public TitleLabel(String text) {
		super(text);
	}

	public TitleLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public TitleLabel(Icon image) {
		super(image);
	}

	public TitleLabel() {
	}

	protected int getIncrement() {
		return 4;
	}

	protected int getStyle() {
		return Font.BOLD;
	}

	protected void onFontUpdate(PropertyChangeEvent event) {
		Font f = (Font) event.getNewValue();
		removePropertyChangeListener("font", listener);
		setFont(f.deriveFont(getStyle(), f.getSize() + getIncrement()));
		addPropertyChangeListener("font", listener);
	}
}

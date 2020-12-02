package com.univocity.envlp.ui.components.labels;

import javax.swing.*;
import java.awt.*;

public class PlainTextLabel extends TitleLabel {

	public PlainTextLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public PlainTextLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public PlainTextLabel(String text) {
		super(text);
	}

	public PlainTextLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public PlainTextLabel(Icon image) {
		super(image);
	}

	public PlainTextLabel() {
	}

	@Override
	protected int getIncrement() {
		return 0;
	}

	@Override
	protected int getStyle() {
		return Font.PLAIN;
	}
}

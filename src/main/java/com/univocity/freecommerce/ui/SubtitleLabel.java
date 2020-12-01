package com.univocity.freecommerce.ui;

import javax.swing.*;

public class SubtitleLabel extends TitleLabel {
	public SubtitleLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public SubtitleLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public SubtitleLabel(String text) {
		super(text);
	}

	public SubtitleLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public SubtitleLabel(Icon image) {
		super(image);
	}

	public SubtitleLabel() {
	}

	@Override
	protected int getIncrement() {
		return 2;
	}
}

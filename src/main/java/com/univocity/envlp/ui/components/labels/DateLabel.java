package com.univocity.envlp.ui.components.labels;

import javax.swing.*;
import java.awt.*;

public class DateLabel extends TitleLabel {
	public DateLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public DateLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public DateLabel(String text) {
		super(text);
	}

	public DateLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public DateLabel(Icon image) {
		super(image);
	}

	public DateLabel() {
	}

	protected int getIncrement(){
		return -2;
	}

	protected int getStyle(){
		return Font.PLAIN;
	}
}

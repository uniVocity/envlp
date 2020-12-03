package com.univocity.envlp.ui.components;

import javax.swing.*;

public class SpendingPasswordField extends JPasswordField {

	public SpendingPasswordField() {
		setColumns(20);
		setTransferHandler(null);
	}


	@Override
	public void copy() {
	}

	@Override
	public void paste() {
	}

	@Override
	public void cut() {
	}
}

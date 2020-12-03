package com.univocity.envlp.ui.components;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.*;

public class SpendingPasswordField extends JPasswordField {

	private final char echoChar;
	private final Consumer<String> textInputConsumer;

	public SpendingPasswordField() {
		this(null);
	}

	public SpendingPasswordField(Consumer<String> textInputConsumer) {
		setColumns(20);
		putClientProperty("JPasswordField.cutCopyAllowed", true);
		echoChar = getEchoChar();
		this.textInputConsumer = textInputConsumer;

		if (textInputConsumer != null) {
			addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					activateConsumer();
				}

				@Override
				public void keyPressed(KeyEvent e) {
					activateConsumer();
				}

				@Override
				public void keyReleased(KeyEvent e) {
					activateConsumer();
				}
			});
		}
	}

	private void activateConsumer() {
		textInputConsumer.accept(new String(getPassword()));
	}

	public void displayPassword() {
		setEchoChar('\u0000');
	}

	public void hidePassword() {
		setEchoChar(echoChar);
	}
}

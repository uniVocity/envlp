package com.univocity.freecommerce.ui.workflow;

import com.univocity.cardano.wallet.common.*;
import com.univocity.freecommerce.ui.*;
import com.univocity.freecommerce.wallet.*;

import javax.swing.*;
import java.awt.*;

public class SeedInputPanel extends WorkflowPanel {

	private JPanel seedPanel;
	private JTextArea seedTextArea;
	private String error;

	public SeedInputPanel() {

	}

	private JPanel getSeedPanel() {
		if (seedPanel == null) {
			seedPanel = new JPanel(new BorderLayout(0, 10));
			seedPanel.add(new SubtitleLabel("Please type in your 24 words seed phrase:"), BorderLayout.NORTH);
			seedPanel.add(getSeedTextArea(), BorderLayout.CENTER);
		}
		return seedPanel;
	}

	private JTextArea getSeedTextArea() {
		if (seedTextArea == null) {
			seedTextArea = new JTextArea(4, 12);
			seedTextArea.setWrapStyleWord(true);
			seedTextArea.setLineWrap(true);
			seedTextArea.setAutoscrolls(false);
		}
		return seedTextArea;
	}

	@Override
	protected String getInputErrorMessage() {
		return error;
	}

	@Override
	protected JComponent getInputComponent() {
		return getSeedPanel();
	}

	@Override
	protected String getTitle() {
		return "Your wallet seed phrase";
	}

	@Override
	protected String getDetails() {
		return "<p>&#8226 Please provide your <b>24 words</b> seed phrase to proceed.</p>" +
				"<p>&#8226 An incorrect seed may cause you to restore the <b>wrong</b> wallet.</p>" +
				"<p>&#8226 Ensure <b>only you</b> have access to this phrase before<br>continuing. It won't be stored anywhere.</p>";
	}

	@Override
	protected Object getValue() {
		error = null;
		String seed = Seed.cleanSeedPhrase(getSeedTextArea().getText());
		if (seed.isEmpty()) {
			error = "Seed phrase cannot be blank";
			return null;
		} else {
			String[] words = seed.split(" ");
			if (words.length != 24) {
				error = "Seed phrase must have 24 words instead of " + words.length;
				return null;
			}

			String privateKey = new WalletService().getAddressManager().generatePrivateKey(seed);
			if (privateKey == null) {
				error = "Invalid seed phrase";
				return null;
			}
		}

		return seed;
	}

	@Override
	public void activate() {
		clear();
	}

	@Override
	protected void clearFields() {
		getSeedTextArea().setText("");
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new SeedInputPanel());
	}
}

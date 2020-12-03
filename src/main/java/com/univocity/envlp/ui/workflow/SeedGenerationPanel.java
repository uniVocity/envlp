package com.univocity.envlp.ui.workflow;

import com.univocity.cardano.wallet.common.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.*;
import com.univocity.envlp.ui.components.labels.*;
import com.univocity.envlp.wallet.*;

import javax.swing.*;
import java.awt.*;

public class SeedGenerationPanel extends WorkflowPanel {

	private JPanel seedPanel;
	private JTextArea seedTextArea;

	public SeedGenerationPanel() {

	}

	private JPanel getSeedPanel() {
		if (seedPanel == null) {
			seedPanel = new JPanel(new BorderLayout(0, 10));
			seedPanel.add(new SubtitleLabel("This is your 24 words seed phrase:"), BorderLayout.NORTH);
			seedPanel.add(getSeedTextArea(), BorderLayout.CENTER);
		}
		return seedPanel;
	}

	private JTextArea getSeedTextArea() {
		if (seedTextArea == null) {
			seedTextArea = new NoClipboardTextArea(4, 12);
			seedTextArea.setEditable(false);
			seedTextArea.setWrapStyleWord(true);
			seedTextArea.setAutoscrolls(false);
		}
		return seedTextArea;
	}

	private void generateSeedPhrase() {
		String seed = new WalletService().getAddressManager().generateSeed();
		String[] words = seed.split(" ");

		StringBuilder tmp = new StringBuilder();
		for (int i = 1; i <= words.length; i++) {
			tmp.append(words[i - 1]);
			if (i % 6 == 0) {
				tmp.append('\n');
			} else {
				tmp.append(' ');
			}
		}
		getSeedTextArea().setText(tmp.toString());
	}

	@Override
	protected String getInputErrorMessage() {
		return null;
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
		return "<p>&#8226 Make sure you <b>never lose it</b>, otherwise any<br>funds associated with this seed will be lost forever.</p>" +
				"<p>&#8226 Anyone who knows this seed phrase will have<br><b>unrestricted access</b> to the funds.</p>" +
				"<p>&#8226 Ensure <b>only you</b> have access to this phrase<br>before continuing. It won't be stored anywhere.</p>";
	}

	@Override
	protected String getValue() {
		return Seed.cleanSeedPhrase(getSeedTextArea().getText());
	}

	@Override
	public void activate() {
		if (getSeedTextArea().getText().isEmpty()) {
			generateSeedPhrase();
		}
	}

	@Override
	protected void clearFields() {
		getSeedTextArea().setText("");
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new SeedGenerationPanel());
	}
}

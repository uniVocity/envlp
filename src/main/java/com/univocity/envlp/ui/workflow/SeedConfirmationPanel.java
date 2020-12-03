package com.univocity.envlp.ui.workflow;

import com.univocity.cardano.wallet.common.*;
import org.slf4j.*;

import java.util.*;

public class SeedConfirmationPanel extends SeedInputPanel {
	private static final Logger log = LoggerFactory.getLogger(SeedConfirmationPanel.class);

	private final SeedGenerationPanel seedGenerationPanel;

	public SeedConfirmationPanel(SeedGenerationPanel seedGenerationPanel) {
		super(null);
		this.seedGenerationPanel = seedGenerationPanel;
	}

	@Override
	protected String getTitle() {
		return "Confirm your new wallet's seed phrase";
	}

	@Override
	protected String getDetails() {
		return "<p>&#8226 Please confirm your <b>24 words</b> seed phrase to proceed.</p>" +
				"<p>&#8226 Make sure you enter all words in the <b>correct order</b>.</p>" +
				"<p>&#8226 <b>Don't lose</b> your seed phrase! It's the only way to recover your wallet.</p>";
	}

	@Override
	protected String getValue() {
		String generated = seedGenerationPanel.getValue();
		String confirmation = super.getValue();

		if (confirmation != null && generated != null) {
			try {
				List<String> generatedWords = Seed.toValidatedMnemonicList(generated, 24);
				List<String> confirmationWords = Seed.toValidatedMnemonicList(confirmation, 24);

				if (generatedWords.size() != confirmationWords.size()) {
					error = "Confirmation phrase doesn't match your wallet seed phrase";
					return null;
				}

				for (int i = 0; i < generatedWords.size(); i++) {
					if (!generatedWords.get(i).equals(confirmationWords.get(i))) {
						error = "Confirmation phrase doesn't match your wallet seed phrase";
						return null;
					}
				}

			} catch (InvalidMnemonicException e) {
				error = e.getMessage();
				return null;
			} catch (Exception e) {
				log.error("Error processing seed phrase confirmation", e);
				error = "Internal error: " + e.getMessage();
				return null;
			}
		} else {
			return null;
		}

		return generated;
	}
}

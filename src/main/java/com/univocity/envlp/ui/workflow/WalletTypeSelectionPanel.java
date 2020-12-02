package com.univocity.envlp.ui.workflow;

import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.labels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WalletTypeSelectionPanel extends WorkflowPanel {

	private JPanel selectionPanel;

	private JPanel walletTypePanel;
	private ButtonGroup walletType;
	private JRadioButton shelley;
	private JRadioButton byron;
	private JRadioButton none;
	private String type;

	public static final String BYRON = "BYRON";
	public static final String SHELLEY = "SHELLEY";

	public WalletTypeSelectionPanel() {
	}

	private JPanel getSelectionPanel() {
		if (selectionPanel == null) {
			selectionPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.weightx = 1.0;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.insets = new Insets(10, 0, 10, 0);

			c.gridy = 0;
			selectionPanel.add(new SubtitleLabel("Select the kind of wallet you want to restore"), c);

			c.gridy = 1;
			selectionPanel.add(getWalletTypePanel(), c);

			walletTypeUpdated(null);
		}
		return selectionPanel;
	}


	private JPanel getWalletTypePanel() {
		if (walletTypePanel == null) {
			walletTypePanel = new JPanel(new GridLayout(2, 1, 7, 7));
			walletType = new ButtonGroup();
			walletTypePanel.add(shelley = getOption("Shelley wallet"));
			walletTypePanel.add(byron = getOption("Legacy Byron wallet"));
			none = getOption("None"); //invisible
		}
		return walletTypePanel;
	}

	private JRadioButton getOption(String label) {
		JRadioButton out = new JRadioButton(label);
		out.addActionListener(this::walletTypeUpdated);
		walletType.add(out);
		return out;
	}

	private void walletTypeUpdated(ActionEvent actionEvent) {
		if (byron.isSelected()) {
			type = BYRON;
		} else if (shelley.isSelected()) {
			type = SHELLEY;
		} else {
			type = null;
		}
	}

	@Override
	protected String getInputErrorMessage() {
		return "Please select the type of wallet you are restoring to proceed";
	}

	@Override
	protected JComponent getInputComponent() {
		return getSelectionPanel();
	}

	@Override
	protected String getTitle() {
		return "Wallet type";
	}

	@Override
	protected String getDetails() {
		return "<p>Please provide the type of wall you are restoring.</p><br/>" +
				"<p>&#8226 Notice Byron wallets can't be used for staking.</p>" +
				"<p>&#8226 You can migrate to a Shelley wallet later on.</p>";
	}

	@Override
	protected String getValue() {
		return type;
	}

	@Override
	public void activate() {

	}

	@Override
	protected void clearFields() {
		byron.setSelected(false);
		shelley.setSelected(false);
		none.setSelected(true);
		type = null;
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new WalletTypeSelectionPanel());
	}
}

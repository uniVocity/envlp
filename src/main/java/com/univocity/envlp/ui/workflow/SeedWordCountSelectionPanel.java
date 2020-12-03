package com.univocity.envlp.ui.workflow;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.labels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.univocity.envlp.ui.workflow.WalletTypeSelectionPanel.*;

public class SeedWordCountSelectionPanel extends WorkflowPanel {

	private JPanel selectionPanel;

	private ButtonGroup wordCountGroup;
	private JRadioButton _12Words;
	private JRadioButton _15Words;
	private JRadioButton _24Words;
	private JRadioButton _27Words;
	private JRadioButton none;

	private JPanel seedWordCountPanel;
	private Integer wordCount;

	private final WalletTypeSelectionPanel walletTypeSelectionPanel;

	public SeedWordCountSelectionPanel(WalletTypeSelectionPanel walletTypeSelectionPanel) {
		this.walletTypeSelectionPanel = walletTypeSelectionPanel;
	}

	private JPanel getSelectionPanel() {
		if (selectionPanel == null) {
			selectionPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.weightx = 1.0;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.insets = new Insets(10, 0, 10, 0);

			c.weighty = 1.0;
			c.gridy = 0;
			selectionPanel.add(new SubtitleLabel("Select the number of words your seed phrase has"), c);
			c.gridy = 2;
			selectionPanel.add(getSeedWordCountPanel(), c);
		}
		return selectionPanel;
	}

	private JPanel getSeedWordCountPanel() {
		if (seedWordCountPanel == null) {
			seedWordCountPanel = new JPanel(new GridLayout(4, 1, 7, 7));
			wordCountGroup = new ButtonGroup();

			_12Words = getOption("<html>12 words <i>(Daedalus)</i></html>");
			_15Words = getOption("<html>15 words <i>(Yoroi)</i></html>");
			_24Words = getOption("<html>24 words <i>(Default)</i></html>");
			_27Words = getOption("<html>27 words <i>(Paper wallet)</i></html>");
			none = getOption("None"); //invisible
		}
		return seedWordCountPanel;
	}

	private JRadioButton getOption(String label) {
		JRadioButton out = new JRadioButton(label);
		out.addActionListener(this::selectionUpdated);
		wordCountGroup.add(out);
		return out;
	}

	private void selectionUpdated(ActionEvent e) {
		if (_12Words.isSelected()) {
			wordCount = 12;
		} else if (_15Words.isSelected()) {
			wordCount = 15;
		} else if (_24Words.isSelected()) {
			wordCount = 24;
		} else if (_27Words.isSelected()) {
			wordCount = 27;
		} else {
			wordCount = null;
		}
	}

	@Override
	protected String getInputErrorMessage() {
		return "Please select the number of words in your seed phrase to proceed";
	}

	@Override
	protected JComponent getInputComponent() {
		return getSelectionPanel();
	}

	@Override
	protected String getTitle() {
		return "Seed phrase word count";
	}

	@Override
	protected String getDetails() {
		return "";
	}

	@Override
	protected Integer getValue() {
		return wordCount;
	}

	public AddressStyle getWalletType(){
		return walletTypeSelectionPanel.getValue();
	}

	@Override
	public void activate() {
		AddressStyle walletType = getWalletType();
		seedWordCountPanel.removeAll();

		if (AddressStyle.Shelley.equals(walletType)) {
			seedWordCountPanel.add(_24Words);
			seedWordCountPanel.add(_15Words);

		} else if (AddressStyle.Byron.equals(walletType)) {
			seedWordCountPanel.add(_12Words);
			seedWordCountPanel.add(_15Words);
			seedWordCountPanel.add(_27Words);
		} else {
			throw new IllegalStateException("Unknown wallet type: " + walletType);
		}
		revalidate();
		repaint();
	}

	@Override
	protected void clearFields() {
		none.setSelected(true);

		wordCount = null;
	}
}

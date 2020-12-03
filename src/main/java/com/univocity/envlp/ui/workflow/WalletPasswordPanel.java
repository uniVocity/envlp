package com.univocity.envlp.ui.workflow;

import com.univocity.envlp.ui.*;
import com.univocity.envlp.ui.components.*;
import org.slf4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WalletPasswordPanel extends WorkflowPanel {

	private static final Logger log = LoggerFactory.getLogger(WalletPasswordPanel.class);

	private JPanel fieldsPanel;
	private SpendingPasswordField spendingPasswordTxt;
	private SpendingPasswordField passwordConfirmationTxt;

	private String error;
	private JCheckBox showPasswordChk;

	public WalletPasswordPanel() {

	}

	private JPanel getFieldsPanel() {
		if (fieldsPanel == null) {
			fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
			fieldsPanel.add(new JLabel("Spending password"));
			fieldsPanel.add(getSpendingPasswordTxt());

			fieldsPanel.add(new JLabel("Confirmation"));
			fieldsPanel.add(getPasswordConfirmationTxt());

			fieldsPanel.add(new JPanel());
			fieldsPanel.add(showPasswordChk = new JCheckBox("Display password"));
			showPasswordChk.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					getSpendingPasswordTxt().displayPassword();
					getPasswordConfirmationTxt().displayPassword();
				} else {
					getSpendingPasswordTxt().hidePassword();
					getPasswordConfirmationTxt().hidePassword();
				}
			});
		}
		return fieldsPanel;
	}

	private SpendingPasswordField getSpendingPasswordTxt() {
		if (spendingPasswordTxt == null) {
			spendingPasswordTxt = new SpendingPasswordField(this::validateOnInput);
		}
		return spendingPasswordTxt;
	}

	private SpendingPasswordField getPasswordConfirmationTxt() {
		if (passwordConfirmationTxt == null) {
			passwordConfirmationTxt = new SpendingPasswordField(this::validateOnInput);
		}
		return passwordConfirmationTxt;
	}

	private void validateOnInput(String password){
		getValue();
		displayError();
	}

	@Override
	protected String getInputErrorMessage() {
		return error;
	}

	@Override
	protected JComponent getInputComponent() {
		return getFieldsPanel();
	}

	@Override
	protected String getTitle() {
		return "Spending password";
	}

	@Override
	protected String getDetails() {
		return "<p>&#8226 Keep your wallet secure by setting a spending password.</p>" +
				"<p>&#8226 The password needs to be <b>at least 10</b> characters long.</p>" +
				"<p>&#8226 Password and its confirmation must match.</p>";
	}

	@Override
	public void activate() {
	}

	@Override
	protected void clearFields() {
		getSpendingPasswordTxt().setText("");
		getPasswordConfirmationTxt().setText("");
	}

	public String getSpendingPassword() {
		String spendingPassword = getValue();
		if (spendingPassword == null) {
			log.warn(error);
		}
		return spendingPassword;
	}

	@Override
	protected String getValue() {
		error = null;
		String spendingPassword = new String(getSpendingPasswordTxt().getPassword());
		if (spendingPassword.length() < 10) {
			error = "Spending password is too short (" + spendingPassword.length() + " characters)";
			return null;
		} else {
			String confirmation = new String(getPasswordConfirmationTxt().getPassword());
			if (confirmation.length() < 10) {
				error = "Confirmation password is too short (" + confirmation.length() + " characters)";
				return null;
			}
			if (!spendingPassword.equals(confirmation)) {
				error = "Spending password and confirmation are not the same";
				return null;
			}
		}
		return spendingPassword;
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new WalletPasswordPanel());
	}
}

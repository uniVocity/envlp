package com.univocity.envlp.ui.workflow;

import com.univocity.envlp.ui.*;
import com.univocity.envlp.wallet.*;
import org.apache.commons.lang3.*;
import org.slf4j.*;

import javax.swing.*;
import java.awt.*;

public class WalletSetupPanel extends WorkflowPanel {

	private static final Logger log = LoggerFactory.getLogger(WalletSetupPanel.class);

	private JPanel fieldsPanel;
	private JTextField walletNameTxt;
	private String error;

	public WalletSetupPanel() {

	}

	private JPanel getFieldsPanel() {
		if (fieldsPanel == null) {
			fieldsPanel = new JPanel(new FlowLayout());
			fieldsPanel.add(new JLabel("Wallet name"));
			fieldsPanel.add(getWalletNameTxt());
		}
		return fieldsPanel;
	}

	private JTextField getWalletNameTxt() {
		if (walletNameTxt == null) {
			walletNameTxt = new JTextField();
			walletNameTxt.setColumns(20);
		}
		return walletNameTxt;
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
		return "Wallet details";
	}

	@Override
	protected String getDetails() {
		return "<p>Please fill in your wallet details.</p><p>Wallet names must be unique.</p>";
	}

	@Override
	public void activate() {
	}

	@Override
	protected void clearFields() {
		getWalletNameTxt().setText("");
	}

	public String getWalletName(){
		String walletName = getValue();
		if(walletName == null){
			log.warn(error);
		}
		return walletName;
	}

	@Override
	protected String getValue() {
		error = null;
		String walletName = getWalletNameTxt().getText();
		if (StringUtils.isBlank(walletName)) {
			error = "Wallet name cannot be blank";
			return null;
		} else {
			walletName = walletName.trim();
			Wallet wallet = new WalletDAO().getWalletByName(walletName);
			if (wallet != null) {
				error = "A wallet named '" + walletName + "' already exists.";
				return null;
			}
		}
		return walletName;
	}

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new WalletSetupPanel());
	}
}

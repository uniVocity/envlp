package com.univocity.freecommerce.ui.workflow;


import com.github.weisj.darklaf.components.*;
import com.univocity.freecommerce.ui.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

abstract class WorkflowPanel extends JPanel {

	private JLabel detailsLabel;

	private JPanel inputPanel;
	private JPanel inputRow;
	private JPanel detailsPanel;
	private JLabel errorLabel;
	private JScrollPane detailsPanelScroll;

	public WorkflowPanel() {
		this.setLayout(new BorderLayout(5, 5));

		this.add(new OverlayScrollPane(new JScrollPane(getInputPanel())), BorderLayout.CENTER);
	}

	private JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setHorizontalAlignment(JLabel.CENTER);
			hideError();
		}
		return errorLabel;
	}

	private void hideError() {
		errorLabel.setText(" ");
		errorLabel.setForeground(new Color(255, 255, 255, 0));
		errorLabel.setBackground(new Color(255, 255, 255, 0));
	}

	private void displayError() {
		errorLabel.setText(getInputErrorMessage());
		errorLabel.setForeground(Color.RED);
		errorLabel.setBackground(new Color(125, 0, 0, 125));
	}

	protected abstract String getInputErrorMessage();

	private JPanel getInputPanel() {
		if (inputPanel == null) {
			inputPanel = new JPanel(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.CENTER;
			inputPanel.add(getInputRow(), c);

			c.gridy = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTH;
			c.insets = new Insets(0, 20, 0, 0);
			inputPanel.add(getErrorLabel(), c);

			c.gridy = 0;
			c.gridx = 1;
			c.gridheight = 2;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(0, 0, 0, 0);
			inputPanel.add(getDetailsPanel(), c);
		}
		return inputPanel;
	}

	protected JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));

			JPanel p = new JPanel(new BorderLayout());
			if (!getDetails().isEmpty()) {
				p.add(getDetailsPanelScroll(), BorderLayout.CENTER);
				p.setBorder(BorderFactory.createLoweredBevelBorder());
			}

			detailsPanel.add(p);
		}
		return detailsPanel;
	}

	protected JScrollPane getDetailsPanelScroll() {
		if (detailsPanelScroll == null) {
			detailsPanelScroll = new JScrollPane(getDetailsLabel());
		}
		return detailsPanelScroll;
	}

	protected JPanel getInputRow() {
		if (inputRow == null) {
			inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

			JComponent input = getInputComponent();
			input.setBorder(new EmptyBorder(20, 10, 20, 10));

			inputRow.add(input);
			inputRow.setBorder(new EmptyBorder(20, 10, 0, 0));
		}
		return inputRow;
	}

	protected abstract JComponent getInputComponent();

	protected abstract String getTitle();

	protected abstract String getDetails();

	protected abstract Object getValue();

	protected abstract void clearFields();

	public void clear(){
		hideError();
		clearFields();
	}

	public boolean validateInput() {
		if (getValue() == null) {
			displayError();
			return false;
		}
		hideError();
		return true;
	}

	protected JLabel getDetailsLabel() {
		if (detailsLabel == null) {
			detailsLabel = new JLabel();
			detailsLabel.setBorder(new EmptyBorder(20, 20, 20, 20));

			setDetails(getDetails());
		}
		return detailsLabel;
	}

	protected void setDetails(String details) {
		getDetailsLabel().setText("<html>" + details + "</html>");
	}

	public abstract void activate();

	public static void main(String... args) {
		WindowUtils.launchTestWindow(new WorkflowPanel() {
			@Override
			protected JComponent getInputComponent() {
				return new JTextField(20);
			}

			@Override
			protected String getTitle() {
				return "A title";
			}

			@Override
			protected String getDetails() {
				return "<h2>Something</h2><br>blah1<b>ertert</b> asd.";
			}

			@Override
			protected Object getValue() {
				return null;
			}

			@Override
			protected String getInputErrorMessage() {
				return "";
			}

			@Override
			public void activate() {
			}

			@Override
			protected void clearFields() {

			}
		});
	}
}

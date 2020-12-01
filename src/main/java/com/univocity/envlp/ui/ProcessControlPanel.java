package com.univocity.envlp.ui;

import com.univocity.cardano.wallet.embedded.services.*;
import com.univocity.envlp.utils.*;

import javax.swing.*;
import java.awt.*;

public class ProcessControlPanel extends JPanel {

	private LogPanel logPanel;
	private JToggleButton processControlButton;
	private JPanel controlPanel;
	private JLabel uptimeLbl;
	private Timer timer;

	private ProcessLog processLog;

	private final ProcessManager processManager;

	public ProcessControlPanel(ProcessManager processManager) {
		super(new BorderLayout());
		this.processManager = processManager;

		this.add(getLogPanel(), BorderLayout.CENTER);
		this.add(getControlPanel(), BorderLayout.SOUTH);

		this.timer = new Timer(1000, e -> updateUptime());
	}

	private LogPanel getLogPanel() {
		if (logPanel == null) {
			logPanel = new LogPanel();
			processLog = new ProcessLog(logPanel);
			processManager.setOutputConsumer(processLog::append);
		}
		return logPanel;
	}

	private JPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(5, 5, 5, 5);
			controlPanel.add(new JLabel(processManager.getToolName() + " uptime:"), c);

			c.gridx = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			controlPanel.add(getUptimeLbl(), c);

			c.gridx = 2;
			c.weightx = 0;
			c.anchor = GridBagConstraints.EAST;

			controlPanel.add(getProcessControlButton(), c);
		}
		return controlPanel;
	}

	private JLabel getUptimeLbl() {
		if (uptimeLbl == null) {
			uptimeLbl = new JLabel();
		}
		return uptimeLbl;
	}

	public void startProcess() {
		if (!processManager.isProcessRunning()) {
			timer.start();
			processManager.startProcess();
		}
	}

	private void updateUptime() {
		if (processManager.isProcessRunning()) {
			getUptimeLbl().setText(TimeInterval.getFormattedDuration(processManager.getUptime()));
		} else {
			getUptimeLbl().setText("Stopped");
			processControlButton.setText("Start");
		}
	}

	private JToggleButton getProcessControlButton() {
		if (processControlButton == null) {
			processControlButton = new JToggleButton("Stop " + processManager.getToolName());
			processControlButton.setSelected(true);

			processControlButton.addActionListener((e) -> {
				if (!processControlButton.isSelected()) {
					processManager.stopProcess();
					uptimeLbl.setText("Stopped " + processManager.getToolName());
					processControlButton.setText("Start " + processManager.getToolName());
					processLog.append("\n!!! " + processManager.getToolName() + " stopped.\n");
					timer.stop();
				} else {
					processLog.append("\n>>> " + processManager.getToolName() + " starting.\n");
					processManager.restartProcess();
					processControlButton.setText("Stop " + processManager.getToolName());
					timer.start();
				}
			});
		}
		return processControlButton;
	}
}

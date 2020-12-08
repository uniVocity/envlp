package com.univocity.envlp.ui;

import com.univocity.cardano.wallet.builders.network.*;
import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.envlp.ui.components.labels.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.*;

public class EpochDetailsPanel extends JPanel {

	private final RemoteWalletServer server;
	private final Timer timer;
	private long epoch = -1;
	private long totalEpochSlots;

	private static final String EPOCH = "EPOCH";
	private static final String SYNC = "SYNC";

	private CardLayout cardLayout;
	private JPanel cards;

	private JPanel syncPanel;
	private PlainTextLabel syncLbl;

	private JPanel epochPanel;
	private PlainTextLabel epochLbl;
	private PlainTextLabel slotsLbl;
	private PlainTextLabel decentralizationLevelLbl;
	private PlainTextLabel epochDuration;

	public EpochDetailsPanel(RemoteWalletServer server) {
		super(new BorderLayout());
		this.add(getCardsPanel(), BorderLayout.CENTER);

		this.server = server;
		timer = new Timer(21_000, evt -> {
			server.network().information(this::updateStatus, error -> updateStatus(null));
		});
		timer.setInitialDelay(200);
		timer.start();
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	private JPanel getCardsPanel() {
		if (cards == null) {
			cardLayout = new CardLayout();
			cards = new JPanel(cardLayout);
			cards.add(getSyncPanel(), SYNC);
			cards.add(getEpochPanel(), EPOCH);
		}
		return cards;
	}

	private JPanel getEpochPanel() {
		if (epochPanel == null) {
			epochPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(0, 0, 0, 7);
			c.gridx = 0;
			epochPanel.add(new JLabel("Epoch:"), c);

			c.weightx = 0.0;
			c.gridx++;
			epochPanel.add(epochLbl = new PlainTextLabel(), c);

			c.gridx++;
			epochPanel.add(new JLabel(" Slot:"), c);

			c.gridx++;
			epochPanel.add(slotsLbl = new PlainTextLabel(), c);

			c.gridx++;
			epochPanel.add(new JLabel(" Ends in:"), c);

			c.gridx++;
			epochPanel.add(epochDuration = new PlainTextLabel(), c);

			c.gridx++;
			epochPanel.add(new JLabel(" Decentralization:"), c);

			c.gridx++;
			epochPanel.add(decentralizationLevelLbl = new PlainTextLabel(), c);
		}
		return epochPanel;
	}

	public JPanel getSyncPanel() {
		if (syncPanel == null) {
			syncPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			syncPanel.add(new JLabel("Status: "));
			syncPanel.add(syncLbl = new PlainTextLabel());
			updateSynchronizationStatus(null);
		}
		return syncPanel;
	}

	private void updateNetworkParameters() {
		NetworkParameters networkParameters = server.network().parameters();

		long totalSlots = networkParameters.epochLengthInSlots();
		int decentralizationLevel = (int) (Math.ceil(networkParameters.decentralizationPercentage() * 100.0));

		totalEpochSlots = totalSlots;
		decentralizationLevelLbl.setText(decentralizationLevel + "%");
	}

	private void updateStatus(NetworkInformation networkInformation) {
		if (networkInformation == null || networkInformation.synchronizationStatus() != SynchronizationStatus.READY) {
			cardLayout.show(cards, SYNC);
			updateSynchronizationStatus(networkInformation);
			return;
		}

		long epoch = networkInformation.networkEpoch();
		if (epoch == -1 || epoch != this.epoch) {
			updateNetworkParameters();
			this.epoch = epoch;
		}

		long currentSlot = networkInformation.nodeSlot();

		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime nextEpochStart = networkInformation.nextEpochStart();


		epochLbl.setText(String.valueOf(epoch));
		slotsLbl.setText(currentSlot + " / " + totalEpochSlots);

		Duration diff = Duration.between(currentTime, nextEpochStart);

		long days = (diff.toHours() / 24);
		long hours = diff.toHoursPart();
		long minutes = diff.toMinutesPart();
		StringBuilder time = new StringBuilder();
		if(days != 0) {
			append(time, "day", "%d", days, false);
		}
		if(hours != 0) {
			append(time, "hour", "%d", hours, false);
		}
		if(minutes != 0) {
			append(time, "minute", "%d", minutes, true);
		}
		epochDuration.setText(time.toString());

		cardLayout.show(cards, EPOCH);
	}

	private static void append(StringBuilder time, String description, String format, long value, boolean displayAlways) {
		if (value > 0 || displayAlways) {
			if (time.length() > 0) {
				if (displayAlways) {
					time.append(" ");
				} else {
					time.append(", ");
				}
			}
			time.append(String.format(format, value)).append(' ').append(description);
			if (value > 1) {
				time.append('s');
			}
		}
	}

	private void updateSynchronizationStatus(NetworkInformation networkInformation) {
		if (networkInformation == null) {
			syncLbl.setText("waiting for connection with cardano-node");
		} else if (networkInformation.synchronizationStatus() == SynchronizationStatus.NOT_RESPONDING) {
			syncLbl.setText("cardano-node not responding");
		} else if (networkInformation.synchronizationStatus() == SynchronizationStatus.SYNCING) {
			syncLbl.setText("blocks synced " + networkInformation.formattedSynchronizationProgressPercentage());
		}
	}

	public static void main(String... args) {
		RemoteWalletServer server = WalletServer.remote("localhost").connectToPort(3002);
		WindowUtils.launchTestWindow(new EpochDetailsPanel(server));
	}
}

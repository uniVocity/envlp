package com.univocity.freecommerce.ui.wallet.management;

import com.univocity.freecommerce.ui.*;
import com.univocity.freecommerce.wallet.*;
import org.slf4j.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;

public class WalletListEntry extends JToggleButton {

	private static final Logger log = LoggerFactory.getLogger(WalletListEntry.class);

	Wallet wallet;

	private JPanel getBalanceLabel(String balance) {
		JPanel out = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		out.setOpaque(false);
		JLabel balanceLbl = new JLabel(balance, SwingConstants.LEFT);
		JLabel symbolLbl = null;

		try {
			symbolLbl = new IconLabel("images/ada-logo.inline.png");
			symbolLbl.setBorder(new EmptyBorder(0, 5, 0, 0));
		} catch (Exception e) {
			log.warn("Error loading ada symbol image", e);
		}

		if (symbolLbl == null) {
			symbolLbl = new JLabel(" ADA");
		}

		out.add(balanceLbl);
		out.add(symbolLbl);
		return out;
	}

	public WalletListEntry(Wallet wallet) {
		this.wallet = wallet;
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(250, 90));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 25, 0, 25);
		c.gridy = 0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;

		JLabel name = new SubtitleLabel(wallet.getName());
		add(name, c);

		c.insets = new Insets(10, 25, 0, 25);
		c.gridy = 1;
		c.weighty = 1.0;
		add(getBalanceLabel("0.000000"), c);

		LocalDateTime date = wallet.getCreatedAt();
		String formattedDate = "";
		if (date != null) {
			formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm"));
		}
		c.insets = new Insets(5, 25, 10, 0);
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridx = 0;
		add(new DateLabel("Updated:"), c);
		c.insets = new Insets(5, 0, 10, 25);
		c.gridx = 1;
		add(new DateLabel(formattedDate), c);


		addPropertyChangeListener("font", evt -> {
			Font f = getFont();
			if (f != null) {
				int width = f.getSize() > 14 ? f.getSize() > 18 ? 450 : 350 : 250;
				setPreferredSize(new Dimension(width, (f.getSize() * 10)));
			}
		});
	}
}

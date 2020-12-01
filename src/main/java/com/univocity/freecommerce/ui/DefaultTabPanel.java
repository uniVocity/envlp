package com.univocity.freecommerce.ui;

import com.github.weisj.darklaf.*;
import com.univocity.freecommerce.utils.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class DefaultTabPanel {

	public static JPanel getTitlePanel(final JTabbedPane tabbedPane, final Component panel, String title, String pathToIcon, boolean closable) {
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		titlePanel.setOpaque(false);

		JLabel label = new JLabel(title);
		titlePanel.add(label);

		ImageIcon icon = Utils.getImageIcon(pathToIcon, 30, 30);
		if (icon != null) {
			label.setIcon(icon);
			label.setIconTextGap(10);
		}
		label.setBorder(BorderFactory.createEmptyBorder(5, icon != null ? 5 : 25, 5, closable ? 20 : 25));

		if (closable) {
			JButton closeButton = new JButton() {
				public void updateUI() {
				}

				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g.create();
					if (getModel().isPressed()) {
						g2.translate(1, 1);
					}
					g2.setStroke(new BasicStroke(1.5f));
					g2.setColor(Color.BLACK);
					if (getModel().isRollover()) {
						g2.setColor(Color.MAGENTA);
					}
					double sz = LafManager.getTheme().getFontSizeRule().getPercentage();
					int delta = Math.min(5, (int) Math.round(5.0 * ((100.0 / sz))));
					g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
					g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
					g2.dispose();
				}
			};

			closeButton.setOpaque(false);
			closeButton.setPreferredSize(new Dimension(17, 17));
			closeButton.setUI(new BasicButtonUI());
			closeButton.setToolTipText("Close " + title);
			closeButton.setFocusable(false);
			closeButton.setBorderPainted(false);
			closeButton.setRolloverEnabled(true);

			closeButton.addActionListener((e) -> tabbedPane.remove(panel));
			titlePanel.add(closeButton);
		}
		return titlePanel;
	}

}

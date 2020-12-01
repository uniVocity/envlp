package com.univocity.envlp.ui;

import com.github.weisj.darklaf.components.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.*;

public class LogPanel extends JPanel {

	private final JTextPane outputArea = new JTextPane() {
		public boolean getScrollableTracksViewportWidth() {
			return getUI().getPreferredSize(this).width <= getParent().getSize().width;
		}
	};

	private final BoundedRangeModel model;
	private boolean autoScroll = true;

	public LogPanel() {
		super(new BorderLayout());

		DefaultCaret caret = (DefaultCaret) outputArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		outputArea.setEditable(false);

		OverlayScrollPane scroll = new OverlayScrollPane(new JScrollPane(outputArea, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS));
		model = scroll.getVerticalScrollBar().getModel();

		scroll.getVerticalScrollBar().addAdjustmentListener(e -> {
			if (!model.getValueIsAdjusting()) {
				if (autoScroll) {
					model.setValue(model.getMaximum());
				}
			} else {
				autoScroll = autoScroll();
			}
		});
		scroll.addMouseWheelListener(e -> autoScroll = e.getWheelRotation() >= 0 && autoScroll());

		this.add(scroll, BorderLayout.CENTER);
	}

	private boolean autoScroll() {
		return model.getValue() + model.getExtent() == model.getMaximum();
	}

	public JTextPane getOutputArea() {
		return outputArea;
	}
}

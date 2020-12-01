package com.univocity.envlp.ui.components;

import com.univocity.envlp.utils.*;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JComponent {
	private Image image;

	public ImagePanel(String imagePath) {
		this.image = Utils.getImage(imagePath);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			int h = (int) (image.getHeight(this) / 1.5);
			int w = (int) (image.getWidth(this) / 1.4);
			g.drawImage(image, getWidth() - w, getHeight() - h, this);
		}
	}
}

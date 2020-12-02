package com.univocity.envlp.ui.components.labels;

import com.univocity.envlp.utils.*;

import javax.swing.*;
import java.awt.*;
import java.beans.*;

public class IconLabel extends TitleLabel {

	private final Image image;


	public IconLabel(String pathToImage) {
		this(pathToImage, LEADING);
	}

	public IconLabel(String pathToImage, int horizontalAlignment) {
		super("", horizontalAlignment);
		image = Utils.getImage(pathToImage);
		updateIcon();
	}

	@Override
	protected int getIncrement() {
		return 0;
	}

	protected int getStyle() {
		return Font.PLAIN;
	}

	private void updateIcon(){
		if(image != null) {
			Font font = getFont();
			if(font != null){
				FontMetrics metrics = getFontMetrics(font);
				int height = (int) ((font.getSize() * (metrics.getAscent() + metrics.getDescent()) / metrics.getAscent()) * 0.8);

				int[] widths = metrics.getWidths();
				int width = 0;
				for(int i = 0; i < widths.length; i++){
					if(width < widths[i]){
						width = widths[i];
					}
				}
				if(width == 0){
					width = height;
				}
				Image resized = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				setIcon(new ImageIcon(resized));
			}

		}
	}

	@Override
	protected void onFontUpdate(PropertyChangeEvent event) {
		super.onFontUpdate(event);
		updateIcon();
	}
}

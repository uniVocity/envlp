
package com.univocity.freecommerce.utils;

import com.univocity.freecommerce.ui.*;
import org.apache.commons.lang3.*;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Utils extends com.univocity.cardano.wallet.common.Utils {


	public static <T> void concatenate(StringBuilder out, String separator, T[] objects) {
		concatenate(out, separator, 0, objects);
	}

	public static <T> void concatenate(StringBuilder out, String separator, int stopIndex, T[] objects) {
		if (objects == null) {
			return;
		}

		if (separator == null) {
			separator = "";
		}

		if (stopIndex <= 0) {
			stopIndex = objects.length;
		}

		for (int i = 0; i < stopIndex; i++) {
			T obj = objects[i];
			if (obj != null) {
				out.append(obj);
				out.append(separator);
			}
		}

		if (objects.length > 0 && !"".equals(separator)) {
			removeSuffix(out, separator);
		}
	}

	public static void removeSuffix(StringBuilder str, String suffix) {
		int strLength = str.length();
		int sufLength = suffix.length();
		if (strLength > 0 && sufLength > 0) {
			if (str.toString().endsWith(suffix)) {
				str.delete(strLength - sufLength, strLength);
			}
		}
	}

	/**
	 * Returns an array of Object[] with 2 Object[] rows. The first has the keys and the second its values.
	 * Chosen keys and their values will appear at the end of the row. Elements are then ordered according with the map
	 *
	 * @param map        map to get keys and values from
	 * @param chosenKeys selected keys whose values will be at the end
	 * @param <K>        type of key in the given map
	 * @param <V>        type of value associated with the keys
	 *
	 * @return a bi-dimensional array with one row for keys and another for values.
	 */
	public static <K, V> Object[][] getValuesAndSelection(Map<K, V> map, Set<K> chosenKeys) {
		int mapSize = map.size();
		Object[][] out = new Object[][]{
				new Object[mapSize], //column names
				new Object[mapSize], //values
		};

		int valueStart = 0;
		int matchStart = map.size() - chosenKeys.size();

		for (Map.Entry<K, V> e : map.entrySet()) {
			K key = e.getKey();
			V value = e.getValue();

			if (chosenKeys.contains(key)) {
				out[0][matchStart] = key;
				out[1][matchStart] = value;
				matchStart++;
			} else {
				out[0][valueStart] = key;
				out[1][valueStart] = value;
				valueStart++;
			}
		}

		return out;
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static void switchToTab(JTabbedPane tabbedPane, String tabTitle, boolean closable, Supplier<Component> componentSupplier) {
		switchToTab(tabbedPane, tabTitle, null, closable, componentSupplier);
	}

	public static void switchToTab(JTabbedPane tabbedPane, String tabTitle, String pathToIcon, boolean closable, Supplier<Component> componentSupplier) {
		int index = tabbedPane.indexOfTab(tabTitle);
		if (index < 0) {
			Component c = componentSupplier.get();

			tabbedPane.addTab(tabTitle, c);
			tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(c), DefaultTabPanel.getTitlePanel(tabbedPane, c, tabTitle, pathToIcon, closable));
			index = tabbedPane.indexOfTab(tabTitle);
		}
		tabbedPane.setSelectedIndex(index);
	}

	public static ImageIcon getImageIcon(String resourcePath) {
		if(resourcePath == null){
			return null;
		}
		URL iconURL = Utils.class.getResource(resourcePath);
		if (iconURL == null) {
			iconURL = Utils.class.getResource("/" + resourcePath);
		}
		if (iconURL != null) {
			return new ImageIcon(iconURL);
		}
		return null;
	}

	public static ImageIcon getImageIcon(String resourcePath, int width, int height) {
		Image image = getImage(resourcePath, width, height);
		if (image != null) {
			return new ImageIcon(image);
		}
		return null;
	}

	public static Image getImage(String resourcePath, int width, int height) {
		Image image = getImage(resourcePath);
		if (image != null) {
			return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		}
		return null;
	}


	public static Image getImage(String resourcePath) {
		ImageIcon imageIcon = getImageIcon(resourcePath);
		if (imageIcon != null) {
			return imageIcon.getImage();
		}
		return null;
	}
}



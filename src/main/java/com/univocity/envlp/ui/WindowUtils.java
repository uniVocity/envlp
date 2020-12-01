package com.univocity.envlp.ui;

import com.univocity.envlp.database.*;

import javax.swing.*;
import java.awt.*;

public class WindowUtils {

	public static void launchTestWindow(JComponent component) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());

		frame.add(component, BorderLayout.CENTER);
		SwingUtilities.invokeLater(() -> frame.setVisible(true));
		Database.initTest();
	}
}

package com.univocity.envlp;

import javax.swing.*;
import java.awt.event.*;

public class MainMenu extends JMenuBar {

	private JMenu fileMenu;

	private JMenuItem exitMenuItem;
	private JMenuItem walletsMenuItem;
	private JMenuItem themeMenuItem;


	private JMenu logMenu;
	private JMenuItem viewApplicationLogMenuItem;
	private JMenuItem viewCardanoNodeLogMenuItem;
	private JMenuItem viewCardanoWalletLogMenuItem;

	private final Main main;

	public MainMenu(Main main) {
		this.main = main;
		add(getFileMenu());
		add(getLogMenu());
	}

	private JMenu getLogMenu() {
		if (logMenu == null) {
			logMenu = new JMenu("Logs");
			logMenu.setMnemonic(KeyEvent.VK_L);
			logMenu.add(getViewApplicationLogMenuItem());
			logMenu.add(getViewCardanoNodeLogMenuItem());
			logMenu.add(getViewCardanoWalletLogMenuItem());
		}
		return logMenu;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);

			fileMenu.add(getWalletsMenuItem());
			fileMenu.add(getThemeMenuItem());


			fileMenu.add(new JSeparator());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	private JMenuItem getWalletsMenuItem() {
		if (walletsMenuItem == null) {
			walletsMenuItem = new JMenuItem("Wallets");
			walletsMenuItem.setMnemonic(KeyEvent.VK_W);
			walletsMenuItem.addActionListener(e -> main.openWalletsTab());
		}
		return walletsMenuItem;
	}

	private JMenuItem getThemeMenuItem() {
		if (themeMenuItem == null) {
			themeMenuItem = new JMenuItem("Change theme");
			themeMenuItem.setMnemonic(KeyEvent.VK_T);
			themeMenuItem.addActionListener((e) -> main.changeTheme());
			themeMenuItem.setEnabled(main.themeManagerLoaded);
		}
		return themeMenuItem;
	}

	private JMenuItem getViewApplicationLogMenuItem() {
		if (viewApplicationLogMenuItem == null) {
			viewApplicationLogMenuItem = new JMenuItem("Application log");
			viewApplicationLogMenuItem.setMnemonic(KeyEvent.VK_A);
			viewApplicationLogMenuItem.addActionListener((e) -> main.openApplicationLogTab());
		}
		return viewApplicationLogMenuItem;
	}

	private JMenuItem getViewCardanoNodeLogMenuItem() {
		if (viewCardanoNodeLogMenuItem == null) {
			viewCardanoNodeLogMenuItem = new JMenuItem("Cardano node log");
			if(main.cardanoNodeControlPanel != null) {
				viewCardanoNodeLogMenuItem.setMnemonic(KeyEvent.VK_N);
				viewCardanoNodeLogMenuItem.addActionListener((e) -> main.openCardanoNodeLogTab());
			} else {
				viewCardanoNodeLogMenuItem.setEnabled(false);
			}
		}
		return viewCardanoNodeLogMenuItem;
	}

	private JMenuItem getViewCardanoWalletLogMenuItem() {
		if (viewCardanoWalletLogMenuItem == null) {
			viewCardanoWalletLogMenuItem = new JMenuItem("Cardano wallet log");
			if(main.cardanoWalletControlPanel != null){
				viewCardanoWalletLogMenuItem.setMnemonic(KeyEvent.VK_W);
				viewCardanoWalletLogMenuItem.addActionListener((e) -> main.openCardanoWalletLogTab());
			} else {
				viewCardanoWalletLogMenuItem.setEnabled(false);
			}
		}
		return viewCardanoWalletLogMenuItem;
	}


	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_X);
			exitMenuItem.addActionListener((e) -> System.exit(0));
		}
		return exitMenuItem;
	}
}

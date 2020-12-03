package com.univocity.envlp.ui.components.wordlist;

import com.univocity.cardano.wallet.common.*;
import com.univocity.envlp.ui.*;
import org.springframework.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WordList extends JList<String> {
	private final String[] words;
	private final DefaultListModel<String> model;
	final boolean selectWithSpaceBar;

	public WordList(Collection<String> words) {
		this(words.toArray(new String[0]));
	}

	public WordList(String... words) {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TreeSet<String> tmp = new TreeSet<>();
		Collections.addAll(tmp, words);
		this.selectWithSpaceBar = tmp.stream().anyMatch(w -> w.indexOf(' ') > 0);
		this.words = tmp.toArray(new String[0]);

		setModel(model = new DefaultListModel<>());
		model.addAll(tmp);

		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point p = new Point(e.getX(), e.getY());
				setSelectedIndex(locationToIndex(p));
			}
		});
	}

	public String getSelectedItem() {
		if (getSelectedIndex() >= 0) {
			return getSelectedValue();
		}
		if (model.getSize() > 0) {
			return model.get(0);
		}
		return null;
	}

	public boolean refreshWordList(String pattern) {
		model.removeAllElements();
		String firstMatch = null;
		if (!pattern.isEmpty()) {
			for (int i = 0, n = words.length; i < n; i++) {
				if (matches(words[i], pattern)) {
					model.addElement(words[i]);
					if (firstMatch == null) {
						firstMatch = words[i];
					}
				} else if (firstMatch != null) {
					break;
				}
			}
		}

		if(firstMatch != null){
			setSelectedIndex(0);
		}

		return firstMatch != null;
	}

	private boolean matches(String str1, String str2) {
		return StringUtils.startsWithIgnoreCase(str1, str2);
	}

	public static void main(String[] args) {
		WordList wordList = new WordList(Seed.englishMnemonicWords());

		JPanel p = new JPanel(new FlowLayout());
		JTextField txt = new JTextField(10);

		txt.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				wordList.refreshWordList(txt.getText());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				wordList.refreshWordList(txt.getText());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				wordList.refreshWordList(txt.getText());
			}
		});

		p.add(txt);
		p.add(new JScrollPane(wordList));
		p.add(new JButton("blah"));
		WindowUtils.launchTestWindow(p);
	}
}

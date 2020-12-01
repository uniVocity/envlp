package com.univocity.envlp.utils;

import ch.qos.logback.classic.*;
import com.github.weisj.darklaf.*;
import com.github.weisj.darklaf.theme.info.*;
import com.univocity.envlp.ui.*;
import org.apache.commons.lang3.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

import static ch.qos.logback.classic.Level.*;
import static com.github.weisj.darklaf.theme.info.ColorToneRule.*;

public class ProcessLog {

	private static final SimpleAttributeSet ERROR_ATT_LIGHT, WARN_ATT_LIGHT, INFO_ATT_LIGHT, ANY_ATT_LIGHT;
	private static final SimpleAttributeSet ERROR_ATT_DARK, WARN_ATT_DARK, INFO_ATT_DARK, ANY_ATT_DARK;

	static {
		// ERROR
		ERROR_ATT_LIGHT = getAttributeSet(new Color(153, 0, 0), true);
		ERROR_ATT_DARK = getAttributeSet(new Color(225, 90, 90), true);

		// WARN
		WARN_ATT_LIGHT = getAttributeSet(new Color(153, 76, 0), false);
		WARN_ATT_DARK = getAttributeSet(new Color(255, 153, 0), false);

		// INFO
		INFO_ATT_LIGHT = getAttributeSet(new Color(70, 70, 70), false);
		INFO_ATT_DARK = getAttributeSet(new Color(200, 200, 200), false);

		ANY_ATT_LIGHT = getAttributeSet(null, false);
		ANY_ATT_DARK = getAttributeSet(null, false);
	}

	static SimpleAttributeSet getAttributeSet(Color color, boolean bold) {
		SimpleAttributeSet out = new SimpleAttributeSet();
		out.addAttribute(StyleConstants.FontFamily, "Monospaced");
		out.addAttribute(StyleConstants.CharacterConstants.Bold, bold ? Boolean.TRUE : Boolean.FALSE);
		out.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.FALSE);
		if (color != null) {
			out.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(255, 255, 255));
		}
		return out;
	}

	private final JTextPane outputArea;

	public ProcessLog(LogPanel logPanel) {
		this.outputArea = logPanel.getOutputArea();
	}

	public void append(String logMsg) {
		Level level = ALL;
		if (logMsg.indexOf(":Notice:") > 0) {
			level = DEBUG;
		} else if (logMsg.indexOf(":Info:") > 0) {
			level = INFO;
		}
		append(logMsg, level);
	}

	protected final void append(String formattedMsg, Level level) {
		if (formattedMsg == null || formattedMsg.length() == 0) {
			return;
		}
		String tmp = StringUtils.truncate(formattedMsg, 1024);
		int length = tmp.length();
		if (length < formattedMsg.length()) {
			tmp += "...\n";
		} else if (length > 0 && tmp.charAt(length - 1) != '\n') {
			tmp += "\n";
		}
		String msg = tmp;
		SwingUtilities.invokeLater(() -> {
			Document doc = outputArea.getDocument();

			try {
				int lineLimit = 1000;
				int toErase = 200;
				if (doc.getDefaultRootElement().getElementCount() > lineLimit) {
					int end = getLineEndOffset(doc, toErase);
					removeOld(doc, end);
				}
				doc.insertString(doc.getLength(), msg, getLogMessageAttributeSet(level));
			} catch (BadLocationException e) {
				//do nothing
			}
		});
	}

	private SimpleAttributeSet getLogMessageAttributeSet(Level level) {
		ColorToneRule tone = LafManager.getTheme().getColorToneRule();
		switch (level.levelInt) {
			case ERROR_INT:
				return tone == DARK ? ERROR_ATT_DARK : ERROR_ATT_LIGHT;
			case WARN_INT:
				return tone == DARK ? WARN_ATT_DARK : WARN_ATT_LIGHT;
			case INFO_INT:
				return tone == DARK ? INFO_ATT_DARK : INFO_ATT_LIGHT;
			case DEBUG_INT:
			case TRACE_INT:
			default:
				return tone == DARK ? ANY_ATT_DARK : ANY_ATT_LIGHT;
		}
	}

	private int getLineCount(Document doc) {
		return doc.getDefaultRootElement().getElementCount();
	}

	private int getLineEndOffset(Document doc, int line) throws BadLocationException {
		int lineCount = getLineCount(doc);
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= lineCount) {
			throw new BadLocationException("No such line", doc.getLength() + 1);
		} else {
			Element map = doc.getDefaultRootElement();
			Element lineElem = map.getElement(line);
			int endOffset = lineElem.getEndOffset();
			// hide the implicit break at the end of the document
			return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
		}
	}

	private void removeOld(Document doc, int end) throws IllegalArgumentException {
		try {
			if (doc instanceof AbstractDocument) {
				((AbstractDocument) doc).replace(0, end, null, null);
			} else {
				doc.remove(0, end);
				doc.insertString(0, null, null);
			}
		} catch (BadLocationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}
package com.univocity.freecommerce.utils;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.*;
import com.univocity.freecommerce.*;

import java.util.*;

public class Appender extends AppenderBase<ILoggingEvent> {

	private PatternLayout patternLayout;

	private ProcessLog processLog;
	private List<ILoggingEvent> tmpEvents = new ArrayList<>();

	@Override
	public void start() {
		patternLayout = new PatternLayout();
		patternLayout.setContext(getContext());
		patternLayout.setPattern("%-5level - %msg%n");
		patternLayout.start();
		super.start();
	}

	@Override
	protected void append(ILoggingEvent event) {
		if (processLog == null) {
			if (Main.isRunning()) {
				processLog = new ProcessLog(Main.getInstance().getApplicationLogPanel());
				tmpEvents.forEach(this::append);
				tmpEvents.clear();
				tmpEvents = null;
			} else {
				tmpEvents.add(event);
				return;
			}
		}
		processLog.append(patternLayout.doLayout(event), event.getLevel());
	}
}
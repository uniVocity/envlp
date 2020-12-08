package com.univocity.envlp;

import org.springframework.context.*;
import org.springframework.context.annotation.*;

public final class App {

	private static ApplicationContext applicationContext;

	private App() {

	}

	private static ApplicationContext applicationContext() {
		if (applicationContext == null) {
			synchronized (Main.class) {
				if (applicationContext == null) {
					applicationContext = new AnnotationConfigApplicationContext(Dependencies.class);
				}
			}
		}
		return applicationContext;
	}

	public static <T> T get(Class<T> beanType) {
		return applicationContext().getBean(beanType);
	}

}

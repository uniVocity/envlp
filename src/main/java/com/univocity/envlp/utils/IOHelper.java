package com.univocity.envlp.utils;

import java.io.*;
import java.util.*;

public class IOHelper {

	public static Map<Closeable, Throwable> closeAllReturningErrors(Closeable... closeables) {
		Map<Closeable, Throwable> errors = new HashMap<Closeable, Throwable>();
		for (Closeable closeable : closeables) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (Throwable e) {
					errors.put(closeable, e);
				}
			}
		}
		return errors;
	}

	public static void closeAllIgnoringErrors(Closeable... closeables) {
		try {
			closeAll(true, closeables);
		} catch (Throwable ex) {
			// ignore
		}
	}

	public static void closeAllThrowingIllegalStateException(Closeable... closeables) {
		try {
			closeAll(false, closeables);
		} catch (IOException ex) {
			throw new IllegalStateException("Error closing resources", ex);
		}
	}

	public static void closeAll(Closeable... closeables) throws IOException {
		closeAll(false, closeables);
	}

	private static void closeAll(boolean ignoreErrors, Closeable... closeables) throws IOException {
		Map<Closeable, Throwable> errors = closeAllReturningErrors(closeables);
		if (!errors.isEmpty() && !ignoreErrors) {
			StringBuilder errorMessage = new StringBuilder();
			for (Map.Entry<Closeable, Throwable> error : errors.entrySet()) {
				errorMessage.append("Unable to close ").append(error.getKey());
				errorMessage.append(". Cause: ").append(error.getValue().getMessage());
				errorMessage.append(".\n");
			}
			throw new IOException(errorMessage.toString(), null);
		}
	}
}

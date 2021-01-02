package com.univocity.envlp.wallet.definition;

import java.util.*;

public interface WalletFormat {

	Token getToken();

	String getName();

	String getDescription();

	int getSeedLength();

	boolean isLegacyFormat();

	default boolean equals(WalletFormat w) {
		return getSeedLength() == w.getSeedLength()
				&& getName().equals(w.getName())
				&& Objects.equals(getDescription(), w.getDescription())
				&& getToken().equals(w.getToken());
	}

	default String printDetails() {
		StringBuilder out = new StringBuilder("Format for ");
		out.append(getToken().getTicker()).append(" (").append(getToken().getName()).append("): ");
		out.append(getName());
		out.append(" - ").append(getDescription());
		out.append(" (seed length = ").append(getSeedLength()).append(')');
		return out.toString();
	}
}

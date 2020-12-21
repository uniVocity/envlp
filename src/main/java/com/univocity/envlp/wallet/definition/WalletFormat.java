package com.univocity.envlp.wallet.definition;

import java.util.*;

public interface WalletFormat {

	Token getToken();

	String getName();

	String getDescription();

	int getSeedLength();

	default boolean equals(WalletFormat w) {
		return getSeedLength() == w.getSeedLength()
				&& getName().equals(w.getName())
				&& Objects.equals(getDescription(), w.getDescription())
				&& getToken().equals(w.getToken());
	}
}

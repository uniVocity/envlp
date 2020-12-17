package com.univocity.envlp.wallet.cardano;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.envlp.stamp.*;

import java.util.*;
import java.util.concurrent.*;

public final class CardanoWalletFormat implements WalletFormat {

	static final Set<CardanoWalletFormat> formats = ConcurrentHashMap.newKeySet();
	static final String SHELLEY = "Shelley";
	static final String BYRON = "Byron";

	public static final CardanoWalletFormat SHELLEY_24;
	public static final CardanoWalletFormat SHELLEY_15;
	public static final CardanoWalletFormat BYRON_12;
	public static final CardanoWalletFormat BYRON_15;
	public static final CardanoWalletFormat BYRON_27;

	static {
		formats.add(SHELLEY_24 = new CardanoWalletFormat(SHELLEY, "Daedalus compatible", 24));
		formats.add(SHELLEY_15 = new CardanoWalletFormat(SHELLEY, "Yoroi compatible", 15));
		formats.add(BYRON_12 = new CardanoWalletFormat(BYRON, "Daedalus compatible (legacy)", 12));
		formats.add(BYRON_15 = new CardanoWalletFormat(BYRON, "Yoroi compatible (legacy)", 15));
		formats.add(BYRON_27 = new CardanoWalletFormat(BYRON, "Daedalus paper wallet (legacy)", 27));
	}

	private final String name;
	private final String description;
	private final int seedLength;

	private CardanoWalletFormat(String name, String description, int seedLength) {
		this.name = name;
		this.description = description;
		this.seedLength = seedLength;
	}

	public static CardanoWalletFormat getFormat(AddressStyle addressStyle, Integer wordCount) {
		if (addressStyle == AddressStyle.Shelley) {
			if (wordCount == 24) {
				return SHELLEY_24;
			} else if (wordCount == 15) {
				return SHELLEY_15;
			}
		} else if (addressStyle == AddressStyle.Byron) {
			if (wordCount == 12) {
				return BYRON_12;
			} else if (wordCount == 15) {
				return BYRON_15;
			} else if (wordCount == 27) {
				return BYRON_27;
			}
		}
		throw new IllegalArgumentException("Unsupported wallet format: " + addressStyle + " with " + wordCount + " words");
	}

	@Override
	public Token getToken() {
		return ADAToken.instance;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getSeedLength() {
		return seedLength;
	}
}

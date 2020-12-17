package com.univocity.envlp.wallet.cardano;

import com.univocity.envlp.stamp.*;

public class CardanoWalletRestorationDetails implements WalletRestorationDetails {

	private WalletFormat walletFormat = CardanoWalletFormat.SHELLEY_24;
	private String seed;
	private String name;
	private String password;

	public WalletFormat walletFormat() {
		return walletFormat;
	}

	public void walletFormat(WalletFormat format) {
		this.walletFormat = format;
	}

	public String seed() {
		return seed;
	}

	public void seed(String seed) {
		this.seed = seed;
	}

	@Override
	public String walletName() {
		return name;
	}

	public void walletName(String name) {
		this.name = name;
	}

	public String password() {
		return password;
	}

	public void password(String password) {
		this.password = password;
	}
}

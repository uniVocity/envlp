package com.univocity.envlp.wallet.cardano;

import com.univocity.envlp.stamp.*;

final class ADAToken implements Token {

	public static final Token instance = new ADAToken();

	private ADAToken() {

	}

	@Override
	public String getName() {
		return "Cardano";
	}

	@Override
	public String getAmountPattern() {
		return "#,###.000000";
	}

	@Override
	public int getDecimals() {
		return 6;
	}

	@Override
	public String getMonetarySymbol() {
		return "₳";
	}

	@Override
	public String getTicker() {
		return "ADA";
	}

	@Override
	public String getDescription() {
		return "Cardano is a proof-of-stake blockchain platform that says its goal is to allow changemakers, innovators and visionaries to bring about positive global change.";
	}

}
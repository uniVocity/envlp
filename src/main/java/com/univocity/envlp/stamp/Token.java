package com.univocity.envlp.stamp;

import java.math.*;
import java.text.*;
import java.util.*;

public interface Token {

	String getName();

	String getAmountPattern();

	int getDecimals();

	String getMonetarySymbol();

	String getTicker();

	String getDescription();

	default String getFormattedAmount(BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		if (getAmountPattern() != null) {
			return new DecimalFormat(getAmountPattern()).format(amount);
		}
		return round(amount).toPlainString();
	}

	default BigDecimal round(BigDecimal amount) {
		if (amount.scale() != getDecimals()) {
			amount = amount.setScale(getDecimals(), RoundingMode.HALF_EVEN);
		}
		return amount;
	}

	default boolean equals(Token t) {
		return getName().equals(t.getName())
				&& getTicker().equals(t.getTicker())
				&& getDecimals() == t.getDecimals()
				&& Objects.equals(getDescription(), t.getDescription())
				&& Objects.equals(getMonetarySymbol(), t.getMonetarySymbol())
				&& Objects.equals(getAmountPattern(), t.getAmountPattern());
	}
}

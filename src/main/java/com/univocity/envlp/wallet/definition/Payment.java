package com.univocity.envlp.wallet.definition;

import java.math.*;

public interface Payment {

	String address();

	BigDecimal amount();
}

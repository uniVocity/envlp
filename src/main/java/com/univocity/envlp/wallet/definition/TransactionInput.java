package com.univocity.envlp.wallet.definition;

import java.math.*;

public interface TransactionInput {

	String address();

	BigDecimal amount();

	String id();

	long index();

}

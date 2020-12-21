package com.univocity.envlp.stamp;

import java.math.*;

public interface TransactionInput {

	String address();

	BigDecimal amount();

	String id();

	long index();

}

package com.univocity.envlp.stamp;

import java.math.*;

public interface Payment {

	String address();

	BigDecimal amount();
}

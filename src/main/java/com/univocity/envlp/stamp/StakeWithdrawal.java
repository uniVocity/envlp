package com.univocity.envlp.stamp;

import java.math.*;

public interface StakeWithdrawal {

	String stakeAddress();

	BigDecimal amount();
}
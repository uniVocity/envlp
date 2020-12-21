package com.univocity.envlp.wallet.definition;

import java.math.*;

public interface StakeWithdrawal {

	String stakeAddress();

	BigDecimal amount();
}
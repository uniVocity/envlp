package com.univocity.envlp.wallet.definition;

import java.math.*;
import java.util.*;

public interface Account {

	long id();

	BigDecimal accountBalance();

	BigDecimal rewardsBalance();

	ExternalWallet wallet();

	Addresses paymentAddresses();

	List<? extends Transaction<?>> transactions();


}

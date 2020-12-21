package com.univocity.envlp.wallet.definition;

import java.math.*;
import java.time.*;
import java.util.*;

public interface Transaction<T> {

	enum Status {
		PENDING,
		IN_LEDGER
	}

	enum Direction {
		OUTGOING,
		INCOMING
	}

	String id();

	BigDecimal amount();

	LocalDateTime insertedAt();

	LocalDateTime pendingSinceTime();

	BigInteger pendingSinceSlot();

	BigInteger pendingSinceEpoch();

	BigInteger pendingSinceSlotAbsolute();

	BigInteger depth();

	Direction direction();

	List<TransactionInput> inputs();

	List<Payment> outputs();

	List<StakeWithdrawal> withdrawals();

	Status status();

	T metadata();
}

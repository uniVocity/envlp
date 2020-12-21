package com.univocity.envlp.stamp;

import java.util.*;

public interface ExternalWallet {

	String id();

	ExternalWalletService<?,?> provider();

	Set<Account> accounts();

	WalletFormat format();
}
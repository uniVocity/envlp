package com.univocity.envlp.wallet.definition;

import java.util.*;

public interface ExternalWallet {

	String id();

	ExternalWalletService<?,?> provider();

	Set<Account> accounts();

	WalletFormat format();
}
package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.cardano.wallet.builders.wallets.*;

public class HotWalletService {

	private final RemoteWalletServer server;

	public HotWalletService(RemoteWalletServer server) {
		this.server = server;
	}

	public ShelleyWallet createWallet(String name, String seed, String password) {
		return server.wallets().create(name).shelley().addressPoolGap(20).fromSeed(seed).password(password);
	}

	public Wallet loadWallet(ColdWallet wallet) {
		if (wallet.canBeHot()) {
			return server.wallets().getById(wallet.getHotWalletId());
		}
		return null;
	}

}

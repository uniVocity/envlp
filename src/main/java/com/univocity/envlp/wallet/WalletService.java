package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.cardano.wallet.builders.wallets.*;

import java.util.*;

public class WalletService {

	private final ColdWalletService coldWalletService;
	private final HotWalletService hotWalletService;

	public WalletService(RemoteWalletServer server) {
		this.coldWalletService = new ColdWalletService();
		this.hotWalletService = new HotWalletService(server);
	}

	public ColdWallet createWallet(String name, String seed, String password) {
		ColdWallet cold = coldWalletService.createNewWallet(name, seed);
		Wallet hot = hotWalletService.createWallet(name, seed, password);
		coldWalletService.associateHotWallet(cold, hot);
		return cold;
	}


	public List<ColdWallet> loadWallets() {
		return coldWalletService.loadWallets();
	}
}

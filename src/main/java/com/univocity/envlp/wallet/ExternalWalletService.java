package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Service
public class ExternalWalletService {

	private final RemoteWalletServer server;

	@Autowired
	public ExternalWalletService(RemoteWalletServer server) {
		this.server = server;
	}

	public ShelleyWallet createWallet(String name, String seed, String password) {
		return server.wallets().create(name).shelley().addressPoolGap(20).fromSeed(seed).password(password);
	}

	public Wallet loadWallet(WalletSnapshot wallet) {
		if (wallet.isExternal()) {
			return server.wallets().getById(wallet.getExternalWalletId());
		}
		return null;
	}

}

package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class WalletService {

	private final WalletSnapshotService walletSnapshotService;
	private final ExternalWalletService localWalletService;

	@Autowired
	public WalletService(WalletSnapshotService walletSnapshotService, ExternalWalletService externalWalletService) {
		this.walletSnapshotService = walletSnapshotService;
		this.localWalletService = externalWalletService;
	}

	public WalletSnapshot createWallet(String name, String seed, String password) {
		WalletSnapshot snapshot = walletSnapshotService.createNewWallet(name, seed);
		Wallet localWallet = localWalletService.createWallet(name, seed, password);
		walletSnapshotService.associateLocalWallet(snapshot, localWallet);
		return snapshot;
	}


	public List<WalletSnapshot> loadWallets() {
		return walletSnapshotService.loadWallets();
	}

//	public Wallet getWallet(){
//
//	}
}

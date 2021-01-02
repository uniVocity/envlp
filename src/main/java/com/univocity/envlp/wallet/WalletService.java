package com.univocity.envlp.wallet;

import com.univocity.envlp.wallet.cardano.*;
import com.univocity.envlp.wallet.definition.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class WalletService {

	private final WalletSnapshotService walletSnapshotService;
	private final ExternalWalletService<?,?> externalWalletService;
	private final TokenDAO tokenDAO;
	private final WalletFormatDAO walletFormatDAO;
	private final ExternalWalletProviderDAO externalWalletProviderDAO;



	@Autowired
	public WalletService(WalletSnapshotService walletSnapshotService, ExternalWalletService<?,?> externalWalletService, TokenDAO tokenDAO, WalletFormatDAO walletFormatDAO, ExternalWalletProviderDAO externalWalletProviderDAO) {
		this.walletSnapshotService = walletSnapshotService;
		this.externalWalletService = externalWalletService;
		this.tokenDAO = tokenDAO;
		this.walletFormatDAO = walletFormatDAO;
		this.externalWalletProviderDAO = externalWalletProviderDAO;
	}

	public WalletSnapshot createWallet(String name, String seed, String password, EnvlpWalletFormat format) {
		//TODO: make this all happen in a cardano-specific package. Also this code sucks so much it hurts (for now).
		CardanoWalletRestorationDetails details = new CardanoWalletRestorationDetails();
		details.walletName(name);
		details.seed(seed);
		details.password(password);
		details.walletFormat(format);
		ExternalWalletProvider cardanoWalletProvider = externalWalletProviderDAO.getWalletProviderByClassName(externalWalletService.getClass().getName());

		//TODO: proper transaction handling in these 3 lines
		WalletSnapshot snapshot = walletSnapshotService.createNewWallet(name, seed, format, cardanoWalletProvider);
		ExternalWallet externalWallet = ((CardanoWalletBackendService)externalWalletService).createOrRestoreWallet(details);
		walletSnapshotService.associateLocalWallet(snapshot, externalWallet);

		return snapshot;
	}


	public List<WalletSnapshot> loadWallets() {
		return walletSnapshotService.loadWallets();
	}

//	public Wallet getWallet(){
//
//	}
}

package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.database.*;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.cardano.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.testng.*;

@ContextConfiguration(classes = {TestDependencies.class})
public class BaseTest extends AbstractTestNGSpringContextTests {

	@Autowired
	protected WalletFormatDAO walletFormatDAO;

	@Autowired
	protected TokenDAO tokenDAO;

	@Autowired
	protected ExternalWalletProviderDAO externalWalletProviderDAO;

	@Autowired
	protected WalletDAO walletDAO;

	@Autowired
	protected WalletSnapshotService service;

	@Autowired
	private ExtendedJdbcTemplate db;

	public ExtendedJdbcTemplate db() {
		return db;
	}

	@Autowired
	private void setDb(ExtendedJdbcTemplate db) {
		this.db = db;
	}

	public WalletSnapshot createNewWallet(String walletName, String seed){
		EnvlpToken token = tokenDAO.getTokenByTicker("ADA");
		EnvlpWalletFormat walletFormat = walletFormatDAO.getWalletFormatsForToken(token).get(0);
		ExternalWalletProvider externalWalletProvider = externalWalletProviderDAO.getWalletProviderByClassName(CardanoWalletBackendService.class.getName());

		WalletSnapshot snapshot = service.createNewWallet(walletName, seed, walletFormat, externalWalletProvider);
		return snapshot;
	}
}

package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.wallet.persistence.model.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class WalletFormatDAOTest extends BaseTest{

	@Test
	public void testPersistence() {
		EnvlpToken token = tokenDAO.getTokenByTicker("ADA");

		EnvlpWalletFormat walletFormat = new EnvlpWalletFormat(token);
		walletFormat.setDescription("Cardano Shelley Wallet Format");
		walletFormat.setName("Shelley");
		walletFormat.setSeedLength(24);

		walletFormat = walletFormatDAO.persistWalletFormat(walletFormat);

		assertNotSame(token, walletFormat.getToken());
		assertNotEquals(walletFormat.getId(), 0L);
		assertNotNull(walletFormat.getCreatedAt());
		assertNotNull(walletFormat.getUpdatedAt());
		assertEquals(walletFormat.getSeedLength(), 24);

		EnvlpWalletFormat inDb = walletFormatDAO.getWalletFormatById(walletFormat.getId());
		assertNotNull(inDb);
		assertEquality(inDb, walletFormat);
		assertEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		walletFormat.setDescription("blah blah blah");
		walletFormat.setSeedLength(15);
		inDb = walletFormatDAO.persistWalletFormat(walletFormat);
		assertEquality(inDb, walletFormat);
		assertNotEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		new TokenDAOTest().assertEquality(walletFormat.getToken(), token);
	}

	void assertEquality(EnvlpWalletFormat inDb, EnvlpWalletFormat token){
		assertEquals(inDb.getId(), token.getId());
		assertEquals(inDb.getCreatedAt(), token.getCreatedAt());
		assertEquals(inDb.getName(), token.getName());
		assertEquals(inDb.getDescription(), token.getDescription());
		assertEquals(inDb.getSeedLength(), token.getSeedLength());
	}
}

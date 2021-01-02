package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.wallet.persistence.model.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class WalletFormatDAOTest extends BaseTest{

	@Test
	public void testPersistence() {
		EnvlpToken token = tokenDAO.getTokenByTicker("ADA");

		EnvlpWalletFormat walletFormat = new EnvlpWalletFormat(token);
		walletFormat.setDescription("Blah Wallet Format");
		walletFormat.setName("Blah");
		walletFormat.setSeedLength(24);
		walletFormat.setLegacyFormat(true);

		walletFormat = walletFormatDAO.persistWalletFormat(walletFormat);

		assertNotSame(token, walletFormat.getToken());
		assertNotEquals(walletFormat.getId(), 0L);
		assertNotNull(walletFormat.getCreatedAt());
		assertNotNull(walletFormat.getUpdatedAt());
		assertEquals(walletFormat.getSeedLength(), 24);
		assertTrue(walletFormat.isLegacyFormat());

		EnvlpWalletFormat inDb = walletFormatDAO.getWalletFormatById(walletFormat.getId());
		assertNotNull(inDb);
		assertEquality(inDb, walletFormat);
		assertEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		walletFormat.setDescription("blah blah blah");
		walletFormat.setSeedLength(15);
		walletFormat.setLegacyFormat(false);
		inDb = walletFormatDAO.persistWalletFormat(walletFormat);
		assertEquality(inDb, walletFormat);
		assertNotEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		new TokenDAOTest().assertEquality(walletFormat.getToken(), token);
	}

	void assertEquality(EnvlpWalletFormat inDb, EnvlpWalletFormat format){
		assertEquals(inDb.getId(), format.getId());
		assertEquals(inDb.getCreatedAt(), format.getCreatedAt());
		assertEquals(inDb.getName(), format.getName());
		assertEquals(inDb.isLegacyFormat(), format.isLegacyFormat());
		assertEquals(inDb.getDescription(), format.getDescription());
		assertEquals(inDb.getSeedLength(), format.getSeedLength());
	}
}

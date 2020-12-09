package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class WalletFormatDAOTest extends BaseTest{

	@Autowired
	WalletFormatDAO walletFormatDAO;

	@Autowired
	TokenDAO tokenDAO;

	@Test
	public void testPersistence() {
		TokenDAOTest tokenTest = new TokenDAOTest();
		tokenTest.tokenDAO = tokenDAO;
		tokenTest.testPersistToken();

		Token token = tokenDAO.listTokens().get(0);

		WalletFormat walletFormat = new WalletFormat(token);
		walletFormat.setDescription("Cardano Shelley Wallet Format");
		walletFormat.setName("Shelley");
		walletFormat.setSeedLength(24);

		walletFormat = walletFormatDAO.persistWalletFormat(walletFormat);

		assertNotSame(token, walletFormat.getToken());
		assertNotEquals(walletFormat.getId(), 0L);
		assertNotNull(walletFormat.getCreatedAt());
		assertNotNull(walletFormat.getUpdatedAt());
		assertEquals(walletFormat.getSeedLength(), 24);

		WalletFormat inDb = walletFormatDAO.getWalletFormatById(walletFormat.getId());
		assertNotNull(inDb);
		assertEquality(inDb, walletFormat);
		assertEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		walletFormat.setDescription("blah blah blah");
		walletFormat.setSeedLength(15);
		inDb = walletFormatDAO.persistWalletFormat(walletFormat);
		assertEquality(inDb, walletFormat);
		assertNotEquals(inDb.getUpdatedAt(), walletFormat.getUpdatedAt());

		tokenTest.assertEquality(walletFormat.getToken(), token);
	}

	void assertEquality(WalletFormat inDb, WalletFormat token){
		assertEquals(inDb.getId(), token.getId());
		assertEquals(inDb.getCreatedAt(), token.getCreatedAt());
		assertEquals(inDb.getName(), token.getName());
		assertEquals(inDb.getDescription(), token.getDescription());
		assertEquals(inDb.getSeedLength(), token.getSeedLength());
	}
}

package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.wallet.persistence.model.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class TokenDAOTest extends BaseTest {

	@Test
	public void testPersistToken() {
		EnvlpToken token = new EnvlpToken();
		token.setDescription("Cardano is blah blah");
		token.setName("Cardano");
		token.setTicker("ADA");
		token.setMonetarySymbol("₳");
		token.setDecimals(6);
		token.setAmountPattern("#,###.000000");

		token = tokenDAO.persistToken(token);
		assertNotEquals(token.getId(), 0L);
		assertNotNull(token.getCreatedAt());
		assertNotNull(token.getUpdatedAt());

		EnvlpToken inDb = tokenDAO.getTokenById(token.getId());
		assertNotNull(inDb);
		assertEquality(inDb, token);
		assertEquals(inDb.getUpdatedAt(), token.getUpdatedAt());

		token.setDescription("blah blah blah");
		inDb = tokenDAO.persistToken(token);
		assertEquality(inDb, token);
		assertNotEquals(inDb.getUpdatedAt(), token.getUpdatedAt());
	}

	void assertEquality(EnvlpToken inDb, EnvlpToken token){
		assertEquals(inDb.getId(), token.getId());
		assertEquals(inDb.getAmountPattern(), token.getAmountPattern());
		assertEquals(inDb.getCreatedAt(), token.getCreatedAt());
		assertEquals(inDb.getDecimals(), token.getDecimals());
		assertEquals(inDb.getMonetarySymbol(), token.getMonetarySymbol());
		assertEquals(inDb.getName(), token.getName());
		assertEquals(inDb.getTicker(), token.getTicker());

	}
}
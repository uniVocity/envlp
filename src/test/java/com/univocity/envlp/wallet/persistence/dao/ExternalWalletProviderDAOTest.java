package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class ExternalWalletProviderDAOTest extends BaseTest{

	@Autowired
	ExternalWalletProviderDAO externalWalletProviderDAO;

	@Test
	public void testPersistence() {
		ExternalWalletProvider provider = new ExternalWalletProvider();
		provider.setDescription("Cardano Wallet backend (same as daedalus)");
		provider.setName("Cardano Wallet Backend");
		provider.setClassName("com.univocity.envlp.wallet.CardanoWalletBackendService");
		provider.setVersion("2020-11-29-SNAPSHOT");

		provider = externalWalletProviderDAO.persist(provider);
		assertNotEquals(provider.getId(), 0L);
		assertNotNull(provider.getCreatedAt());
		assertNotNull(provider.getUpdatedAt());

		ExternalWalletProvider inDb = externalWalletProviderDAO.getWalletProviderById(provider.getId());
		assertNotNull(inDb);
		assertEquality(inDb, provider);
		assertEquals(inDb.getUpdatedAt(), provider.getUpdatedAt());

		provider.setDescription("blah blah blah");
		inDb = externalWalletProviderDAO.persist(provider);
		assertEquality(inDb, provider);
		assertNotEquals(inDb.getUpdatedAt(), provider.getUpdatedAt());
	}

	private void assertEquality(ExternalWalletProvider inDb, ExternalWalletProvider token){
		assertEquals(inDb.getId(), token.getId());
		assertEquals(inDb.getCreatedAt(), token.getCreatedAt());
		assertEquals(inDb.getName(), token.getName());
		assertEquals(inDb.getClassName(), token.getClassName());
		assertEquals(inDb.getVersion(), token.getVersion());
		assertEquals(inDb.getDescription(), token.getDescription());

	}
}

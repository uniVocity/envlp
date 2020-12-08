package com.univocity.envlp.wallet;

import com.univocity.envlp.wallet.persistence.dao.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.beans.factory.annotation.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class WalletDAOTest extends BaseTest {

	@Autowired
	private WalletDAO walletDAO;

	private WalletSnapshot newWallet(String name) {
		WalletSnapshot out = new WalletSnapshot(name);


		out.addPublicRootKey(0, name + "_1");
		out.addPublicRootKey(1, name + "_2");
		out.addPublicRootKey(2, name + "_3");
		return out;
	}

	@Test
	public void testPersistWallet() {
		WalletSnapshot wallet1 = walletDAO.persistWallet(newWallet("wallet1"));
		checkWallet(wallet1, 1, "wallet1", 3);

		WalletSnapshot wallet2 = walletDAO.persistWallet(newWallet("wallet2"));
		checkWallet(wallet2, 2, "wallet2", 3);

		WalletSnapshot wallet3 = walletDAO.persistWallet(newWallet("wallet3"));
		checkWallet(wallet3, 3, "wallet3", 3);
	}

	private void checkWallet(WalletSnapshot wallet, long id, String name, int accounts) {
		assertEquals(wallet.getId(), id);
		assertEquals(wallet.getName(), name);
		assertEquals(wallet.getAccounts().size(), accounts);
		assertNotNull(wallet.getCreatedAt());

		for (long i = 0; i < accounts; i++) {
			assertNotNull(wallet.getAccounts().get(i));
			assertEquals(wallet.getAccounts().get(i), name + "_" + (i + 1));
		}
	}

	@Test(dependsOnMethods = "testPersistWallet")
	public void testLoadWallet() {
		checkWallet(walletDAO.loadWallet(1), 1, "wallet1", 3);
		checkWallet(walletDAO.loadWallet(2), 2, "wallet2", 3);
		checkWallet(walletDAO.loadWallet(3), 3, "wallet3", 3);
	}

	@Test(dependsOnMethods = "testLoadWallet")
	public void testLoadWallets() {
		List<WalletSnapshot> wallets = walletDAO.loadWallets();
		assertEquals(wallets.size(), 3);
		checkWallet(wallets.get(0), 1, "wallet1", 3);
		checkWallet(wallets.get(1), 2, "wallet2", 3);
		checkWallet(wallets.get(2), 3, "wallet3", 3);
	}

	@Test(dependsOnMethods = "testLoadWallets")
	public void testModifyWallet() {
		WalletSnapshot wallet = walletDAO.loadWallet(2);
		checkWallet(wallet, 2, "wallet2", 3);

		wallet.setName("wall2");
		wallet.addPublicRootKey(1, "noop");
		wallet.addPublicRootKey(3, "lalala");
		wallet.addPublicRootKey(4, "lololo");

		assertEquals(wallet.getAccounts().get(1L), "wallet2_2");
		assertEquals(wallet.getAccounts().get(3L), "lalala");
		assertEquals(wallet.getAccounts().get(4L), "lololo");

		WalletSnapshot persisted = walletDAO.persistWallet(wallet);
		assertTrue(persisted == wallet);
		assertEquals(wallet.getName(), "wall2");
		assertEquals(wallet.getAccounts().get(3L), "lalala");
		assertEquals(wallet.getAccounts().get(4L), "lololo");

		WalletSnapshot fromDb = walletDAO.loadWallet(2);
		assertTrue(fromDb != wallet);
		assertEquals(fromDb.getName(), "wall2");
		assertEquals(fromDb.getAccounts().get(3L), "lalala");
		assertEquals(fromDb.getAccounts().get(4L), "lololo");
	}

	@Test(dependsOnMethods = "testModifyWallet")
	public void testDeleteWallet() {
		assertTrue(walletDAO.deleteWallet(walletDAO.loadWallet(1)));
		assertNull(walletDAO.loadWallet(1));

		assertTrue(walletDAO.deleteWallet(2L));
		assertNull(walletDAO.loadWallet(2L));
		assertEquals(walletDAO.loadWallets().size(), 1);

		assertTrue(walletDAO.deleteWallet(3L));
		assertTrue(walletDAO.loadWallets().isEmpty());

		assertEquals(db().count("SELECT COUNT(*) FROM wallet_account"), 0L);
	}
}
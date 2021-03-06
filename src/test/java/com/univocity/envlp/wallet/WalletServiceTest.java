package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

public class WalletServiceTest extends BaseTest {

	@Test
	public void testCreateNewWallet() {
		String seed = service.generateSeed();
		WalletSnapshot wallet = createNewWallet("wallet from seed", seed);

		assertNotNull(wallet);
		assertNotNull(wallet.getCreatedAt());
		assertNotNull(wallet.getId());
		assertEquals(wallet.getName(), "wallet from seed");

		assertEquals(wallet.getAccounts().size(), 1);
		assertNotNull(wallet.getAccounts().get(0L));

		seed = AddressManagerTest.seed;
		wallet = createNewWallet("myWallet", seed);
		assertEquals(wallet.getAccounts().get(0L), AddressManagerTest.publicRootKey_0);
	}

	@Test(dependsOnMethods = "testCreateNewWallet")
	public void testAddAccountFromSeed() {
		WalletSnapshot wallet = service.getWalletByName("myWallet");
		service.addAccountFromSeed(wallet, AddressManagerTest.seed, 10);
		assertEquals(wallet.getAccounts().get(10L), AddressManagerTest.publicRootKey_10);

		wallet = service.getWalletByName("myWallet");
		assertEquals(wallet.getAccounts().get(10L), AddressManagerTest.publicRootKey_10);

		assertEquals(wallet.getAccounts().size(), 2);
	}

	@Test(dependsOnMethods = "testAddAccountFromSeed")
	public void testAddAccountsFromSeed() {
		WalletSnapshot wallet = service.getWalletByName("myWallet");
		assertEquals(wallet.getAccounts().get(0L), AddressManagerTest.publicRootKey_0);
		assertEquals(wallet.getAccounts().get(10L), AddressManagerTest.publicRootKey_10);

		service.addAccountFromSeed(wallet, AddressManagerTest.seed, 5); //add
		service.addAccountFromSeed(wallet, AddressManagerTest.seed, 11); //add

		service.addAccountsFromSeed(wallet, AddressManagerTest.seed, 10); //add 10 more

		assertEquals(wallet.getAccounts().get(0L), AddressManagerTest.publicRootKey_0);
		assertEquals(wallet.getAccounts().get(10L), AddressManagerTest.publicRootKey_10);

		assertEquals(wallet.getAccounts().size(), 14);
		for (long i = 0; i < 14; i++) {
			assertNotNull(wallet.getAccounts().get(i));
		}
	}

	@Test(dependsOnMethods = "testAddAccountsFromSeed")
	public void testGetPaymentAddress() {
		WalletSnapshot wallet = service.getWalletByName("myWallet");
		String address0_0 = service.getPaymentAddress(wallet, 0, 0);
		assertNotNull(address0_0);
		String address0_0Again = service.getPaymentAddress(wallet, 0, 0);
		assertNotNull(address0_0Again);

		assertEquals(address0_0, address0_0Again);

		//account wasn't created, return null.
		assertNull(service.getPaymentAddress(wallet, 9999, 0));

	}

	@Test
	public void testAllocateNextPaymentAddress() {
		String seed = service.generateSeed();
		WalletSnapshot wallet = createNewWallet("randomWallet2", seed);

		//no other accounts registered, will allocate to default account 0
		String payment1 = service.allocateNextPaymentAddress(wallet);
		assertNotNull(payment1);
		String payment2 = service.allocateNextPaymentAddress(wallet);
		assertNotNull(payment2);
		assertNotEquals(payment1, payment2);

		assertEquals(payment1, service.getPaymentAddress(wallet, 0, 0));
		assertEquals(payment2, service.getPaymentAddress(wallet, 0, 1));

		//Once account is added, will allocate from any account other than 0.
		wallet = service.addAccountFromSeed(wallet, seed, 23);
		String payment3 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment3, service.getPaymentAddress(wallet, 23, 0));

		String payment4 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment4, service.getPaymentAddress(wallet, 23, 1));

		String payment5 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment5, service.getPaymentAddress(wallet, 23, 2));

		//Added another account
		wallet = service.addAccountFromSeed(wallet, seed, 3);
		String payment6 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment6, service.getPaymentAddress(wallet, 3, 0));

		String payment7 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment7, service.getPaymentAddress(wallet, 3, 1));

		String payment8 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment8, service.getPaymentAddress(wallet, 3, 2));

		//will cycle through accounts (not using the default 0)
		String payment9 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment9, service.getPaymentAddress(wallet, 3, 3));

		String payment10 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment10, service.getPaymentAddress(wallet, 23, 3));

		String payment11 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment11, service.getPaymentAddress(wallet, 3, 4));

		String payment12 = service.allocateNextPaymentAddress(wallet);
		assertEquals(payment12, service.getPaymentAddress(wallet, 23, 4));
	}

	@Test
	public void testAllocateNextPaymentAddressFromAccount() {
		String seed = service.generateSeed();
		WalletSnapshot wallet = createNewWallet("randomWallet1", seed);

		String payment1 = service.allocateNextPaymentAddress(wallet, 0);
		assertNotNull(payment1);
		String payment2 = service.allocateNextPaymentAddress(wallet, 0);
		assertNotNull(payment2);
		assertNotEquals(payment1, payment2);

		assertEquals(payment1, service.getPaymentAddress(wallet, 0, 0));
		assertEquals(payment2, service.getPaymentAddress(wallet, 0, 1));
	}

	@Test(dependsOnMethods = "testAllocateNextPaymentAddressFromAccount")
	public void testGetAddressesForDefaultAccount() {
		WalletSnapshot wallet = service.getWalletByName("randomWallet1");
		List<AddressAllocation> addresses = service.getAddressesForDefaultAccount(wallet);
		assertEquals(addresses.size(), 2);
		for (AddressAllocation address : addresses) {
			assertNotNull(address.getPaymentAddress());
			assertEquals(address.getAccountIndex(), 0);
			assertEquals(address.getWalletId(), wallet.getId());
		}

		//most recent address first
		assertEquals(addresses.get(0).getDerivationIndex(), 1);
		assertEquals(addresses.get(1).getDerivationIndex(), 0);
	}
}

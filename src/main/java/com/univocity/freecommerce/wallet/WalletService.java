package com.univocity.freecommerce.wallet;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.freecommerce.*;
import org.apache.commons.lang3.*;

import java.util.*;

public class WalletService {

	private final WalletDAO walletDAO = new WalletDAO();
	private final AddressAllocationDAO addressAllocationDAO = new AddressAllocationDAO();
	private final AddressManager addressManager;

	public WalletService() {
		addressManager = new AddressManager(Configuration.getInstance().getCardanoToolsDirPath());
	}

	public AddressManager getAddressManager() {
		return addressManager;
	}

	public String generateSeed() {
		return addressManager.generateSeed();
	}

	public Wallet createNewWallet(String name, String seed) {
		Wallet wallet = new Wallet(name);

		final String privateKey = addressManager.generatePrivateKey(seed);
		wallet = addAccount(wallet, privateKey, 0);
		return wallet;
	}

	public Wallet addAccountFromSeed(Wallet wallet, String seed, long accountIndex) {
		return addAccount(wallet, addressManager.generatePrivateKey(seed), accountIndex);
	}

	public Wallet addAccount(Wallet wallet, String privateKey, long accountIndex) {
		addAccountToWallet(wallet, privateKey, accountIndex);
		return walletDAO.persistWallet(wallet);
	}

	public Wallet addAccountsFromSeed(Wallet wallet, String seed, long accountsToCreate) {
		return addAccounts(wallet, addressManager.generatePrivateKey(seed), accountsToCreate);
	}

	public Wallet addAccounts(Wallet wallet, String privateKey, long accountsToCreate) {
		Set<Long> keys = new LinkedHashSet<>(wallet.accounts.keySet());
		long seq = 0;

		while (accountsToCreate-- > 0) {
			while (keys.contains(seq)) {
				seq++;
			}
			addAccountToWallet(wallet, privateKey, seq++);
		}

		wallet = walletDAO.persistWallet(wallet);
		return wallet;
	}

	private void addAccountToWallet(Wallet wallet, String privateKey, long accountIndex) {
		String account = addressManager.generatePublicRootKeyFromPrivateKey(privateKey, accountIndex);
		wallet.addPublicRootKey(accountIndex, account);
	}

	public Wallet getWalletByName(String walletName) {
		return walletDAO.getWalletByName(walletName);
	}

	public boolean deleteWallet(Wallet wallet) {
		return walletDAO.deleteWallet(wallet);
	}

	String getPaymentAddress(Wallet wallet, long accountIndex, long derivationIndex) {
		String accountPublicRootKey = wallet.accounts.get(accountIndex);
		if (accountPublicRootKey == null) {
			return null;
		}
		return addressManager.generatePaymentAddressFromPublicRootKey(accountPublicRootKey, derivationIndex);
	}

	public String allocateNextPaymentAddress(Wallet wallet, long accountIndex) {
		AddressAllocation allocation = accountIndex >= 0 ? addressAllocationDAO.allocateNextAddress(wallet, accountIndex) : addressAllocationDAO.allocateNextAccount(wallet);

		if (StringUtils.isBlank(allocation.getPaymentAddress())) {
			String paymentAddress = getPaymentAddress(wallet, allocation.getAccountIndex(), allocation.getDerivationIndex());
			allocation.setPaymentAddress(paymentAddress);
			addressAllocationDAO.persistPaymentAddress(allocation);
		}

		return allocation.getPaymentAddress();
	}

	public String allocateNextPaymentAddress(Wallet wallet) {
		return allocateNextPaymentAddress(wallet, -1);
	}

	public List<AddressAllocation> getAddressesForDefaultAccount(Wallet wallet) {
		return addressAllocationDAO.getAddresses(wallet, 0);
	}

	public List<AddressAllocation> getAddressesForAccount(Wallet wallet, long accountIndex) {
		return addressAllocationDAO.getAddresses(wallet, accountIndex);
	}
}

package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.envlp.*;
import org.apache.commons.lang3.*;

import java.util.*;

public class ColdWalletService {

	private final WalletDAO walletDAO = new WalletDAO();
	private final AddressAllocationDAO addressAllocationDAO = new AddressAllocationDAO();
	private final AddressManager addressManager;

	public ColdWalletService() {
		addressManager = new AddressManager(Configuration.getInstance().getCardanoToolsDirPath());
	}

	public AddressManager getAddressManager() {
		return addressManager;
	}

	public String generateSeed() {
		return addressManager.generateSeed();
	}

	public List<ColdWallet> loadWallets(){
		return walletDAO.loadWallets();
	}

	public ColdWallet createNewWallet(String name, String seed) {
		ColdWallet wallet = new ColdWallet(name);

		final String privateKey = addressManager.generatePrivateKey(seed);
		wallet = addAccount(wallet, privateKey, 0);
		return wallet;
	}

	public ColdWallet addAccountFromSeed(ColdWallet wallet, String seed, long accountIndex) {
		return addAccount(wallet, addressManager.generatePrivateKey(seed), accountIndex);
	}

	public ColdWallet addAccount(ColdWallet wallet, String privateKey, long accountIndex) {
		addAccountToWallet(wallet, privateKey, accountIndex);
		return walletDAO.persistWallet(wallet);
	}

	public ColdWallet addAccountsFromSeed(ColdWallet wallet, String seed, long accountsToCreate) {
		return addAccounts(wallet, addressManager.generatePrivateKey(seed), accountsToCreate);
	}

	public ColdWallet addAccounts(ColdWallet wallet, String privateKey, long accountsToCreate) {
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

	private void addAccountToWallet(ColdWallet wallet, String privateKey, long accountIndex) {
		String account = addressManager.generatePublicRootKeyFromPrivateKey(privateKey, accountIndex);
		wallet.addPublicRootKey(accountIndex, account);
	}

	public ColdWallet getWalletByName(String walletName) {
		return walletDAO.getWalletByName(walletName);
	}

	public boolean deleteWallet(ColdWallet wallet) {
		return walletDAO.deleteWallet(wallet);
	}

	String getPaymentAddress(ColdWallet wallet, long accountIndex, long derivationIndex) {
		String accountPublicRootKey = wallet.accounts.get(accountIndex);
		if (accountPublicRootKey == null) {
			return null;
		}
		return addressManager.generatePaymentAddressFromPublicRootKey(accountPublicRootKey, derivationIndex);
	}

	public String allocateNextPaymentAddress(ColdWallet wallet, long accountIndex) {
		AddressAllocation allocation = accountIndex >= 0 ? addressAllocationDAO.allocateNextAddress(wallet, accountIndex) : addressAllocationDAO.allocateNextAccount(wallet);

		if (StringUtils.isBlank(allocation.getPaymentAddress())) {
			String paymentAddress = getPaymentAddress(wallet, allocation.getAccountIndex(), allocation.getDerivationIndex());
			allocation.setPaymentAddress(paymentAddress);
			addressAllocationDAO.persistPaymentAddress(allocation);
		}

		return allocation.getPaymentAddress();
	}

	public String allocateNextPaymentAddress(ColdWallet wallet) {
		return allocateNextPaymentAddress(wallet, -1);
	}

	public List<AddressAllocation> getAddressesForDefaultAccount(ColdWallet wallet) {
		return addressAllocationDAO.getAddresses(wallet, 0);
	}

	public List<AddressAllocation> getAddressesForAccount(ColdWallet wallet, long accountIndex) {
		return addressAllocationDAO.getAddresses(wallet, accountIndex);
	}

	public void associateHotWallet(ColdWallet cold, Wallet hot) {
		walletDAO.associateHotWallet(cold, hot);
	}
}

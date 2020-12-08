package com.univocity.envlp.wallet;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class WalletSnapshotService {

	private final WalletDAO walletDAO;
	private final AddressAllocationDAO addressAllocationDAO;
	private final AddressManager addressManager;

	@Autowired
	public WalletSnapshotService(AddressManager addressManager, WalletDAO walletDAO, AddressAllocationDAO addressAllocationDAO) {
		this.addressManager = addressManager;
		this.walletDAO = walletDAO;
		this.addressAllocationDAO = addressAllocationDAO;
	}

	public AddressManager getAddressManager() {
		return addressManager;
	}

	public String generateSeed() {
		return addressManager.generateSeed();
	}

	public List<WalletSnapshot> loadWallets() {
		return walletDAO.loadWallets();
	}

	public WalletSnapshot createNewWallet(String name, String seed) {
		WalletSnapshot wallet = new WalletSnapshot(name);

		final String privateKey = addressManager.generatePrivateKey(seed);
		wallet = addAccount(wallet, privateKey, 0);
		return wallet;
	}

	public WalletSnapshot addAccountFromSeed(WalletSnapshot wallet, String seed, long accountIndex) {
		return addAccount(wallet, addressManager.generatePrivateKey(seed), accountIndex);
	}

	public WalletSnapshot addAccount(WalletSnapshot wallet, String privateKey, long accountIndex) {
		addAccountToWallet(wallet, privateKey, accountIndex);
		return walletDAO.persistWallet(wallet);
	}

	public WalletSnapshot addAccountsFromSeed(WalletSnapshot wallet, String seed, long accountsToCreate) {
		return addAccounts(wallet, addressManager.generatePrivateKey(seed), accountsToCreate);
	}

	public WalletSnapshot addAccounts(WalletSnapshot wallet, String privateKey, long accountsToCreate) {
		Set<Long> keys = new LinkedHashSet<>(wallet.getAccounts().keySet());
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

	private void addAccountToWallet(WalletSnapshot wallet, String privateKey, long accountIndex) {
		String account = addressManager.generatePublicRootKeyFromPrivateKey(privateKey, accountIndex);
		wallet.addPublicRootKey(accountIndex, account);
	}

	public WalletSnapshot getWalletByName(String walletName) {
		return walletDAO.getWalletByName(walletName);
	}

	public boolean deleteWallet(WalletSnapshot wallet) {
		return walletDAO.deleteWallet(wallet);
	}

	String getPaymentAddress(WalletSnapshot wallet, long accountIndex, long derivationIndex) {
		String accountPublicRootKey = wallet.getAccounts().get(accountIndex);
		if (accountPublicRootKey == null) {
			return null;
		}
		return addressManager.generatePaymentAddressFromPublicRootKey(accountPublicRootKey, derivationIndex);
	}

	public String allocateNextPaymentAddress(WalletSnapshot wallet, long accountIndex) {
		AddressAllocation allocation = accountIndex >= 0 ? addressAllocationDAO.allocateNextAddress(wallet, accountIndex) : addressAllocationDAO.allocateNextAccount(wallet);

		if (StringUtils.isBlank(allocation.getPaymentAddress())) {
			String paymentAddress = getPaymentAddress(wallet, allocation.getAccountIndex(), allocation.getDerivationIndex());
			allocation.setPaymentAddress(paymentAddress);
			addressAllocationDAO.persistPaymentAddress(allocation);
		}

		return allocation.getPaymentAddress();
	}

	public String allocateNextPaymentAddress(WalletSnapshot wallet) {
		return allocateNextPaymentAddress(wallet, -1);
	}

	public List<AddressAllocation> getAddressesForDefaultAccount(WalletSnapshot wallet) {
		return addressAllocationDAO.getAddresses(wallet, 0);
	}

	public List<AddressAllocation> getAddressesForAccount(WalletSnapshot wallet, long accountIndex) {
		return addressAllocationDAO.getAddresses(wallet, accountIndex);
	}

	public void associateLocalWallet(WalletSnapshot cold, Wallet hot) {
		walletDAO.associateHotWallet(cold, hot);
	}
}

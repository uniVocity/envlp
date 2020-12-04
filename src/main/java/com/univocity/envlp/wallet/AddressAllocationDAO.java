package com.univocity.envlp.wallet;

import com.univocity.envlp.database.*;
import com.univocity.envlp.utils.*;
import org.slf4j.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;


public class AddressAllocationDAO {

	private static final Logger log = LoggerFactory.getLogger(AddressAllocationDAO.class);

	private static final RowMapper<AddressAllocation> rowMapper = (rs, rowNum) -> {
		AddressAllocation out = new AddressAllocation();
		out.setWalletId(rs.getLong("wallet_id"));
		out.setAccountIndex(rs.getLong("account_idx"));
		out.setDerivationIndex(rs.getLong("derivation_idx"));
		out.setAvailable(rs.getBoolean("available"));
		out.setPaymentAddress(rs.getString("payment_address"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		out.setClaimedAt(Utils.toLocalDateTime(rs.getTimestamp("claimed_at")));
		return out;
	};

	public void createDefaultAllocations(ColdWallet wallet) {
		List<Long> missingAccounts = Database.get().queryForList("SELECT account_idx FROM wallet_account WHERE wallet_id = ? AND account_idx NOT IN (SELECT account_idx FROM address_allocation WHERE wallet_id = ?)", Long.class, wallet.getId(), wallet.getId());
		if (!missingAccounts.isEmpty()) {
			List<Object[]> batch = new ArrayList<>();
			missingAccounts.forEach(accountIndex -> batch.add(new Object[]{wallet.getId(), accountIndex}));
			Database.get().batchUpdate("INSERT INTO address_allocation (wallet_id, account_idx, derivation_idx) VALUES (?,?,0)", batch);
		}
	}

	public AddressAllocation allocateNextAccount(ColdWallet wallet) {
		return allocateNextAddress(wallet, -1L);
	}

	public void persistPaymentAddress(AddressAllocation allocation) {
		if (Database.get().update("UPDATE address_allocation SET payment_address = ? WHERE wallet_id = ? AND account_idx = ? AND derivation_idx = ? AND payment_address IS NULL", allocation.getPaymentAddress(), allocation.getWalletId(), allocation.getAccountIndex(), allocation.getDerivationIndex()) == 0) {
			throw new IllegalStateException("Illegal state updating payment address of wallet " + allocation.getWalletId() + ", account_index " + allocation.getAccountIndex() + ", derivation index " + allocation.getDerivationIndex() + " to " + allocation.getPaymentAddress());
		}
	}

	public AddressAllocation allocateNextAddress(ColdWallet wallet, long accountIndex) {
		String query;

		if (accountIndex < 0) { //get allocation from account with smallest derivation index first
			query = "SELECT * FROM address_allocation WHERE wallet_id = ? AND available = 1 AND account_idx <> 0 ORDER BY derivation_idx, account_idx LIMIT 1";
		} else { //get allocation from given account index
			query = "SELECT * FROM address_allocation WHERE wallet_id = ? AND available = 1 AND account_idx = " + accountIndex + " ORDER BY derivation_idx, account_idx LIMIT 1";
		}

		AddressAllocation allocation = Database.get().queryForFirstObject(query, rowMapper, wallet.getId());
		if (allocation == null) {
			if (accountIndex < 0) {
				log.warn("No accounts available from wallet {}(id: {}) beyond default index 0. Allocating payment address from default wallet.", wallet.getName(), wallet.getId());
				return allocateNextAddress(wallet, 0);
			} else {
				throw new IllegalStateException("Wallet " + wallet.getName() + " has no accounts available. Unable to generate a payment address");
			}
		}

		Timestamp now = new Timestamp(System.currentTimeMillis());

		long derivation = allocation.getDerivationIndex();

		if (allocation.isAvailable()) { //available address, claim it.
			Database.get().update("UPDATE address_allocation SET available = 0, claimed_at = ? WHERE wallet_id = ? AND account_idx = ? AND derivation_idx = ?", now, wallet.getId(), allocation.getAccountIndex(), derivation);
			Database.get().insert("INSERT INTO address_allocation (wallet_id, account_idx, derivation_idx) VALUES (?,?,?)", wallet.getId(), allocation.getAccountIndex(), derivation + 1);
		} else {
			throw new IllegalStateException("Could not determine next available adress allocation of wallet " + allocation.getWalletId() + ", account_index " + allocation.getAccountIndex() + ", derivation index " + allocation.getDerivationIndex());
		}

		allocation = Database.get().queryForObject("SELECT * FROM address_allocation WHERE wallet_id = ? AND account_idx = ? AND derivation_idx = ?", rowMapper, wallet.getId(), allocation.getAccountIndex(), derivation);
		return allocation;
	}

	public List<AddressAllocation> getAddresses(ColdWallet wallet, long accountIndex) {
		String query;
		if (accountIndex < 0) { //get allocations from all accounts that are not the default 0 account
			query = "SELECT * FROM address_allocation WHERE wallet_id = ? AND account_idx <> 0 AND payment_address IS NOT NULL ORDER BY derivation_idx DESC, account_idx";
		} else { //get allocations from given account index
			query = "SELECT * FROM address_allocation WHERE wallet_id = ? AND account_idx = " + accountIndex + " AND payment_address IS NOT NULL ORDER BY derivation_idx DESC, account_idx";
		}
		return Database.get().query(query, rowMapper, wallet.getId());
	}
}

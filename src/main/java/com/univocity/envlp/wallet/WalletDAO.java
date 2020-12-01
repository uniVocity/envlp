package com.univocity.envlp.wallet;

import com.univocity.envlp.database.*;
import com.univocity.envlp.utils.*;
import org.slf4j.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.util.*;

public class WalletDAO {

	private static final Logger log = LoggerFactory.getLogger(WalletDAO.class);

	private static final RowMapper<Wallet> walletMapper = (rs, rowNum) -> {
		Wallet out = new Wallet();
		out.setId(rs.getLong("id"));
		out.setName(rs.getString("name"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		return out;
	};

	private AddressAllocationDAO addressAllocationDAO = new AddressAllocationDAO();

	public Wallet persistWallet(Wallet wallet) {
		if (wallet.accounts.isEmpty()) {
			throw new IllegalStateException("Can't persist wallet without accounts");
		}

		Map<String, Object> data = new HashMap<>();
		data.put("name", wallet.getName());

		long walletId;

		if (wallet.getId() == null) {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			wallet.setCreatedAt(Utils.toLocalDateTime(now));
			data.put("created_at", now);
			walletId = Database.get().insertReturningKey("wallet", data).longValue();
			wallet.setId(walletId);
		} else {
			walletId = wallet.getId();
			data.put("id", walletId);

			Database.get().update("wallet", data, new String[]{"id"});
		}

		List<Object[]> batch = new ArrayList<>(wallet.accounts.size());
		wallet.accounts.forEach((accountIdx, rootKey) -> batch.add(new Object[]{walletId, accountIdx, rootKey}));

		Database.get().batchUpdate("MERGE INTO wallet_account (wallet_id, account_idx, public_root_key) KEY (wallet_id, account_idx) VALUES (?,?,?);", batch);
		addressAllocationDAO.createDefaultAllocations(wallet);

		loadAccounts(wallet);

		return wallet;
	}

	public Wallet loadWallet(long walletId) {
		Wallet out = Database.get().queryForOptionalObject("SELECT * FROM wallet WHERE id = ?", walletMapper, walletId);
		loadAccounts(out);
		return out;
	}

	public List<Wallet> loadWallets() {
		Map<Long, Wallet> wallets = new TreeMap<>();
		Database.get().query("SELECT w.id, w.name, w.created_at, a.account_idx, a.public_root_key FROM wallet w JOIN wallet_account a ON w.id = a.wallet_id", (RowCallbackHandler) rs -> {
			Long walletId = rs.getLong("id");
			Wallet wallet = wallets.computeIfAbsent(walletId, k -> {
				try {
					return walletMapper.mapRow(rs, 0);
				} catch (SQLException e) {
					log.error("Error loading wallet ID " + walletId, e);
					return null;
				}
			});

			if (wallet != null) {
				wallet.addPublicRootKey(rs.getLong("account_idx"), rs.getString("public_root_key"));
			}
		});
		return new ArrayList<>(wallets.values());
	}

	private void loadAccounts(Wallet wallet) {
		if (wallet == null) {
			return;
		}
		Database.get().query("SELECT * FROM wallet_account WHERE wallet_id = ?", new Object[]{wallet.getId()}, rs -> {
			wallet.addPublicRootKey(rs.getLong("account_idx"), rs.getString("public_root_key"));
		});
	}

	public boolean deleteWallet(Long walletId) {
		if (walletId == null) {
			return false;
		}

		return Database.get().update("DELETE FROM wallet WHERE id = ?", walletId) == 1;
	}

	public boolean deleteWallet(Wallet wallet) {
		if (wallet == null) {
			return false;
		}
		return deleteWallet(wallet.getId());
	}

	public Wallet getWalletByName(String walletName) {
		Wallet out = Database.get().queryForOptionalObject("SELECT * FROM wallet WHERE name = ?", walletMapper, walletName);
		loadAccounts(out);
		return out;
	}
}
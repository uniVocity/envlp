package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.stamp.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.sql.*;
import java.util.*;

@Repository
public class WalletDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(WalletDAO.class);

	private final TokenDAO tokenDAO;
	private final WalletFormatDAO walletFormatDAO;
	private final AddressAllocationDAO addressAllocationDAO;
	private final ExternalWalletProviderDAO externalWalletProviderDAO;

	private final RowMapper<WalletSnapshot> walletMapper;

	@Autowired
	public WalletDAO(AddressAllocationDAO addressAllocationDAO, TokenDAO tokenDAO, ExternalWalletProviderDAO externalWalletProviderDAO, WalletFormatDAO walletFormatDAO) {
		this.addressAllocationDAO = addressAllocationDAO;
		this.tokenDAO = tokenDAO;
		this.externalWalletProviderDAO = externalWalletProviderDAO;
		this.walletFormatDAO = walletFormatDAO;

		walletMapper = (rs, rowNum) -> {
			EnvlpWalletFormat format = walletFormatDAO.getWalletFormatById(rs.getLong("format_id"));
			ExternalWalletProvider externalWalletProvider = this.externalWalletProviderDAO.getWalletProviderById(rs.getLong("external_wallet_provider_id"));

			WalletSnapshot out = new WalletSnapshot(rs.getLong("id"), rs.getString("name"), format, externalWalletProvider);
			out.setExternalWalletId(rs.getString("external_wallet_id"));

			out.setAccountBalance(rs.getBigDecimal("account_balance"));
			out.setRewardsBalance(rs.getBigDecimal("rewards_balance"));

			out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
			out.setUpdatedAt(Utils.toLocalDateTime(rs.getTimestamp("updated_at")));
			return out;
		};
	}

	public WalletSnapshot persistWallet(WalletSnapshot wallet) {
		if (wallet.getAccounts().isEmpty()) {
			throw new IllegalStateException("Can't persist wallet without accounts");
		}

		Map<String, Object> data = new HashMap<>();
		data.put("token_id", wallet.getToken().getId());
		data.put("name", wallet.getName());
		data.put("external_wallet_id", wallet.getExternalWalletId());
		data.put("external_wallet_provider_id", wallet.getExternalWalletProvider().getId());
		data.put("format_id", wallet.getFormat().getId());
		data.put("account_balance", wallet.getAccountBalance());
		data.put("rewards_balance", wallet.getRewardsBalance());


		long id = wallet.getId();
		if (id == 0) {
			id = db().insertReturningKey("wallet_snapshot", "id", data).longValue();
		} else {
			data.put("id", id);
			db().update("wallet_snapshot", data, new String[]{"id"});
		}

		List<Object[]> batch = new ArrayList<>(wallet.getAccounts().size());

		final long walletId = id;
		wallet.getAccounts().forEach((accountIdx, rootKey) -> batch.add(new Object[]{walletId, accountIdx, rootKey}));

		db().batchUpdate("MERGE INTO wallet_account (wallet_id, account_idx, public_root_key) KEY (wallet_id, account_idx) VALUES (?,?,?);", batch);
		wallet = db().queryForObject("SELECT * FROM wallet_snapshot WHERE id = ?", walletMapper, walletId);
		addressAllocationDAO.createDefaultAllocations(wallet);

		loadAccounts(wallet);

		return wallet;
	}

	public WalletSnapshot loadWallet(long walletId) {
		WalletSnapshot out = db().queryForOptionalObject("SELECT * FROM wallet_snapshot WHERE id = ?", walletMapper, walletId);
		loadAccounts(out);
		return out;
	}

	public List<WalletSnapshot> loadWallets() {
		Map<Long, WalletSnapshot> wallets = new TreeMap<>();
		db().query("SELECT w.*, a.account_idx, a.public_root_key FROM wallet_snapshot w JOIN wallet_account a ON w.id = a.wallet_id", (RowCallbackHandler) rs -> {
			Long walletId = rs.getLong("id");
			WalletSnapshot wallet = wallets.computeIfAbsent(walletId, k -> {
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

	private void loadAccounts(WalletSnapshot wallet) {
		if (wallet == null) {
			return;
		}
		db().query("SELECT * FROM wallet_account WHERE wallet_id = ?", rs -> {
			wallet.addPublicRootKey(rs.getLong("account_idx"), rs.getString("public_root_key"));
		}, wallet.getId());
	}

	public boolean deleteWallet(Long walletId) {
		if (walletId == null) {
			return false;
		}

		return db().update("DELETE FROM wallet_snapshot WHERE id = ?", walletId) == 1;
	}

	public boolean deleteWallet(WalletSnapshot wallet) {
		if (wallet == null) {
			return false;
		}
		return deleteWallet(wallet.getId());
	}

	public WalletSnapshot getWalletByName(String walletName) {
		WalletSnapshot out = db().queryForOptionalObject("SELECT * FROM wallet_snapshot WHERE name = ?", walletMapper, walletName);
		loadAccounts(out);
		return out;
	}

	public void associateExternalWallet(WalletSnapshot walletSnapshot, ExternalWallet externalWallet) {
		walletSnapshot.setExternalWalletId(externalWallet.id());
		db().update("UPDATE wallet_snapshot SET external_wallet_id = ? WHERE id = ?", externalWallet.id(), walletSnapshot.getId());
	}
}

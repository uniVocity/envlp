package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.definition.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class WalletFormatDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(WalletFormatDAO.class);

	private TokenDAO tokenDAO;

	@Autowired
	public WalletFormatDAO(TokenDAO tokenDAO) {
		this.tokenDAO = tokenDAO;
	}

	private final RowMapper<EnvlpWalletFormat> walletFormatRowMapper = (rs, rowNum) -> {
		EnvlpToken token = tokenDAO.getTokenById(rs.getLong("token_id"));
		EnvlpWalletFormat out = new EnvlpWalletFormat(rs.getLong("id"), token);
		out.setName(rs.getString("name"));
		out.setDescription(rs.getString("description"));
		out.setSeedLength(rs.getInt("seed_length"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		out.setUpdatedAt(Utils.toLocalDateTime(rs.getTimestamp("updated_at")));
		return out;
	};

	public EnvlpWalletFormat persistWalletFormat(EnvlpWalletFormat walletFormat) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("token_id", walletFormat.getToken().getId());
		data.put("name", walletFormat.getName());
		data.put("description", walletFormat.getDescription());
		data.put("seed_length", walletFormat.getSeedLength());

		long id = walletFormat.getId();
		if (id == 0) {
			id = db().insertReturningKey("wallet_format", "id", data).longValue();
		} else {
			data.put("id", walletFormat.getId());
			db().update("wallet_format", data, new String[]{"id"});
		}
		return getWalletFormatById(id);
	}

	public EnvlpWalletFormat getWalletFormatById(long id) {
		return db().queryForObject("SELECT * FROM wallet_format WHERE id = ?", walletFormatRowMapper, id);
	}

	public List<EnvlpWalletFormat> getWalletFormatsForToken(EnvlpToken token) {
		return db().query("SELECT * FROM wallet_format WHERE token_id = ?", walletFormatRowMapper, token.getId());
	}

	public EnvlpWalletFormat loadWalletFormat(WalletFormat walletFormat) {
		EnvlpToken token = tokenDAO.getTokenByTicker(walletFormat.getToken().getTicker());
		return db().queryForObject("SELECT * FROM wallet_format WHERE token_id = ? AND name = ? AND seed_length = ?", walletFormatRowMapper, token.getId(), walletFormat.getName(), walletFormat.getSeedLength());
	}
}

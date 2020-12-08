package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class WalletFormatDAO extends BaseDAO{

	private static final Logger log = LoggerFactory.getLogger(WalletFormatDAO.class);

	private TokenDAO tokenDAO;

	@Autowired
	WalletFormatDAO(TokenDAO tokenDAO) {
		this.tokenDAO = tokenDAO;
	}

	private final RowMapper<WalletFormat> walletFormatRowMapper = (rs, rowNum) -> {
		WalletFormat out = new WalletFormat();
		out.setId(rs.getLong("id"));
		out.setToken(tokenDAO.getTokenById(rs.getLong("token")));
		out.setName(rs.getString("name"));
		out.setDescription(rs.getString("description"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		out.setUpdatedAt(Utils.toLocalDateTime(rs.getTimestamp("updated_at")));
		return out;
	};

	public WalletFormat persistWalletFormat(WalletFormat walletFormat) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("id", walletFormat.getId());
		data.put("token_id", walletFormat.getToken().getId());
		data.put("name", walletFormat.getName());
		data.put("description", walletFormat.getDescription());

		long id = walletFormat.getId();
		if (id == 0) {
			id = db().insertReturningKey("wallet_format", data).longValue();
		} else {
			db().update("wallet_format", data, new String[]{"id"});
		}
		return getWalletFormatById(id);
	}

	public WalletFormat getWalletFormatById(long id) {
		return db().queryForObject("SELECT * FROM wallet_format WHERE id = ?", walletFormatRowMapper, id);
	}

	public List<WalletFormat> getWalletFormatsForToken(Token token) {
		return db().query("SELECT * FROM wallet_format WHERE token_id = ?", walletFormatRowMapper, token.getId());
	}
}

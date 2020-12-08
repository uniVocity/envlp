package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.database.*;
import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.slf4j.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class TokenDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(TokenDAO.class);

	private static final RowMapper<Token> tokenRowMapper = (rs, rowNum) -> {
		Token out = new Token();
		out.setId(rs.getLong("id"));
		out.setName(rs.getString("name"));
		out.setTicker(rs.getString("ticker"));
		out.setMonetarySymbol(rs.getString("monetary_symbol"));
		out.setDescription(rs.getString("description"));
		out.setAmountPattern(rs.getString("amount_pattern"));
		out.setDecimals(rs.getInt("decimals"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		out.setUpdatedAt(Utils.toLocalDateTime(rs.getTimestamp("updated_at")));
		return out;
	};

	public Token persistToken(Token token) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("name", token.getName());
		data.put("ticker", token.getTicker());
		data.put("monetary_symbol", token.getMonetarySymbol());
		data.put("description", token.getDescription());
		data.put("amount_pattern", token.getAmountPattern());
		data.put("decimals", token.getDecimals());

		long id = token.getId();
		if (id == 0) {
			id = db().insertReturningKey("token", data).longValue();
		} else {
			data.put("id", id);
			db().update("token", data, new String[]{"id"});
		}
		return getTokenById(id);
	}

	public Token getTokenById(long id) {
		return db().queryForObject("SELECT * FROM token WHERE id = ?", tokenRowMapper, id);
	}

	public List<Token> listTokens() {
		return db().query("SELECT * FROM token", tokenRowMapper);
	}
}

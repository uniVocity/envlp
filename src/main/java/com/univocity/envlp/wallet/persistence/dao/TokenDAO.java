package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.definition.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class TokenDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(TokenDAO.class);

	private static final RowMapper<EnvlpToken> tokenRowMapper = (rs, rowNum) -> {
		EnvlpToken out = new EnvlpToken(rs.getLong("id"));
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

	public EnvlpToken wrap(Token token) {
		if (token instanceof EnvlpToken) {
			return (EnvlpToken) token;
		}
		EnvlpToken out = new EnvlpToken();
		out.setAmountPattern(token.getAmountPattern());
		out.setDecimals(token.getDecimals());
		out.setDescription(token.getDescription());
		out.setTicker(token.getTicker());
		out.setMonetarySymbol(token.getMonetarySymbol());
		out.setName(token.getName());
		return out;
	}

	public EnvlpToken persistToken(EnvlpToken token) {
		long id = token.getId();
		if (id == 0) {
			try {
				return getTokenByTicker(token.getTicker());
			} catch (EmptyResultDataAccessException e) {
				//all good, let it insert.
			}
		}

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("name", token.getName());
		data.put("ticker", token.getTicker());
		data.put("monetary_symbol", token.getMonetarySymbol());
		data.put("description", token.getDescription());
		data.put("amount_pattern", token.getAmountPattern());
		data.put("decimals", token.getDecimals());


		if (id == 0) {
			id = db().insertReturningKey("token", "id", data).longValue();
		} else {
			data.put("id", id);
			db().update("token", data, new String[]{"id"});
		}
		return getTokenById(id);
	}

	public EnvlpToken getTokenById(long id) {
		return db().queryForObject("SELECT * FROM token WHERE id = ?", tokenRowMapper, id);
	}

	public EnvlpToken getTokenByTicker(String ticker) {
		return db().queryForObject("SELECT * FROM token WHERE ticker = ?", tokenRowMapper, ticker);
	}

	public List<EnvlpToken> listTokens() {
		return db().query("SELECT * FROM token", tokenRowMapper);
	}
}

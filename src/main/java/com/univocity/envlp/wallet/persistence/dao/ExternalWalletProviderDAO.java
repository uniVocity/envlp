package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.utils.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class ExternalWalletProviderDAO extends BaseDAO {

	private static RowMapper<ExternalWalletProvider> rowMapper = (rs, rowNum) -> {
		ExternalWalletProvider out = new ExternalWalletProvider();

		out.setId(rs.getLong("id"));
		out.setClassName(rs.getString("class_name"));
		out.setName(rs.getString("name"));
		out.setVersion(rs.getString("version"));
		out.setDescription(rs.getString("description"));
		out.setCreatedAt(Utils.toLocalDateTime(rs.getTimestamp("created_at")));
		out.setUpdatedAt(Utils.toLocalDateTime(rs.getTimestamp("updated_at")));

		return out;
	};

	public ExternalWalletProvider persist(ExternalWalletProvider externalWalletProvider) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("class_name", externalWalletProvider.getClassName());
		data.put("name", externalWalletProvider.getName());
		data.put("version", externalWalletProvider.getVersion());
		data.put("description", externalWalletProvider.getDescription());

		long id = externalWalletProvider.getId();
		if (id == 0) {
			id = db().insertReturningKey("external_wallet_provider", data).longValue();
		} else {
			data.put("id", id);
			db().update("external_wallet_provider", data, new String[]{"id"});
		}
		return getWalletProviderById(id);

	}

	public ExternalWalletProvider getWalletProviderById(long id) {
		return db().queryForOptionalObject("SELECT * FROM external_wallet_provider WHERE id = ?", rowMapper, id);
	}
}

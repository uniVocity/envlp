package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.envlp.database.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Repository
public abstract class BaseDAO {

	private ExtendedJdbcTemplate db;

	public ExtendedJdbcTemplate db() {
		return db;
	}

	@Autowired
	private void setDb(ExtendedJdbcTemplate db) {
		this.db = db;
	}
}

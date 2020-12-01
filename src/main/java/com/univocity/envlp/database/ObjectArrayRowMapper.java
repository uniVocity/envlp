package com.univocity.envlp.database;


import org.springframework.jdbc.core.*;

import java.sql.*;

public class ObjectArrayRowMapper implements RowMapper<Object[]> {

	private static ObjectArrayRowMapper instance = new ObjectArrayRowMapper();

	public static ObjectArrayRowMapper getInstance() {
		return instance;
	}

	private ObjectArrayRowMapper() {

	}

	@Override
	public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		int columns = rs.getMetaData().getColumnCount();
		Object[] out = new Object[columns];

		for (int i = 0; i < columns; i++) {
			out[i] = rs.getObject(i + 1);
		}

		return out;
	}
}

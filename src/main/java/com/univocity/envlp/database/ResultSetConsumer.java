

package com.univocity.envlp.database;

import java.sql.*;

public interface ResultSetConsumer {
	void consume(ResultSet rs) throws SQLException;
}

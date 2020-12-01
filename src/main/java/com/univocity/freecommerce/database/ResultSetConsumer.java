

package com.univocity.freecommerce.database;

import java.sql.*;

public interface ResultSetConsumer {
	void consume(ResultSet rs) throws SQLException;
}

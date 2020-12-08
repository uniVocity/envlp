package com.univocity.envlp.database;


import com.univocity.envlp.utils.*;
import org.slf4j.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.nio.charset.*;


/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Database {

	private static final Logger log = LoggerFactory.getLogger(Database.class);

	private final ExtendedJdbcTemplate db;

	private Database(String connectionString) {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		DataSource dataSource = new SingleConnectionDataSource(connectionString, "sa", "", false);

		db = new ExtendedJdbcTemplate(dataSource);

		createTableIfNotExists("token");
		createTableIfNotExists("wallet_format");
		createTableIfNotExists("wallet_snapshot");
		createTableIfNotExists("wallet_account");
		createTableIfNotExists("address_allocation");
	}

	public void createTableIfNotExists(final String tableName) {
		try {
			boolean exists = db.execute((StatementCallback<Boolean>) statement -> {
				try {
					db.queryForObject("SELECT count(*) FROM " + tableName + " WHERE 0 = 1", Number.class);
					return true;
				} catch (Exception e) {
					return false;
				}
			});

			if (!exists) {
				db.execute((StatementCallback<Void>) statement -> {
					String script = Utils.readTextFromResource("schema/" + tableName + ".tbl", StandardCharsets.UTF_8);
					statement.execute(script);
					return null;
				});
			}
		} catch (Exception e) {
			log.error("Error initializing database table " + tableName, e);
		}
	}

	public static ExtendedJdbcTemplate initLocal() {
		return new Database("jdbc:h2:./db/wallet").db;
	}

	public static ExtendedJdbcTemplate initTest() {
		return new Database("jdbc:h2:mem:wallet").db;
	}
}

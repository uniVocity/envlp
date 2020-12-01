package com.univocity.freecommerce.database;


import com.univocity.freecommerce.utils.*;
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
	private static Database instance;

	private final ExtendedJdbcTemplate db;

	private Database(String connectionString) {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		DataSource dataSource = new SingleConnectionDataSource(connectionString, "sa", "", false);

		db = new ExtendedJdbcTemplate(dataSource);

		createTableIfNotExists("wallet");
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

	private static synchronized void init(String url) {
		if (instance == null) {
			instance = new Database(url);
		}
	}

	public static void initLocal() {
		init("jdbc:h2:./db/wallet");
	}

	public static void initTest() {
		init("jdbc:h2:mem:wallet");
	}

	public static ExtendedJdbcTemplate get() {
		return instance.db;
	}
}

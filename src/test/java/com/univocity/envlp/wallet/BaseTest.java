package com.univocity.envlp.wallet;

import com.univocity.envlp.database.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.testng.*;

@ContextConfiguration(classes = {TestDependencies.class})
public class BaseTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private ExtendedJdbcTemplate db;

	public ExtendedJdbcTemplate db() {
		return db;
	}

	@Autowired
	private void setDb(ExtendedJdbcTemplate db) {
		this.db = db;
	}
}

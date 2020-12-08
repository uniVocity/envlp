package com.univocity.envlp.wallet.persistence.dao;

import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.envlp.*;
import com.univocity.envlp.database.*;
import org.springframework.context.annotation.*;

@Configuration
public class TestDependencies extends Dependencies {

	@Bean
	@Override
	public ExtendedJdbcTemplate db() {
		return Database.initTest();
	}

	@Bean
	@Override
	public RemoteWalletServer walletServer() {
		return null;
	}

}

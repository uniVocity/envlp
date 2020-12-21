package com.univocity.envlp;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.envlp.database.*;
import com.univocity.envlp.stamp.*;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.cardano.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import org.slf4j.*;
import org.springframework.context.annotation.*;

@Configuration
public class Dependencies {

	private static final Logger log = LoggerFactory.getLogger(Dependencies.class);

	@Bean
	public Main main(){
		return new Main(configuration(), walletService(), walletServer());
	}



	@Bean
	public ExtendedJdbcTemplate db() {
		return Database.initLocal();
	}

	@Bean
	public WalletFormatDAO walletFormatDAO(){
		return new WalletFormatDAO(tokenDAO());
	}

	@Bean
	public TokenDAO tokenDAO() {
		return new TokenDAO();
	}

	@Bean
	public AddressManager addressManager() {
		return new AddressManager(configuration().getCardanoToolsDirPath());
	}

	@Bean
	public AddressAllocationDAO addressAllocationDAO() {
		return new AddressAllocationDAO();
	}

	@Bean
	public ExternalWalletProviderDAO externalWalletProviderDAO() {
		return new ExternalWalletProviderDAO();
	}

	@Bean
	public WalletDAO walletDAO() {
		return new WalletDAO(addressAllocationDAO(), tokenDAO(), externalWalletProviderDAO(), walletFormatDAO());
	}

	@Bean
	public WalletSnapshotService walletSnapshotService() {
		return new WalletSnapshotService(addressManager(), walletDAO(), addressAllocationDAO());
	}

	//TODO: refactor to load external wallet support dynamically
	@Bean
	public CardanoWalletBackendService externalWalletService() {
		return new CardanoWalletBackendService(walletServer());
	}

	//TODO: refactor to load external wallet support dynamically
	@Bean
	public WalletService walletService() {
		return new WalletService(walletSnapshotService(), externalWalletService(), tokenDAO(), walletFormatDAO(), externalWalletProviderDAO());
	}



}

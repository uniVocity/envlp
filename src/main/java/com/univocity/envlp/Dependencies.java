package com.univocity.envlp;

import com.univocity.cardano.wallet.addresses.*;
import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.envlp.database.*;
import com.univocity.envlp.wallet.*;
import com.univocity.envlp.wallet.cardano.*;
import com.univocity.envlp.wallet.definition.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import org.slf4j.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
public class Dependencies {

	private static final Logger log = LoggerFactory.getLogger(Dependencies.class);

	@Bean
	public Main main() {
		return new Main(configuration(), walletService(), walletServer());
	}

	@Bean
	public WalletConfiguration configuration() {
		return new WalletConfiguration();
	}

	@Bean
	public ExtendedJdbcTemplate db() {
		return Database.initLocal();
	}

	@Bean
	public WalletFormatDAO walletFormatDAO() {
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
	public ExternalWalletService<?, ?> externalWalletService() {
		return new CardanoWalletBackendService(walletServer());
	}

	//TODO: refactor to load external wallet support dynamically
	@Bean
	public WalletService walletService() {
		ExternalWalletService<?, ?> service = externalWalletService();
		log.info("Initializing wallet backend '{}'", service.description());

		Set<? extends WalletFormat> walletFormats = service.supportedWalletFormats();
		for (WalletFormat format : walletFormats) {
			log.info("Enabling support for {}", format.printDetails());
			walletFormatDAO().persistWalletFormat(walletFormatDAO().wrap(format));
		}

		return new WalletService(walletSnapshotService(), externalWalletService(), tokenDAO(), walletFormatDAO(), externalWalletProviderDAO());
	}

	//TODO: refactor to move into CardanoWalletBackendService
	@Bean
	public RemoteWalletServer walletServer() {
		RemoteWalletServer walletServer;
		WalletConfiguration config = configuration();
		int port = config.getCardanoNodePort();
		if (port == -1) {
			walletServer = WalletServer.remote("localhost").connectToPort(config.getWalletServicePort());
		} else {
			EmbeddedWalletServer server = WalletServer.embedded()
					.binariesIn(config.getCardanoToolsDirPath())
					.mainnetNode()
					.configuration(config.getNodeConfigurationFilePath())
					.topology(config.getTopologyFilePath())
					.storeBlockchainIn(config.getBlockchainDirPath())
					.port(config.getCardanoNodePort())
					.ignoreOutput()
					.wallet()
					.enableHttps()
					.port(config.getWalletServicePort())
					.ignoreOutput();

			walletServer = server;

			server.start();
			log.info("Network clock details: " + walletServer.network().clock());
		}
		return walletServer;
	}

}

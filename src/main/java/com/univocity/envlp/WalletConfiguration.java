package com.univocity.envlp;

import com.univocity.cardano.wallet.embedded.services.*;
import com.univocity.cardano.wallet.exception.*;
import com.univocity.envlp.utils.*;
import org.apache.commons.lang3.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class WalletConfiguration {
	private static final Logger log = LoggerFactory.getLogger(WalletConfiguration.class);

	private final PropertyBasedConfiguration config;

	private final Map<String, String> dirPaths = new ConcurrentHashMap<>();
	private final Map<String, Integer> ports = new ConcurrentHashMap<>();

	private File applicationDir;
	private File configurationDir;
	private final File cardanoToolsDir;

	WalletConfiguration() {
		config = new PropertyBasedConfiguration("config/application.properties", "application.properties");
		String toolsDir = config.getProperty("cardano.tools.dir");

		log.debug("Looking for cardano tools");
		URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();

		String path = location.getFile();
		boolean isJar = path.endsWith(".jar");
		if (isJar) {
			path = new File(path).getParentFile().getParent();
		}

		if (!new File(path).exists()) {
			path = ".";
		} else {
			log.debug("Starting from " + path);
		}

		if (StringUtils.isNotBlank(toolsDir)) {
			cardanoToolsDir = new File(toolsDir).getAbsoluteFile();
		} else {

			String cli;
			if (SystemUtils.IS_OS_WINDOWS) {
				cli = "/cli/win/";
			} else if (SystemUtils.IS_OS_LINUX) {
				cli = "/cli/lin/";
			} else if (SystemUtils.IS_OS_MAC) {
				cli = "/cli/mac/";
			} else {
				log.warn("Could not determine operating system. Assuming Linux");
				cli = "/cli/lin/";
			}
			cardanoToolsDir = new File(path + cli).getAbsoluteFile();
		}

		if (cardanoToolsDir.exists()) {
			applicationDir = new File(path);
			configurationDir = applicationDir;
			if (isJar) {
				configurationDir = new File(configurationDir + "/config");
			}

			log.info("Detected tools in " + cardanoToolsDir.getAbsolutePath());

			dirPaths.put("app.dir", getApplicationDirPath());
			dirPaths.put("tools.dir", getCardanoToolsDirPath());
			dirPaths.put("config.dir", getConfigurationDirPath());

			dirPaths.forEach(System::setProperty);
		} else {
			log.error("Unable to find cardano tools, please configure the path to cardano tools manually");
			throw new CardanoToolsNotFoundException(cardanoToolsDir);
		}
	}

	public File getBlockchainDir() {
		return config.getDirectory("blockchain.dir", true, true, false);
	}

	public String getBlockchainDirPath() {
		return getBlockchainDir().getAbsolutePath();
	}

	public File getCardanoToolsDir() {
		return cardanoToolsDir;
	}

	public String getCardanoToolsDirPath() {
		return getCardanoToolsDir().getAbsolutePath();
	}

	public String getApplicationDirPath() {
		return applicationDir.getAbsolutePath();
	}

	public String getConfigurationDirPath() {
		return configurationDir.getAbsolutePath();
	}

	public void replaceParameters(ProcessManager processManager) {
		processManager.setCommandParameterValidationEnabled(false);
		processManager.getCommandParameters().forEach(parameter -> processManager.setCommandParameter(parameter, config.getOptionalProperty(parameter)));
		dirPaths.forEach(processManager::setCommandParameter);
	}

	public int getWalletServicePort() {
		return getPort("cardano.wallet.port", 3002);
	}

	public String getWalletServiceBaseUrl() {
		return "https://localhost:" + getWalletServicePort();
	}

	public int getCardanoNodePort() {
		return getPort("cardano.node.port", 3001);
	}

	private int getPort(String portProperty, int defaultPort) {
		Integer out = ports.get(portProperty);
		if (out == null) {
			synchronized (ports) {
				out = ports.get(portProperty);
				if(out == null) {
					String port = config.getProperty(portProperty);
					try {
						if ("random".equalsIgnoreCase(port)) {
							out = Utils.randomPortNumber();
						} else {
							out = Integer.parseInt(port);
						}
					} catch (Exception e) {
						log.warn("Could not read property '" + portProperty + "' value '" + port + "', using default " + defaultPort, e);
						out = defaultPort;
					}
					ports.put(portProperty, out);
				}
			}
		}
		return out;
	}

	public String getTopologyFilePath() {
		return configurationDir.toPath().resolve("mainnet-topology.json").toFile().getAbsolutePath();
	}

	public String getNodeConfigurationFilePath() {
		return configurationDir.toPath().resolve("mainnet-config.json").toFile().getAbsolutePath();
	}
}

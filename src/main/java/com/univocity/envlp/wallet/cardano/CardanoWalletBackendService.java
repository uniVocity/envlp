package com.univocity.envlp.wallet.cardano;

import com.univocity.cardano.wallet.builders.server.*;
import com.univocity.cardano.wallet.builders.wallets.*;
import com.univocity.envlp.stamp.*;

import java.util.*;

import static com.univocity.envlp.wallet.cardano.CardanoWalletFormat.*;

public class CardanoWalletBackendService implements ExternalWalletService<CardanoWalletFormat, CardanoWalletRestorationDetails> {

	//FIXME: this is now a mixed bag. All cardano-specific code to be repackaged and loaded independently.
	private RemoteWalletServer server;

	public CardanoWalletBackendService(RemoteWalletServer server) {
		this.server = server;
	}

	@Override
	public Set<CardanoWalletFormat> supportedWalletFormats() {
		return CardanoWalletFormat.formats;
	}

	@Override
	public ExternalWallet loadWallet(String id) {
		Wallet wallet = server.wallets().getById(id);
		return null;
	}

	@Override
	public ExternalWallet createOrRestoreWallet(CardanoWalletRestorationDetails details) {
		String formatName = details.walletFormat().getName();
		if(SHELLEY.equals(formatName)){
			server.wallets().createOrGet(details.walletName()).shelley().fromSeed(details.seed()).password(details.password());
		} else if(BYRON.equals(formatName)){
			server.wallets().createOrGet(details.walletName()).byron().fromSeed(details.seed()).password(details.password());
		} else {
			throw new IllegalArgumentException("Unsupported wallet format name: " + formatName);
		}

		return null;
	}
}

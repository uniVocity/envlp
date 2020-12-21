package com.univocity.envlp.wallet.definition;

import java.util.*;

public interface ExternalWalletService<W extends WalletFormat, T extends WalletRestorationDetails> {

	Set<W> supportedWalletFormats();

	ExternalWallet loadWallet(String id);

	ExternalWallet createOrRestoreWallet(T details);

	default W getWalletFormat(WalletFormat format){
		for(W w : supportedWalletFormats()){
			if(w.getSeedLength() == format.getSeedLength() && w.getName().equals(format.getName()) && w.getToken().equals(format.getToken())){
				return w;
			}
		}
		return null;
	}

}

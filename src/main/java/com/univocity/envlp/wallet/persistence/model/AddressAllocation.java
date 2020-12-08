package com.univocity.envlp.wallet.persistence.model;

import java.time.*;

public class AddressAllocation {

	private long walletId;
	private long accountIndex;
	private long derivationIndex;
	private String paymentAddress;
	private boolean available;
	private LocalDateTime createdAt;
	private LocalDateTime claimedAt;

	public long getWalletId() {
		return walletId;
	}

	public void setWalletId(long walletId) {
		this.walletId = walletId;
	}

	public long getAccountIndex() {
		return accountIndex;
	}

	public void setAccountIndex(long accountIndex) {
		this.accountIndex = accountIndex;
	}

	public long getDerivationIndex() {
		return derivationIndex;
	}

	public void setDerivationIndex(long derivationIndex) {
		this.derivationIndex = derivationIndex;
	}

	public String getPaymentAddress() {
		return paymentAddress;
	}

	public void setPaymentAddress(String paymentAddress) {
		this.paymentAddress = paymentAddress;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getClaimedAt() {
		return claimedAt;
	}

	public void setClaimedAt(LocalDateTime claimedAt) {
		this.claimedAt = claimedAt;
	}
}

package com.univocity.envlp.wallet.persistence.model;

import org.apache.commons.lang3.*;

import java.math.*;
import java.time.*;
import java.util.*;

public class WalletSnapshot {

	private long id;
	private Token token;

	private String name;
	private String externalWalletId;
	private ExternalWalletProvider externalWalletProvider;
	private WalletFormat format;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;


	private BigDecimal accountBalance;
	private BigDecimal rewardsBalance;

	private final Map<Long, String> accounts = new TreeMap<>();

	public WalletSnapshot() {

	}

	public WalletSnapshot(String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Wallet name cannot be null/blank");
		}
		this.name = name;
	}

	public void addPublicRootKey(long accountIndex, String publicRootKey) {
		if (accountIndex < 0) {
			throw new IllegalArgumentException("Account index of public root key of wallet '" + name + "' cannot be negative");
		}
		if (StringUtils.isBlank(publicRootKey)) {
			throw new IllegalArgumentException("Public root key of wallet '" + name + "' cannot be null/blank");
		}
		getAccounts().putIfAbsent(accountIndex, publicRootKey);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<String> getAccountPublicRootKeys() {
		return new ArrayList<>(getAccounts().values());
	}

	public String getExternalWalletId() {
		return externalWalletId;
	}

	public void setExternalWalletId(String externalWalletId) {
		this.externalWalletId = externalWalletId;
	}

	public boolean isExternal() {
		return StringUtils.isNotBlank(externalWalletId);
	}

	public WalletFormat getFormat() {
		return format;
	}

	public void setFormat(WalletFormat format) {
		this.format = format;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public BigDecimal getTotalBalance(){
		return getAccountBalance().add(getRewardsBalance());
	}

	public String getFormattedTotalBalance() {
		return token.getFormattedAmount(getTotalBalance());
	}

	public BigDecimal getAccountBalance(){
		return accountBalance == null ? BigDecimal.ZERO : accountBalance;
	}

	public String getFormattedAccountBalance() {
		return token.getFormattedAmount(getAccountBalance());
	}

	public BigDecimal getRewardsBalance(){
		return rewardsBalance == null ? BigDecimal.ZERO : rewardsBalance;
	}

	public String getFormattedRewardsBalance() {
		return token.getFormattedAmount(getRewardsBalance());
	}

	public Map<Long, String> getAccounts() {
		return accounts;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ExternalWalletProvider getExternalWalletProvider() {
		return externalWalletProvider;
	}

	public void setExternalWalletProvider(ExternalWalletProvider externalWalletProvider) {
		this.externalWalletProvider = externalWalletProvider;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public void setRewardsBalance(BigDecimal rewardsBalance) {
		this.rewardsBalance = rewardsBalance;
	}
}
package com.univocity.envlp.wallet;

import org.apache.commons.lang3.*;

import java.time.*;
import java.util.*;

public class ColdWallet {

	private Long id;
	private String name;
	private LocalDateTime createdAt;
	private String hotWalletId;

	final Map<Long, String> accounts = new TreeMap<>();

	ColdWallet() {

	}

	public ColdWallet(String name) {
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
		accounts.putIfAbsent(accountIndex, publicRootKey);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<String> getAccountPublicRootKeys() {
		return new ArrayList<>(accounts.values());
	}

	public String getHotWalletId() {
		return hotWalletId;
	}

	public void setHotWalletId(String hotWalletId) {
		this.hotWalletId = hotWalletId;
	}

	public boolean canBeHot() {
		return StringUtils.isNotBlank(hotWalletId);
	}
}

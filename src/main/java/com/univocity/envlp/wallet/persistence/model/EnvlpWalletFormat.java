package com.univocity.envlp.wallet.persistence.model;

import com.univocity.envlp.wallet.definition.*;

import java.time.*;

public class EnvlpWalletFormat implements WalletFormat {

	private final long id;
	private final EnvlpToken token;

	private String name;
	private String description;
	private int seedLength;
	private boolean legacy;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public EnvlpWalletFormat(EnvlpToken token) {
		this(0, token);
	}

	public EnvlpWalletFormat(long id, EnvlpToken token) {
		this.id = id;
		if (token == null) {
			throw new IllegalStateException("Token information can't be null");
		}
		this.token = token;
	}

	public long getId() {
		return id;
	}

	@Override
	public EnvlpToken getToken() {
		return token;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getSeedLength() {
		return seedLength;
	}

	public void setSeedLength(int seedLength) {
		this.seedLength = seedLength;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean isLegacyFormat() {
		return legacy;
	}

	public void setLegacyFormat(boolean legacy) {
		this.legacy = legacy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EnvlpWalletFormat that = (EnvlpWalletFormat) o;

		if (id == 0 || that.id == 0) {
			return WalletFormat.super.equals(that);
		}

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}

package com.univocity.envlp.wallet.persistence.model;



import com.univocity.envlp.wallet.definition.*;

import java.time.*;

public class EnvlpToken implements Token {

	private final long id;
	private String name;
	private String ticker;
	private String monetarySymbol;
	private String description;
	private String amountPattern;
	private int decimals;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public EnvlpToken() {
		this(0);
	}

	public EnvlpToken(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmountPattern() {
		return amountPattern;
	}

	public void setAmountPattern(String amountPattern) {
		this.amountPattern = amountPattern;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public String getMonetarySymbol() {
		return monetarySymbol;
	}

	public void setMonetarySymbol(String monetarySymbol) {
		this.monetarySymbol = monetarySymbol;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EnvlpToken that = (EnvlpToken) o;

		if (id == 0 || that.id == 0) {
			return Token.super.equals(that);
		}

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}

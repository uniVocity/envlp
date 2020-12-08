package com.univocity.envlp.wallet.persistence.model;

import java.math.*;
import java.text.*;
import java.time.*;

public class Token {

	private final long id;
	private String name;
	private String ticker;
	private String monetarySymbol;
	private String description;
	private String amountPattern;
	private int decimals;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private ThreadLocal<DecimalFormat> priceFormatter;

	public Token() {
		this(0);
	}

	public Token(long id) {
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
		this.priceFormatter = ThreadLocal.withInitial(() -> new DecimalFormat(amountPattern));
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

	public String getFormattedAmount(BigDecimal amount) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		if (priceFormatter == null) {
			return round(amount).toPlainString();
		}
		return priceFormatter.get().format(amount);
	}

	private BigDecimal round(BigDecimal amount) {
		if (amount.scale() != this.decimals) {
			amount = amount.setScale(decimals, RoundingMode.HALF_EVEN);
		}
		return amount;
	}
}

package com.univocity.envlp.wallet.persistence.model;

import java.time.*;

public class WalletFormat {

	private final long id;
	private Token token;

	private String name;
	private String description;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public WalletFormat(long id, Token token) {
		this.id = id;
		if(token == null){
			throw new IllegalStateException("Token information can't be null");
		}
		this.token = token;
	}

	public long getId() {
		return id;
	}

	public Token getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}

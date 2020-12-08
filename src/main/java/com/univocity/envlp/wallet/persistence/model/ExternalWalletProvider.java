package com.univocity.envlp.wallet.persistence.model;

import java.time.*;

public class ExternalWalletProvider {

	private final long id;
	private String className;
	private String name;
	private String version;
	private String description;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ExternalWalletProvider() {
		this(0);
	}

	public ExternalWalletProvider(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

package com.sg.client.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientAccountBuilder {

	private float balance;

	private String accountId;

	private List<Operation> operations;

	public float getBalance() {
		return balance;
	}

	public String getAccountId() {
		return accountId;
	}

	public List<Operation> getOperations() {
		return new ArrayList<>(Collections.unmodifiableCollection(operations));
	}
	
	public ClientAccountBuilder from(ClientAccount clientAccount) {
		this.balance = clientAccount.getBalance();
		this.accountId = clientAccount.getAccountId();
		this.operations = clientAccount.getOperations();
		return this;
	}
	
	public ClientAccountBuilder withBalance(float balance) {
		this.balance = balance;
		return this;
	}
	
	public ClientAccount build() {
		return new ClientAccount(this);
	}
}

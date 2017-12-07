package com.sg.client.account;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAccount {
	
    public enum ClientAccountOperations {
        INITIAL_DEPOSIT, CREDIT, DEBIT
    }
    
	private float balance;

	private String accountId;

	private List<Operation> operations;
	
	public ClientAccount() {
		super();
	}
	
	public ClientAccount(String accountId, float initialDeposit) {
        super();
        this.accountId = accountId;
        this.balance = initialDeposit;
        operations = new LinkedList<>();
        operations.add(new Operation(ClientAccountOperations.INITIAL_DEPOSIT, new Date(), initialDeposit));
    }

    public ClientAccount(ClientAccountBuilder clientAccountBuilder) {
		this.balance = clientAccountBuilder.getBalance();
		this.accountId = clientAccountBuilder.getAccountId();
		this.operations = clientAccountBuilder.getOperations();
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public float getBalance() {
		return balance;
	}

	public String getAccountId() {
		return accountId;
	}
	
	@Override
	public String toString() {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return "balance : " + balance + "\n"
                + operations.stream().map(operation -> "operation : " + operation.getType().toString() 
                                                                + " - date : "+ formatter.format(operation.getDate()) 
                                                                + " - amount : " + operation.getAmount())
                                            .collect(Collectors.joining("\n"));
	} 
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientAccount other = (ClientAccount) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        return true;
    }
}

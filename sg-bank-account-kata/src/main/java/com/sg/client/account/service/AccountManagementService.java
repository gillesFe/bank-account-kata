package com.sg.client.account.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.sg.client.account.ClientAccount;
import com.sg.client.account.ClientAccount.ClientAccountOperations;
import com.sg.client.account.ClientAccountBuilder;
import com.sg.client.account.Operation;
import com.sg.client.account.dao.AccountDao;
import com.sg.client.account.dao.JsonAccountDao;

public class AccountManagementService {

    AccountDao accountDao;
    
    public AccountManagementService() {
        accountDao = new JsonAccountDao();
    }
    
    public Optional<ClientAccount> getAccountById(String clientAccountId) {
        return accountDao.getClientAccountByID(clientAccountId);
    }

    public void creditAccount(String clientAccountId, float amountToCredit) {
        Optional<ClientAccount> clientAccount = accountDao.getClientAccountByID(clientAccountId);

        if (clientAccount.isPresent()) {
            float balanceAfterCredit = getBalanceAfterCredit(amountToCredit, clientAccount.get());
            getOperations(clientAccount.get()).add(new Operation(ClientAccountOperations.CREDIT, new Date(), amountToCredit));
            accountDao.updateClientAccount(getClientAccountUpdated(clientAccount.get(), balanceAfterCredit));
        }
    }

    public void debitAccount(String clientAccountId, float amountToDebit) {
        Optional<ClientAccount> clientAccount = accountDao.getClientAccountByID(clientAccountId);

        float balanceAfterDebit = 0;
        if (clientAccount.isPresent()) {
            balanceAfterDebit = getBalanceAfterDebit(amountToDebit, clientAccount.get());
        }

        if (clientAccount.isPresent() && isPositiveBalance(balanceAfterDebit)) {
        	getOperations(clientAccount.get()).add(new Operation(ClientAccountOperations.DEBIT, new Date(), amountToDebit));
            accountDao.updateClientAccount(getClientAccountUpdated(clientAccount.get(), balanceAfterDebit));
        }

    }

    private float getBalanceAfterDebit(float amountToDebit, ClientAccount clientAccount) {
        return clientAccount.getBalance() - amountToDebit;
    }

    private float getBalanceAfterCredit(float amountToDebit, ClientAccount clientAccount) {
        return clientAccount.getBalance() + amountToDebit;
    }

    private List<Operation> getOperations(ClientAccount clientAccountFound) {
		return clientAccountFound.getOperations();
	}

	private ClientAccount getClientAccountUpdated(ClientAccount clientAccountFound, float newBalance) {
		return new ClientAccountBuilder().from(clientAccountFound).withBalance(newBalance).build();
	}

    private boolean isPositiveBalance(float newBalance) {
        return newBalance > 0;
    }
}

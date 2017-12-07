package com.sg.client.account.dao;

import java.util.Optional;

import com.sg.client.account.ClientAccount;

public interface AccountDao {
    public Optional<ClientAccount> getClientAccountByID(String clientAccount);
    
    public void updateClientAccount(ClientAccount clientAccount);
}

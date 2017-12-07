package com.sg.client.account.dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sg.client.account.ClientAccount;

public class JsonAccountDao implements AccountDao {
    
    private static final Logger logger =  LoggerFactory.getLogger(JsonAccountDao.class);
    private ObjectMapper mapper;
    
    public JsonAccountDao() {
        this.mapper = new ObjectMapper();
    }

    public Optional<ClientAccount> getClientAccountByID(final String clientAccountId) {
        try {
            List<ClientAccount> clientAccounts = mapper.readValue(new File(getClientAccountJsonPath()), new TypeReference<List<ClientAccount>>(){});
            return clientAccounts.stream().filter(currentAccount -> currentAccount.getAccountId().equals(clientAccountId)).findFirst();
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
            return Optional.empty();
        }
    }

    public void updateClientAccount(ClientAccount clientAccount) {
        try {
            List<ClientAccount> clientAccounts = mapper.readValue(new File(getClientAccountJsonPath()), new TypeReference<List<ClientAccount>>(){});
            clientAccounts.remove(clientAccount);
            clientAccounts.add(clientAccount);
            mapper.writeValue(new File(getClientAccountJsonPath()), clientAccounts);
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }
        
    }

    private String getClientAccountJsonPath () {
        try {
            URL url = getClassLoader().getResource("accounts_clients.json");
            if (url != null) {
                return Paths.get(url.toURI()).toString();
            }
            return "";
        } catch (URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return "";
        }
    }
    
    private ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }
}

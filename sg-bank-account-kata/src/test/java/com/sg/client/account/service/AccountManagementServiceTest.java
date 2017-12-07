package com.sg.client.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.sg.client.account.ClientAccount;
import com.sg.client.account.ClientAccount.ClientAccountOperations;
import com.sg.client.account.Operation;

public class AccountManagementServiceTest {
	
    private static final String HISTORY_OPERATIONS = "balance : 25.0" + "\n" + "operation : INITIAL_DEPOSIT - date : 2017-12-07 - amount : 30.0" + "\n" + "operation : CREDIT - date : 2017-12-07 - amount : 20.0" + "\n" + "operation : DEBIT - date : 2017-12-07 - amount : 25.0";

    private static final String ACCOUNTS_CLIENTS_JSON_FILE = "accounts_clients.json";
    
    private static final float INITIAL_DEPOSIT_AMOUNT = 30;
    
    private static final float AMOUNT_TO_CREDIT = 20;
    
    private static final float AMOUNT_TO_DEBIT = 25;
    
    private static final float AMOUNT_MORE_IMPORTANT_THAN_BALANCE = 40;
    
    private static final String A_CLIENT_ACCOUNT_ID = "123456789";
    
    private static final String UNKOWN_ACCOUNT_ID = "UNKOWN_ACCOUNT_ID";
	
    private AccountManagementService accountManagementService;

    @Before
    public void setUp() throws Exception {
        accountManagementService = new AccountManagementService();
        initClientAccountsJsonTest();
    }

    private void initClientAccountsJsonTest() throws IOException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        String path = Paths.get(classLoader.getResource(ACCOUNTS_CLIENTS_JSON_FILE).toURI()).toString();
        mapper.writeValue(new File(path), Arrays.asList(new ClientAccount(A_CLIENT_ACCOUNT_ID, INITIAL_DEPOSIT_AMOUNT)));
    }
	
	@Test
    public void should_return_a_client_account() throws Exception {
        Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(A_CLIENT_ACCOUNT_ID);
        
        assertThat(clientAccount).isNotEmpty();
        assertThat(clientAccount.get().getAccountId()).isEqualTo(A_CLIENT_ACCOUNT_ID);
    }
	
	@Test
    public void should_return_empty_client_account_when_account_id_is_not_found() throws Exception {
        Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(UNKOWN_ACCOUNT_ID);
        
        assertThat(clientAccount).isEmpty();
    }
	
	@Test
	public void should_credit_client_account() throws Exception {
		accountManagementService.creditAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_TO_CREDIT);
		
		Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(A_CLIENT_ACCOUNT_ID);
		assertCreditAccount(clientAccount);
	}
	
	@Test
    public void should_debit_client_account_when_balance_is_pretty_full() throws Exception {
        accountManagementService.debitAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_TO_DEBIT);
        
        Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(A_CLIENT_ACCOUNT_ID);
        assertDebitAccount(clientAccount);
    }
	
	@Test
    public void should_not_debit_client_account_when_balance_is_insufficient() throws Exception {
        accountManagementService.debitAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_MORE_IMPORTANT_THAN_BALANCE);
        
        Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(A_CLIENT_ACCOUNT_ID);
        assertSameBalance(clientAccount);
    }
	
	@Test
    public void should_print_history_operations() throws Exception {
	    accountManagementService.creditAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_TO_CREDIT);
	    accountManagementService.debitAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_TO_DEBIT);
	    accountManagementService.debitAccount(A_CLIENT_ACCOUNT_ID, AMOUNT_MORE_IMPORTANT_THAN_BALANCE);
	    
	    Optional<ClientAccount> clientAccount = accountManagementService.getAccountById(A_CLIENT_ACCOUNT_ID);
	    
	    assertHistoryOperations(clientAccount);
    }

    private void assertHistoryOperations(Optional<ClientAccount> clientAccount) {
        assertThat(clientAccount).isNotEmpty();
	    ClientAccount clientAccountFound = clientAccount.get();
	    assertThat(clientAccountFound.getOperations().size()).isEqualTo(3);
	    assertThat(clientAccountFound.toString()).isEqualTo(HISTORY_OPERATIONS);
	    System.out.println(clientAccountFound.toString());
    }

    private void assertCreditAccount(Optional<ClientAccount> clientAccount) {
        assertThat(clientAccount).isNotEmpty();
		ClientAccount clientAccountFound = clientAccount.get();
		assertThat(clientAccountFound.getBalance()).isEqualTo(INITIAL_DEPOSIT_AMOUNT + AMOUNT_TO_CREDIT);
		assertThat(clientAccountFound.getOperations().size()).isEqualTo(2);
		Operation operationOne = clientAccountFound.getOperations().get(0);
        assertThat(operationOne.getAmount()).isEqualTo(INITIAL_DEPOSIT_AMOUNT);
        assertThat(operationOne.getType()).isEqualTo(ClientAccountOperations.INITIAL_DEPOSIT);
		Operation operationTwo = clientAccountFound.getOperations().get(1);
		assertThat(operationTwo.getAmount()).isEqualTo(AMOUNT_TO_CREDIT);
		assertThat(operationTwo.getType()).isEqualTo(ClientAccountOperations.CREDIT);
    }
    
    private void assertDebitAccount(Optional<ClientAccount> clientAccount) {
        assertThat(clientAccount).isNotEmpty();
        ClientAccount clientAccountFound = clientAccount.get();
        assertThat(clientAccountFound.getBalance()).isEqualTo(INITIAL_DEPOSIT_AMOUNT - AMOUNT_TO_DEBIT);
        assertThat(clientAccountFound.getOperations().size()).isEqualTo(2);
        Operation operationOne = clientAccountFound.getOperations().get(0);
        assertThat(operationOne.getAmount()).isEqualTo(INITIAL_DEPOSIT_AMOUNT);
        assertThat(operationOne.getType()).isEqualTo(ClientAccountOperations.INITIAL_DEPOSIT);
        Operation operation = clientAccountFound.getOperations().get(1);
        assertThat(operation.getAmount()).isEqualTo(AMOUNT_TO_DEBIT);
        assertThat(operation.getType()).isEqualTo(ClientAccountOperations.DEBIT);
    }
    
    private void assertSameBalance(Optional<ClientAccount> clientAccount) {
        assertThat(clientAccount).isNotEmpty();
        ClientAccount clientAccountFound = clientAccount.get();
        assertThat(clientAccountFound.getBalance()).isEqualTo(INITIAL_DEPOSIT_AMOUNT);
        assertThat(clientAccountFound.getOperations().size()).isEqualTo(1);
        Operation operationOne = clientAccountFound.getOperations().get(0);
        assertThat(operationOne.getAmount()).isEqualTo(INITIAL_DEPOSIT_AMOUNT);
        assertThat(operationOne.getType()).isEqualTo(ClientAccountOperations.INITIAL_DEPOSIT);
        
    }

}

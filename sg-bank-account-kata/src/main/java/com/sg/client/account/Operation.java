package com.sg.client.account;

import java.util.Date;

import com.sg.client.account.ClientAccount.ClientAccountOperations;

public class Operation {
    private ClientAccountOperations type;
    
    private Date date;
    
    private float amount;

    public Operation(ClientAccountOperations type, Date date, float amount) {
        this.type = type;
        this.date = date;
        this.amount = amount;
    }
    
    public Operation() {
        super();
    }

    public ClientAccountOperations getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

}

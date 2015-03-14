package com.dotin.socket.client;

import com.dotin.socket.server.model.to.ServerTransactionTo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class ClientTransaction {
    private String id;
    private String amount;
    private String type;
    private String newInitialBalance;

    public String getNewInitialBalance() {
        return newInitialBalance;
    }

    public void setNewInitialBalance(String newInitialBalance) {
        this.newInitialBalance = newInitialBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setTransactionFields(String id, String amount, String type)
    {
        this.id=id;
        this.amount=amount;
        this.type=type;
    }

    public void setTransactionFields(String id, String amount, String type, String newInitialBalance)
    {
        this.id=id;
        this.amount=amount;
        this.type=type;
        this.newInitialBalance=newInitialBalance;
    }
public ClientTransaction()
{

}
}

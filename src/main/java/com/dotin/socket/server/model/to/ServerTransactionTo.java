package com.dotin.socket.server.model.to;

import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class ServerTransactionTo {
    private String id;
    private BigDecimal amount;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDepositFields(String id, BigDecimal amount, String type)
    {
        this.id=id;
        this.amount=amount;
        this.type=type;
    }
}

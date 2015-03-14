package com.dotin.socket.server.model.to;

import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/6/2015.
 */
public class DepositTo {
    private String customerName;
    private BigDecimal initialBalance;
    private BigDecimal upperBound;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(BigDecimal upperBound) {
        this.upperBound = upperBound;
    }

    public void setDepositFields(String customerName, BigDecimal initialBalance, BigDecimal upperBound)
    {
        this.customerName=customerName;
        this.upperBound=upperBound;
        this.initialBalance=initialBalance;
    }
    public DepositTo()
    {

    }
}

package com.dotin.socket.server.model.bl;

import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public class DataValidatorImpl implements DataValidator {

    public void validWithDraw(BigDecimal initialBalance, BigDecimal amount) throws NegativeDurationException
    {
        if ((initialBalance.subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeDurationException("Your initial balance is insufficient!");
        }
    }

    @Override
    public void validDeposit(BigDecimal initialBalance, BigDecimal amount, BigDecimal upperBound) throws NegativeDurationException {
        if ((initialBalance.add(amount)).compareTo(upperBound) > 0) {
            throw new NegativeDurationException("Your initial balance will overflow!");
        }
    }
}

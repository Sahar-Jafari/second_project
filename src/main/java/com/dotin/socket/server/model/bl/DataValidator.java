package com.dotin.socket.server.model.bl;

import java.math.BigDecimal;

/**
 * Created by Dotin school 6 on 3/7/2015.
 */
public interface DataValidator {
    public void validWithDraw(BigDecimal initialBalance, BigDecimal amount) throws NegativeDurationException;
    public void validDeposit(BigDecimal initialBalance, BigDecimal amount, BigDecimal upperBound) throws NegativeDurationException;

}

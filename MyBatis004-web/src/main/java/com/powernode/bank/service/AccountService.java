package com.powernode.bank.service;

import com.powernode.bank.exceptions.MoneyNotEnoughException;
import com.powernode.bank.exceptions.TransferException;

public interface AccountService {

    void transfer(String fromActno, String toActno, double money) throws MoneyNotEnoughException, TransferException;


}

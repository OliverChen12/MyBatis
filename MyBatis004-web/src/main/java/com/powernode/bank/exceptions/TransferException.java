package com.powernode.bank.exceptions;

public class TransferException extends Exception{
    public TransferException(){}
    public TransferException(String msg){
        super(msg);
    }
}

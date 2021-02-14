package org.kodluyoruz.mybank.service.transaction;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface TransferTransactionService {

    public <T extends Account> ResponseEntity<Object> sendMoney(T a, T b, TransferTransactionRequest request) throws IOException;


}
package org.kodluyoruz.mybank.service.transaction;


import java.util.*;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.request.transaction.EndTransactionDate;

import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;

public interface AccountTransactionService {

    public <T extends Account> ResponseEntity<Object> balanceOnAccount(T a, String transactionType, AccountTransactionRequest request) throws IOException;
    public <T extends Account> ResponseEntity<Object> debtOnAccount(T a, DebtOnAccountRequest request) throws IOException;
    public <T extends Account> List<AccountTransaction> findAccountTransactionByDateAndAccountNumber(T a,TransactionDate date,String accountNumber) throws Exception;
    public <T extends Account> ResponseEntity<Object> withDrawAllMoney(T a,String accountNumber) throws IOException;
}

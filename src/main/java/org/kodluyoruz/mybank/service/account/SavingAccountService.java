package org.kodluyoruz.mybank.service.account;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.request.account.CreateAccountRequest;
import org.kodluyoruz.mybank.request.account.CreateSavingAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface SavingAccountService {

    public ResponseEntity<Object> createAccount(CreateSavingAccountRequest request) throws IOException;
    public ResponseEntity<Object> addBalance(AccountTransactionRequest request) throws IOException;
    public ResponseEntity<Object> withdrawBalance(AccountTransactionRequest request) throws IOException;
    public ResponseEntity<Object> sendMoneyToDeposit(TransferTransactionRequest request) throws IOException;
    public ResponseEntity<Object> sendMoneyToSaving(TransferTransactionRequest request) throws IOException;
    public ResponseEntity<Object> deleteAccount(String accountNumber);
    public List<AccountTransaction> findTransaction(TransactionDate date, String accountNumber) throws Exception;
    public ResponseEntity<Object> withDrawAllMoney(String accountNumber) throws IOException;
}

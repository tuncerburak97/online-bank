package org.kodluyoruz.mybank.service.impl.account;


import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.request.CreateAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.kodluyoruz.mybank.service.account.AccountService;
import org.kodluyoruz.mybank.service.account.DepositAccountService;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.kodluyoruz.mybank.service.transaction.TransferTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DepositAccountServiceImpl implements DepositAccountService {

    @Autowired
    private final AccountService accountService;

    @Autowired
    private final AccountTransactionService accountTransactionService;

    @Autowired
    private final TransferTransactionService transactionService;

    public DepositAccountServiceImpl(AccountService accountService, AccountTransactionService accountTransactionService, TransferTransactionService transactionService) {

        this.accountService = accountService;
        this.accountTransactionService = accountTransactionService;

        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<Object> createAccount(CreateAccountRequest request){
        DepositAccount depositAccount = new DepositAccount();
        return accountService.createAccount(depositAccount,request);
    }

    @Override
    public ResponseEntity<Object> addBalance(AccountTransactionRequest request) throws IOException {

        DepositAccount depositAccount = new DepositAccount();
        String type="AddBalance";
        return accountTransactionService.balanceOnAccount(depositAccount,type,request);


    }

    @Override
    public ResponseEntity<Object> withdrawBalance(AccountTransactionRequest request) throws IOException {
        DepositAccount depositAccount = new DepositAccount();
        String type="WithdrawBalance";
        return accountTransactionService.balanceOnAccount(depositAccount,type,request);
    }

    @Override
    public ResponseEntity<Object> sendMoneyToDeposit(TransferTransactionRequest request)throws IOException {

        DepositAccount sender = new DepositAccount();
        DepositAccount receiver = new DepositAccount();

        return transactionService.sendMoney(sender,receiver,request);

    }

    @Override
    public ResponseEntity<Object> sendMoneyToSaving(TransferTransactionRequest request) throws IOException {
        DepositAccount sender = new DepositAccount();
        SavingAccount receiver = new SavingAccount();

        return transactionService.sendMoney(sender,receiver,request);
    }

    @Override
    public ResponseEntity<Object> deleteAccount(String accountNumber){

        DepositAccount depositAccount = new DepositAccount();
        return accountService.deleteAccount(depositAccount,accountNumber);

    }


}


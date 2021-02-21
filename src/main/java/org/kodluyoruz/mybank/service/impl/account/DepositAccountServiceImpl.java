package org.kodluyoruz.mybank.service.impl.account;


import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.payment.AutoPayment;
import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.payment.AutoPaymentRepository;
import org.kodluyoruz.mybank.request.account.CreateAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.kodluyoruz.mybank.service.account.AccountService;
import org.kodluyoruz.mybank.service.account.DepositAccountService;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.kodluyoruz.mybank.service.transaction.TransferTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

import java.io.IOException;

@Service
public class DepositAccountServiceImpl implements DepositAccountService {

    @Autowired
    private final AccountService accountService;

    @Autowired
    private final AccountTransactionService accountTransactionService;

    @Autowired
    private final TransferTransactionService transactionService;

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private AutoPaymentRepository autoPaymentRepository;

    public DepositAccountServiceImpl(AccountService accountService, AccountTransactionService accountTransactionService, TransferTransactionService transactionService) {

        this.accountService = accountService;
        this.accountTransactionService = accountTransactionService;

        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<Object> createAccount(CreateAccountRequest request) throws IOException {
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

    public List<AccountTransaction> findTransaction(TransactionDate date,String accountNumber) throws Exception {

        DepositAccount depositAccount = new DepositAccount();
        return accountTransactionService.findAccountTransactionByDateAndAccountNumber(depositAccount,date,accountNumber);
    }

    @Override
    public ResponseEntity<Object> withDrawAllMoney(String accountNumber) throws IOException {
        DepositAccount depositAccount = new DepositAccount();
        return accountTransactionService.withDrawAllMoney(depositAccount,accountNumber);

    }

    @Override
    public ResponseEntity<Object> autoPaymentRequest(String accountNumber, double amount) {

        Calendar calendar=Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DepositAccount depositAccount = depositAccountRepository.findByAccountNumber(accountNumber);

        if(depositAccount==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");

        AutoPayment autoPayment = new AutoPayment();

        autoPayment.setDepositAccount(depositAccount);
        autoPayment.setDay(day);
        autoPayment.setAmount(amount);
        autoPayment.setCurrency("TRY");

        autoPaymentRepository.save(autoPayment);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Automatic payment order has been created.");
    }


}


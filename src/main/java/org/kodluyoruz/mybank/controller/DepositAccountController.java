package org.kodluyoruz.mybank.controller;

import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.repository.transaction.AccountTransactionRepository;
import org.kodluyoruz.mybank.request.account.CreateAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.kodluyoruz.mybank.service.account.DepositAccountService;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.io.IOException;

@RestController
@RequestMapping("/customer/account/deposit")
public class DepositAccountController {

    @Autowired
    private DepositAccountService depositAccountService;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private AccountTransactionService accountTransactionService;


    @PostMapping("/create")
    public ResponseEntity<Object> createAccount(@RequestBody CreateAccountRequest request) throws IOException {
        return depositAccountService.createAccount(request);
    }
    @PostMapping("/addBalance")
    public ResponseEntity<Object> addBalance(@RequestBody AccountTransactionRequest request) throws IOException {

        try {
            return depositAccountService.addBalance(request);
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.LOCKED).body("Deposit account already used in another transaction");
        }
    }

    @PostMapping("/withdrawBalance")
    public ResponseEntity<Object> withdrawBalance(@RequestBody AccountTransactionRequest request) throws IOException {
        try {
            return depositAccountService.withdrawBalance(request);
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.LOCKED).body("Deposit account already used in another transaction");
        }
    }

    @PostMapping("/send/deposit")
    public ResponseEntity<Object> sendMoneyToDeposit(@RequestBody TransferTransactionRequest request) throws IOException {

        try {
            return depositAccountService.sendMoneyToDeposit(request);
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.LOCKED).body("This account already used in another transaction");
        }
    }

    @PostMapping("/send/saving")
    public ResponseEntity<Object> sendMoneyToSaving(@RequestBody TransferTransactionRequest request) throws IOException {

        try {
            return depositAccountService.sendMoneyToSaving(request);
        }catch (Exception e){

            return ResponseEntity.status(HttpStatus.LOCKED).body("This account already used in another transaction");
        }
    }

    @DeleteMapping("/deleteByAccountNumber/{accountNumber}")
    private ResponseEntity<Object> deleteAccount(@PathVariable String accountNumber){

        return depositAccountService.deleteAccount(accountNumber);

    }
    @GetMapping("/list/allTransactions/{accountNumber}")
    private List<AccountTransaction> findTransaction(@PathVariable String accountNumber){
        return accountTransactionRepository.findByAccountNumber(accountNumber);

    }

    @PostMapping("/list/transaction/byDate/{accountNumber}")
    public List<AccountTransaction> findAccountTransactionByDate(@RequestBody TransactionDate date, @PathVariable String accountNumber) throws Exception {

        try {
            return depositAccountService.findTransaction(date,accountNumber);
        }catch (Exception e){
            throw new Exception("Account not found");
        }
    }

}

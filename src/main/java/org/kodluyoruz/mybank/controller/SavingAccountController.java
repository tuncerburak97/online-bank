package org.kodluyoruz.mybank.controller;

import org.kodluyoruz.mybank.request.CreateAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.kodluyoruz.mybank.service.account.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("customer/account/saving")
public class SavingAccountController {

    @Autowired
    private SavingAccountService savingAccountService;

    @PostMapping("/create")
    public ResponseEntity<Object> createAccount(@RequestBody CreateAccountRequest request){
        return savingAccountService.createAccount(request);
    }
    @PostMapping("/addBalance")
    public ResponseEntity<Object> addBalance(@RequestBody AccountTransactionRequest request) throws IOException {
        return savingAccountService.addBalance(request);
    }
    @PostMapping("/withdrawBalance")
    public ResponseEntity<Object> withdrawBalance(@RequestBody AccountTransactionRequest request) throws IOException {
        return savingAccountService.withdrawBalance(request);
    }

    @PostMapping("/send/deposit")
    public ResponseEntity<Object> sendMoneyToDeposit(@RequestBody TransferTransactionRequest request) throws IOException {
        return savingAccountService.sendMoneyToDeposit(request);
    }

    @PostMapping("/send/saving")
    public ResponseEntity<Object> sendMoneyToSaving(@RequestBody TransferTransactionRequest request) throws IOException {
        return savingAccountService.sendMoneyToSaving(request);
    }

    @DeleteMapping("/deleteByAccountNumber/{accountNumber}")
    private ResponseEntity<Object> deleteAccount(@PathVariable String accountNumber){

        return savingAccountService.deleteAccount(accountNumber);

    }
}

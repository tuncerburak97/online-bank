package org.kodluyoruz.mybank.controller;

import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.service.card.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/customer/creditCard")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;


    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody CreateCreditCardRequest request){
        return creditCardService.createCredit(request);
    }

    @PostMapping("/withDrawBalance")
    public ResponseEntity<Object> withdrawBalance(@RequestBody CardTransactionRequest request) throws IOException {
        return creditCardService.withdrawBalance(request);
    }

    @PostMapping("/debtPayment/deposit")
    public ResponseEntity<Object> debtOnDeposit(@RequestBody DebtOnAccountRequest request) throws IOException {
        return creditCardService.debtOnDepositAccount(request);
    }
    @PostMapping("/debtPayment/saving")
    public ResponseEntity<Object> debtOnSaving(@RequestBody DebtOnAccountRequest request) throws IOException {
        return creditCardService.debtOnSavingAccount(request);
    }

    @PostMapping("/debtPayment/debitCard")
    public ResponseEntity<Object> debtOnDebit(@RequestBody DebtOnCardRequest request) throws IOException {
        return creditCardService.debtOnDebitCard(request);
    }

    @GetMapping("/debtInquiry/{cardNumber}")
    public ResponseEntity<Object> debtInquiry(@PathVariable String cardNumber){

        return creditCardService.debtInquiry(cardNumber);
    }

    @DeleteMapping("/deleteByCardNumber/{cardNumber}")
    private ResponseEntity<Object> deleteById(@PathVariable String cardNumber){
        return creditCardService.deleteCreditCardByCardNumber(cardNumber);
    }
    @GetMapping("/getTransaction/{cardNumber}")
    public List<CardTransaction> findTransaction(@PathVariable String cardNumber){

        return creditCardService.findByTransactionCardTypeAndCardNo(cardNumber);
    }

}

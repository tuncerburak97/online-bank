package org.kodluyoruz.mybank.controller;

import org.kodluyoruz.mybank.request.card.CreateDebitCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.service.card.DebitCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/customer/account/deposit/debitCard")
public class DebitCardController {

    @Autowired
    private DebitCardService debitCardService;


    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody CreateDebitCardRequest request){
        return debitCardService.createDebitCard(request);
    }

    @PostMapping("/addBalance")
    public ResponseEntity<Object> addBalance(@RequestBody CardTransactionRequest request) throws IOException {
        return debitCardService.addBalance(request);
    }

    @PostMapping("/withDrawBalance")
    public ResponseEntity<Object> withdrawBalance(@RequestBody CardTransactionRequest request) throws IOException {
        return debitCardService.withdrawBalance(request);
    }

    @DeleteMapping("/deleteByCardNumber/{cardNumber}")
    private ResponseEntity<Object> deleteById(@PathVariable String cardNumber) throws IOException {
        return debitCardService.deleteDebitByCardNumber(cardNumber);
    }

}

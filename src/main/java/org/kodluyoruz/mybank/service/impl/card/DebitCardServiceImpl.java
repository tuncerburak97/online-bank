package org.kodluyoruz.mybank.service.impl.card;


import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.operation.OperationType;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.SystemOperationsRepository;
import org.kodluyoruz.mybank.repository.card.DebitCardRepository;
import org.kodluyoruz.mybank.request.card.CreateDebitCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.service.card.CardService;
import org.kodluyoruz.mybank.service.card.DebitCardService;
import org.kodluyoruz.mybank.service.transaction.CardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class DebitCardServiceImpl implements DebitCardService {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardTransactionService cardTransactionService;

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;

    @Override
    public ResponseEntity<Object> createDebitCard(CreateDebitCardRequest request) {

        DebitCard debitCard = new DebitCard();
        return cardService.createDebitCard(debitCard,request);
    }
    @Override
    public ResponseEntity<Object> withdrawBalance(CardTransactionRequest request) throws IOException {
        return cardTransactionService.debitCardTransaction(request,"WithdrawBalance");
    }

    @Override
    public ResponseEntity<Object> addBalance(CardTransactionRequest request) throws IOException {
        return cardTransactionService.debitCardTransaction(request,"AddBalance");
    }

    @Override
    public ResponseEntity<Object> deleteDebitByCardNumber(String cardNumber) throws IOException {

        DebitCard debitCard = debitCardRepository.findByCardNumber(cardNumber);

        if(debitCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found");
        }

        debitCardRepository.delete(debitCard);


        SystemOperations systemOperations = new SystemOperations(0L, OperationType.DELETE_DEBIT_CARD.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperationsRepository.save(systemOperations);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card deleted");
    }





}

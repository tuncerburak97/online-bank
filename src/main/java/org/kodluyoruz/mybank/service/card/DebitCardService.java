package org.kodluyoruz.mybank.service.card;

import org.kodluyoruz.mybank.request.card.CreateDebitCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DebitCardService {

    ResponseEntity<Object> createDebitCard(CreateDebitCardRequest request);

    ResponseEntity<Object> withdrawBalance(CardTransactionRequest request) throws IOException;

    ResponseEntity<Object> addBalance(CardTransactionRequest request) throws IOException;

    ResponseEntity<Object> deleteDebitByCardNumber(String cardNumber) throws IOException;
}

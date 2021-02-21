package org.kodluyoruz.mybank.service.card;

import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.card.CreateDebitCardRequest;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface CardService {

    public ResponseEntity<Object> createCreditCard(CreditCard creditCard, CreateCreditCardRequest request);
    public ResponseEntity<Object> createDebitCard(DebitCard debitCard, CreateDebitCardRequest request);

}

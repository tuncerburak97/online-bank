package org.kodluyoruz.mybank.service.transaction;


import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.card.Card;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface CardTransactionService {

    public ResponseEntity<Object> withdrawCreditCard(CardTransactionRequest transaction);

    public ResponseEntity<Object> debitCardTransaction(CardTransactionRequest transaction,String transactionType) throws IOException;

    public <T extends Account> ResponseEntity<Object> debtOnCard(T a, DebtOnCardRequest request) throws IOException;

    public <T extends Card> List<CardTransaction> findByDateBetweenAndCardNo(T a,TransactionDate date, String cardNo) throws Exception;


}
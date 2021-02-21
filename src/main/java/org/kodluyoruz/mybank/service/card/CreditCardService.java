package org.kodluyoruz.mybank.service.card;

import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface CreditCardService {

    public ResponseEntity<Object> createCredit(CreateCreditCardRequest request);

    ResponseEntity<Object> withdrawBalance(CardTransactionRequest request) throws IOException;

    ResponseEntity<Object> debtInquiry(String cardNumber);

    ResponseEntity<Object> debtOnDepositAccount(DebtOnAccountRequest request) throws IOException;

    ResponseEntity<Object> debtOnSavingAccount(DebtOnAccountRequest request) throws IOException;

    ResponseEntity<Object> debtOnDebitCard(DebtOnCardRequest request) throws IOException;

    ResponseEntity<Object> deleteCreditCardByCardNumber(String cardNumber);

    List<CardTransaction> findByTransactionCardTypeAndCardNo(String cardNo);

    List<CardTransaction> findTransactionDateBetweenAndCardNo(TransactionDate date,String cardNo) throws Exception;
}

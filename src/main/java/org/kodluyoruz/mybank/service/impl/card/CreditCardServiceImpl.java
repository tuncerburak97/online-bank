package org.kodluyoruz.mybank.service.impl.card;


import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.entity.credit.CreditPoint;
import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.operation.OperationType;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.SystemOperationsRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.credit.CreditPointRepository;
import org.kodluyoruz.mybank.repository.transaction.CardTransactionRepository;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.kodluyoruz.mybank.service.card.CardService;
import org.kodluyoruz.mybank.service.card.CreditCardService;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.kodluyoruz.mybank.service.transaction.CardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class CreditCardServiceImpl implements CreditCardService {


    @Autowired
    private CardService cardService;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private CardTransactionService cardTransactionService;

    @Autowired
    private CardTransactionRepository cardTransactionRepository;

    @Autowired
    private AccountTransactionService accountTransactionService;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditPointRepository creditPointRepository;

    @Override
    public ResponseEntity<Object> createCredit(CreateCreditCardRequest request) {

        CreditCard creditCard = new CreditCard();
        return cardService.createCreditCard(creditCard,request);
    }

    @Override
    public ResponseEntity<Object> withdrawBalance(CardTransactionRequest request) throws IOException {
        return cardTransactionService.withdrawCreditCard(request);
    }

    @Override
    public ResponseEntity<Object> debtInquiry(String cardNumber) {

        CreditCard creditCard =creditCardRepository.findByCardNumber(cardNumber);
        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
        }

        double debt = creditCard.getCardLimit()-creditCard.getCurrentLimit();
        return ResponseEntity.status(HttpStatus.OK).body("Credit card debt is:"+debt);
    }

    @Override
    public ResponseEntity<Object> debtOnDepositAccount(DebtOnAccountRequest request) throws IOException {

        DepositAccount depositAccount =new DepositAccount();
        return accountTransactionService.debtOnAccount(depositAccount,request);
    }

    @Override
    public ResponseEntity<Object> debtOnSavingAccount(DebtOnAccountRequest request) throws IOException {
        SavingAccount savingAccount =new SavingAccount();
        return accountTransactionService.debtOnAccount(savingAccount,request);
    }

    @Override
    public ResponseEntity<Object> debtOnDebitCard(DebtOnCardRequest request) throws IOException {

        DepositAccount depositAccount = new DepositAccount();
        return cardTransactionService.debtOnCard(depositAccount,request);

    }

    @Override
    public ResponseEntity<Object> deleteCreditCardByCardNumber(String cardNumber) {

        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber);

        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
        }

        double debt = creditCard.getCardLimit()-creditCard.getCurrentLimit();

        if(debt==0){

            creditCardRepository.delete(creditCard);
            return ResponseEntity.status(HttpStatus.OK).body("Credit card deleted");
        }


        SystemOperations systemOperations = new SystemOperations(0L, OperationType.DELETE_CREDIT_CARD.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(creditCard.getCustomer());
        systemOperationsRepository.save(systemOperations);

        return ResponseEntity.status(HttpStatus.OK).body("Credit card have a debt.Delete operation is not allowed.");
    }

    @Override
    public List<CardTransaction> findByTransactionCardTypeAndCardNo(String cardNo) {

        String type="CreditCard";

        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNo);

        if(creditCard==null){

            return null;
        }
        return cardTransactionRepository.findByCardNo(cardNo);

    }

    @Override
    public List<CardTransaction> findTransactionDateBetweenAndCardNo(TransactionDate date, String cardNo) throws Exception {

        CreditCard creditCard = new CreditCard();
        return cardTransactionService.findByDateBetweenAndCardNo(creditCard,date,cardNo);
    }

    @Override
    public ResponseEntity<Object> getMaxCreditLimit(long customerId){

        Customer customer = customerRepository.findById(customerId);
        if(customer==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");

        CreditPoint creditPoint = creditPointRepository.findByRanking(customer.getCreditPoint());
        return ResponseEntity.status(HttpStatus.OK).body("The customer's credit card limit can be up to "+creditPoint.getMaxCreditLimit() +" TL");


    }


}

package org.kodluyoruz.mybank.service.impl.card;


import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.card.Card;
import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.operation.OperationType;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.generator.CardNumberGenerator;
import org.kodluyoruz.mybank.generator.SecurityCodeGenerator;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.SystemOperationsRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.card.DebitCardRepository;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.card.CreateDebitCardRequest;
import org.kodluyoruz.mybank.service.card.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;


    CardNumberGenerator cardNumberGenerator=new CardNumberGenerator();

    SecurityCodeGenerator securityCodeGenerator = new SecurityCodeGenerator();

    public CardServiceImpl(CustomerRepository customerRepository, CreditCardRepository creditCardRepository, DepositAccountRepository depositAccountRepository, DebitCardRepository debitCardRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.debitCardRepository = debitCardRepository;
    }

    public CardServiceImpl() {

    }

    @Override
    public ResponseEntity<Object> createCreditCard(CreditCard creditCard, CreateCreditCardRequest request){

        Customer customer =customerRepository.findById(request.getCustomer_id());

        if(customer==null){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        boolean isUnique=true;
        while (isUnique){

            String cardNumber = cardNumberGenerator.generateCardNumber();

            if(creditCardRepository.findByCardNumber(cardNumber)!=null){
                continue;
            }

            creditCard.setCardNumber(cardNumber);
            isUnique=false;
        }


        creditCard.setCardLimit(request.getCreditLimit());
        creditCard.setCardType(creditCard.getClass().getSimpleName());
        creditCard.setCurrentLimit(request.getCreditLimit());
        creditCard.setExpiredDate(this.expiredDate());
        creditCard.setUsable(true);
        creditCard.setCustomer(customer);
        creditCard.setSecurityNumber(securityCodeGenerator.generateSecurityCode());


//        customer.getCreditCards().add(creditCard);

        creditCardRepository.save(creditCard);



        SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_CREDIT_CARD.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);
        systemOperationsRepository.save(systemOperations);

        return ResponseEntity.status(HttpStatus.OK).body("Credit card created");
    }

    @Override
    public ResponseEntity<Object> createDebitCard(DebitCard debitCard, CreateDebitCardRequest request) {

        DepositAccount depositAccount = depositAccountRepository.findByAccountNumber(request.getAccountNumber());

        if(depositAccount==null){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");

        }
        boolean isUnique=true;

        while (isUnique){

            String cardNumber = cardNumberGenerator.generateCardNumber();
            if(debitCardRepository.findByCardNumber(cardNumber)!=null){
                continue;
            }
            debitCard.setCardNumber(cardNumber);
            isUnique=false;
        }


        debitCard.setCardType(debitCard.getClass().getSimpleName());
        debitCard.setExpiredDate(this.expiredDate());
        debitCard.setDepositAccount(depositAccount);
        debitCard.setUsable(true);
        debitCard.setSecurityNumber(securityCodeGenerator.generateSecurityCode());


        // depositAccount.getDebitCards().add(debitCard);

        debitCardRepository.save(debitCard);
        //depositAccountRepository.save(depositAccount);


        SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_DEBIT_CARD.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(depositAccount.getCustomer());
        systemOperationsRepository.save(systemOperations);


        return ResponseEntity.status(HttpStatus.OK).body("Debit card created");
    }




    public String expiredDate(){

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR)+5;
        String expiredDate= month +"/"+ year;

        return expiredDate;

    }

    //@Scheduled(cron = "0 0 1 * *")
    public void isUsable(){


        Calendar calendar = Calendar.getInstance();
        int month =calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR)+1;

        String montString = String.valueOf(month);
        String yearString = String.valueOf(year);

        List<Card> creditCards=creditCardRepository.findAll();

        for (Card creditCard : creditCards) {

            if (creditCard.getExpiredDate().equals(montString+"/"+yearString)) {

                creditCard.setUsable(false);
                creditCardRepository.save(creditCard);
            }

        }
    }



}


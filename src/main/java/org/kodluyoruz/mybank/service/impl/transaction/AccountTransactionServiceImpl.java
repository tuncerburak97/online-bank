package org.kodluyoruz.mybank.service.impl.transaction;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.DateFormat;
import java.util.*;
import org.kodluyoruz.mybank.checker.FormatChecker;
import org.kodluyoruz.mybank.domain.CurrencyOperation;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.card.CreditCard;
//import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
//import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.AccountRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.account.SavingAccountRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.transaction.AccountTransactionRepository;
import org.kodluyoruz.mybank.repository.transaction.CardTransactionRepository;
import org.kodluyoruz.mybank.request.transaction.*;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class AccountTransactionServiceImpl implements AccountTransactionService {

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private CardTransactionRepository cardTransactionRepository;

    @Autowired
    private SaveTransactionServiceImpl saveTransactionServiceImpl;




    @PersistenceContext
    private EntityManager entityManager;


    private final FormatChecker formatChecker = new FormatChecker();

    @Transactional
    @Override
    public <T extends Account> ResponseEntity<Object> balanceOnAccount(T a, String transactionType, AccountTransactionRequest request) throws IOException {


        boolean accountNumberCheck=formatChecker.numberFormatChecker(request.getAccountNumber());

        if(!accountNumberCheck)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account type");

        Account account=null;
        AccountRepository repository=null;

        if(a instanceof DepositAccount){

            account=depositAccountRepository.findByAccountNumber(request.getAccountNumber());
            repository=depositAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        }

        if(a instanceof SavingAccount){

            account=savingAccountRepository.findByAccountNumber(request.getAccountNumber());
            repository=savingAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }

        }



        ObjectMapper objectMapper =new ObjectMapper();
        URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
        CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

        Customer customer =customerRepository.findById(account.getCustomer().getId());
        double newReceiverCustomerAsset = request.getAmount()/currencyClass.getRates().get(account.getCurrencyType().toString());

        if(transactionType.equals("AddBalance")){


            entityManager.lock(account,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

            try {

                Thread.sleep(5000);
                account.setBalance(account.getBalance()+request.getAmount());
                customer.setAsset(customer.getAsset()+newReceiverCustomerAsset);

                repository.save(account);
                customerRepository.save(customer);

            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
            }

            saveTransactionServiceImpl.saveBalanceOnAccount("AddBalance",account,request);

            return ResponseEntity.status(HttpStatus.OK).body("Balance is added");

        }

        if(transactionType.equals("WithdrawBalance")){

            if(account.getBalance()>=request.getAmount()){


                entityManager.lock(account,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

                try {

                    Thread.sleep(5000);
                    account.setBalance(account.getBalance()-request.getAmount());
                    customer.setAsset(customer.getAsset()-newReceiverCustomerAsset);

                    repository.save(account);
                    customerRepository.save(customer);

                }catch (Exception e){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
                }

                saveTransactionServiceImpl.saveBalanceOnAccount("WithdrawBalance",account,request);

                return ResponseEntity.status(HttpStatus.OK).body("Balance is withdraw");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("You dont have enough money for this transaction");

    }



    @Transactional
    @Override
    public <T extends Account> ResponseEntity<Object> debtOnAccount(T a, DebtOnAccountRequest request) throws IOException {


        boolean accountNumberCheck=formatChecker.numberFormatChecker(request.getAccountNumber());
        boolean cardNumberCheck=formatChecker.numberFormatChecker(request.getCardNumber());

        if(!accountNumberCheck)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account number type");

        if(!cardNumberCheck)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card number type");


        Account account = null;
        AccountRepository accountRepository =null;

        CreditCard creditCard = creditCardRepository.findByCardNumber(request.getCardNumber());

        if(creditCard==null){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
        }

        if(a instanceof DepositAccount){

            account = depositAccountRepository.findByAccountNumber(request.getAccountNumber());
            accountRepository=depositAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deposit account not found");
            }
        }
        if(a instanceof SavingAccount){

            account = savingAccountRepository.findByAccountNumber(request.getAccountNumber());
            accountRepository=savingAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Saving account not found");
            }
        }

        if(account.getCustomer()!=creditCard.getCustomer()){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer can pay only own credit card debt");
        }

        ObjectMapper objectMapper =new ObjectMapper();
        URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
        CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

        Customer customer = account.getCustomer();

        double currencyAsset =account.getBalance()/currencyClass.getRates()
                .get(account.getCurrencyType().toString());

        if(currencyAsset<request.getDebt()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient balance");
        }


        entityManager.lock(creditCard,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        entityManager.lock(account,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

        try {

            Thread.sleep(5000);

            creditCard.setCurrentLimit(creditCard.getCurrentLimit()+request.getDebt());
            account.setBalance(account.getBalance()- (request.getDebt()*currencyClass.getRates().get(account.getCurrencyType().toString())));
            customer.setAsset(customer.getAsset()-request.getDebt());

            accountRepository.save(account);
            creditCardRepository.save(creditCard);
            customerRepository.save(customer);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
        }

        CardTransactionRequest cardTransactionRequest = new CardTransactionRequest();

        cardTransactionRequest.setCardNumber(creditCard.getCardNumber());
        cardTransactionRequest.setAmount(request.getDebt());

        saveTransactionServiceImpl.saveCreditCardTransaction(cardTransactionRequest,customer,"DebtPayment");
        saveTransactionServiceImpl.saveDebtOnAccount(account,request,currencyClass);

        return ResponseEntity.status(HttpStatus.OK).body("Credit card debt amounting to "+request.getDebt()+" TL has been paid");
    }


    @Override
    public <T extends Account> List<AccountTransaction> findAccountTransactionByDateAndAccountNumber(T a,TransactionDate date,String accountNumber) throws Exception {


        Account account = null;

        if(a instanceof DepositAccount){

            account = depositAccountRepository.findByAccountNumber(accountNumber);
        }

        if(a instanceof SavingAccount){

            account = savingAccountRepository.findByAccountNumber(accountNumber);
        }

        if(account==null){
            throw new Exception("Account not found");
        }


        String startYear=date.getStartYear() ;
        String startMonth=date.getStartMonth();
        String startDay=date.getStartDay();

        String endYear=date.getEndYear();
        String endMonth =date.getEndMonth();
        String endDay =date.getEndDay();


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = dateFormat.parse(startYear+"-"+startMonth+"-"+startDay);
        Date endDate = dateFormat.parse(endYear+"-"+endMonth+"-"+endDay);

        return accountTransactionRepository.findByDateBetweenAndAccountNumber(startDate,endDate,accountNumber);



    }



}
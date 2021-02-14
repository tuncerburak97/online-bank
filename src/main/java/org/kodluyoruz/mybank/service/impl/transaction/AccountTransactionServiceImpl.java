package org.kodluyoruz.mybank.service.impl.transaction;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.kodluyoruz.mybank.domain.CurrencyOperation;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.card.CreditCard;
//import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
//import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.entity.transaction.AccountTransaction;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.AccountRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.account.SavingAccountRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.transaction.AccountTransactionRepository;
import org.kodluyoruz.mybank.repository.transaction.CardTransactionRepository;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

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



    @Override
    public <T extends Account> ResponseEntity<Object> balanceOnAccount(T a, String transactionType, AccountTransactionRequest request) throws IOException {

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


            account.setBalance(account.getBalance()+request.getAmount());
            customer.setAsset(customer.getAsset()+newReceiverCustomerAsset);

            repository.save(account);
            customerRepository.save(customer);

           AccountTransaction accountTransaction = new AccountTransaction(request.getAccountNumber(),
                   account.getCurrencyType().toString(),
                   request.getAmount(),"AddBalance",
                   new Timestamp(System.currentTimeMillis()));

           accountTransaction.setCustomer(account.getCustomer());

            accountTransactionRepository.save(accountTransaction);

            return ResponseEntity.status(HttpStatus.OK).body("Balance is added");

        }

        if(transactionType.equals("WithdrawBalance")){

            if(account.getBalance()>=request.getAmount()){

                account.setBalance(account.getBalance()-request.getAmount());
                customer.setAsset(customer.getAsset()-newReceiverCustomerAsset);

                repository.save(account);
                customerRepository.save(customer);

                AccountTransaction accountTransaction = new AccountTransaction(request.getAccountNumber(),
                        account.getCurrencyType().toString(),
                        request.getAmount(),"WithdrawBalance",
                        new Timestamp(System.currentTimeMillis()));

                accountTransactionRepository.save(accountTransaction);

                return ResponseEntity.status(HttpStatus.OK).body("Balance is withdrawed");
            }
        }




        return ResponseEntity.status(HttpStatus.OK).body("You dont have enough money for this transaction");

    }

    @Override
    public <T extends Account> ResponseEntity<Object> debtOnAccount(T a, DebtOnAccountRequest request) throws IOException {

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

        creditCard.setCurrentLimit(creditCard.getCurrentLimit()+request.getDebt());

        account.setBalance(account.getBalance()- (request.getDebt()*currencyClass.getRates().get(account.getCurrencyType().toString())));

        customer.setAsset(customer.getAsset()-request.getDebt());

    CardTransaction creditCardTransaction = new CardTransaction(request.getDebt(),"DebtPayment",new Timestamp(System.currentTimeMillis()),request.getCardNumber(),"CreditCard","TRY");
        cardTransactionRepository.save(creditCardTransaction);

        AccountTransaction accountTransaction = new AccountTransaction(request.getAccountNumber(),
                account.getCurrencyType().toString(),
                request.getDebt(),"DebtPayment",
                new Timestamp(System.currentTimeMillis()));
        accountTransaction.setCustomer(account.getCustomer());
       accountTransactionRepository.save(accountTransaction);


        accountRepository.save(account);
        creditCardRepository.save(creditCard);
        customerRepository.save(customer);



        return ResponseEntity.status(HttpStatus.OK).body("Credit card debt amounting to "+request.getDebt()+" TL has been paid");
    }
}

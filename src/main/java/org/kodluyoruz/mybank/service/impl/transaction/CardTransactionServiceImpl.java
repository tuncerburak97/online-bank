package org.kodluyoruz.mybank.service.impl.transaction;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.kodluyoruz.mybank.domain.CurrencyOperation;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.AccountRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.card.DebitCardRepository;
import org.kodluyoruz.mybank.repository.transaction.CardTransactionRepository;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.service.transaction.CardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

@Service
public class CardTransactionServiceImpl implements CardTransactionService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private CardTransactionRepository cardTransactionRepository;

    public CardTransactionServiceImpl(CustomerRepository customerRepository, CreditCardRepository creditCardRepository, DepositAccountRepository depositAccountRepository, DebitCardRepository debitCardRepository, CardTransactionRepository cardTransactionRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.debitCardRepository = debitCardRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public CardTransactionServiceImpl() {

    }

    @Override
    public ResponseEntity<Object> withdrawCreditCard(CardTransactionRequest transaction){

        CreditCard creditCard = creditCardRepository.findByCardNumber(transaction.getCardNumber());

        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }

        if(!creditCard.isUsable()){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Credit card expired date.Card is not usable");
        }

        if(creditCard.getCurrentLimit()>=transaction.getAmount()){

            creditCard.setCurrentLimit(creditCard.getCurrentLimit()-transaction.getAmount());
            creditCardRepository.save(creditCard);

          CardTransaction cardTransaction = new CardTransaction(transaction.getAmount(),"Withdraw",new Timestamp(System.currentTimeMillis()),transaction.getCardNumber(),"CreditCard","TRY");
          cardTransaction.setCustomer(creditCard.getCustomer());
           cardTransactionRepository.save(cardTransaction);

            return ResponseEntity.status(HttpStatus.OK).body("Transaction completed");
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Insufficient balance ");
    }

    @Override
    public ResponseEntity<Object> debitCardTransaction(CardTransactionRequest transaction,String transactionType) throws IOException {

        DebitCard debitCard =debitCardRepository.findByCardNumber(transaction.getCardNumber());

        if(debitCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }

        ObjectMapper objectMapper =new ObjectMapper();
        URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
        CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

        DepositAccount depositAccount = debitCard.getDepositAccount();

        double customerAsset = transaction.getAmount()/currencyClass.getRates()
                .get(depositAccount.getCurrencyType().toString());


        if(transactionType.equals("WithdrawBalance")){

            if(depositAccount.getBalance()>=transaction.getAmount()){

                Customer customer =depositAccount.getCustomer();
                depositAccount.setBalance(depositAccount.getBalance()-transaction.getAmount());
                customer.setAsset(customer.getAsset()-customerAsset);

                customerRepository.save(customer);
                depositAccountRepository.save(depositAccount);


           CardTransaction cardTransaction = new CardTransaction(transaction.getAmount(),transactionType,new Timestamp(System.currentTimeMillis()),transaction.getCardNumber(),"DebitCard",debitCard.getDepositAccount().getCurrencyType().toString());
           cardTransactionRepository.save(cardTransaction);


                return ResponseEntity.status(HttpStatus.OK).body("Withdraw balance transaction completed");

            }

            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Insufficient balance ");
        }

        if(transactionType.equals("AddBalance")){

            Customer customer =depositAccount.getCustomer();
            depositAccount.setBalance(depositAccount.getBalance()+transaction.getAmount());
            customer.setAsset(customer.getAsset()+customerAsset);

            customerRepository.save(customer);
            depositAccountRepository.save(depositAccount);


            CardTransaction cardTransaction = new CardTransaction(transaction.getAmount(),transactionType,new Timestamp(System.currentTimeMillis()),transaction.getCardNumber(),"DebitCard",debitCard.getDepositAccount().getCurrencyType().toString());
            cardTransaction.setCustomer(debitCard.getDepositAccount().getCustomer());
            cardTransactionRepository.save(cardTransaction);


            return ResponseEntity.status(HttpStatus.OK).body("Add balance transaction completed");
        }

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout");
    }

    @Override
    public <T extends Account> ResponseEntity<Object> debtOnCard(T a, DebtOnCardRequest request) throws IOException {

        Account account = null;
        AccountRepository accountRepository=null;

        CreditCard creditCard = creditCardRepository.findByCardNumber(request.getCreditCardNumber());
        DebitCard debitCard = debitCardRepository.findByCardNumber(request.getCardNumber());

        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
        }
        if(debitCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found");
        }
        if(a instanceof DepositAccount){

            account=debitCard.getDepositAccount();
            accountRepository=depositAccountRepository;

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

        accountRepository.save(account);
        creditCardRepository.save(creditCard);
        customerRepository.save(customer);


      CardTransaction debitCardTransaction = new CardTransaction(request.getDebt(),"DebtPayment",new Timestamp(System.currentTimeMillis()),debitCard.getCardNumber(),"DebitCard",debitCard.getDepositAccount().getCurrencyType().toString());
      debitCardTransaction.setCustomer(account.getCustomer());
      cardTransactionRepository.save(debitCardTransaction);

      CardTransaction creditCardTransaction = new CardTransaction(request.getDebt()/currencyClass.getRates().get(account.getCurrencyType().toString()),"DebtPayment",new Timestamp(System.currentTimeMillis()),debitCard.getCardNumber(),"CreditCard","TRY");
      creditCardTransaction.setCustomer(account.getCustomer());
      cardTransactionRepository.save(creditCardTransaction);


        return ResponseEntity.status(HttpStatus.OK).body("Credit card debt amounting to "+request.getDebt()+" TL has been paid");

    }




}


package org.kodluyoruz.mybank.service.impl.transaction;


import org.kodluyoruz.mybank.checker.FormatChecker;
import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.card.Card;
import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.transaction.CardTransaction;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.AccountRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.account.SavingAccountRepository;
import org.kodluyoruz.mybank.repository.card.CreditCardRepository;
import org.kodluyoruz.mybank.repository.card.DebitCardRepository;
import org.kodluyoruz.mybank.repository.transaction.AccountTransactionRepository;
import org.kodluyoruz.mybank.repository.transaction.CardTransactionRepository;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnCardRequest;
import org.kodluyoruz.mybank.request.transaction.TransactionDate;
import org.kodluyoruz.mybank.service.currency.CurrencyService;
import org.kodluyoruz.mybank.service.impl.interest.DailyInterestServiceImpl;
import org.kodluyoruz.mybank.service.impl.transaction.helper.AccountTypeHelper;
import org.kodluyoruz.mybank.service.transaction.CardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private SaveTransactionServiceImpl saveTransactionServiceImpl;

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private DailyInterestServiceImpl dailyInterestService;

    @Autowired
    private AccountTypeHelper accountTypeHelper;

    @Autowired
    private CurrencyService currencyService;

    @PersistenceContext
    private EntityManager entityManager;

    private final FormatChecker formatChecker = new FormatChecker();

    public CardTransactionServiceImpl(CustomerRepository customerRepository, CreditCardRepository creditCardRepository, DepositAccountRepository depositAccountRepository, DebitCardRepository debitCardRepository, CardTransactionRepository cardTransactionRepository) {
        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.debitCardRepository = debitCardRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public CardTransactionServiceImpl() {

    }

    @Transactional
    @Override
    public ResponseEntity<Object> withdrawCreditCard(CardTransactionRequest transaction){

        boolean cardNumberChecker = formatChecker.numberFormatChecker(transaction.getCardNumber());

        if(!cardNumberChecker)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card number type");


        CreditCard creditCard = creditCardRepository.findByCardNumber(transaction.getCardNumber());

        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }

        if(!creditCard.isUsable()){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Credit card expired date.Card is not usable");
        }

        if(creditCard.getCurrentLimit()>=transaction.getAmount()){

            entityManager.lock(creditCard, LockModeType.PESSIMISTIC_FORCE_INCREMENT);

            try {

                Thread.sleep(5000);
                creditCard.setCurrentLimit(creditCard.getCurrentLimit()-transaction.getAmount());
                creditCardRepository.save(creditCard);

            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
            }

            saveTransactionServiceImpl.saveCreditCardTransaction(transaction,creditCard.getCustomer(),"WithdrawBalance");


            return ResponseEntity.status(HttpStatus.OK).body("Transaction completed");
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Insufficient balance ");
    }

    @Transactional
    @Override
    public ResponseEntity<Object> debitCardTransaction(CardTransactionRequest transaction,String transactionType) throws IOException {

        boolean cardNumberChecker = formatChecker.numberFormatChecker(transaction.getCardNumber());

        if(!cardNumberChecker)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card number type");


        DebitCard debitCard =debitCardRepository.findByCardNumber(transaction.getCardNumber());

        if(debitCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }



        DepositAccount depositAccount = debitCard.getDepositAccount();

        AccountTransactionRequest request = new AccountTransactionRequest();
        request.setAccountNumber(depositAccount.getAccountNumber());
        request.setAmount(transaction.getAmount());

        if(transactionType.equals("WithdrawBalance")){



            if(depositAccount.getBalance()>=transaction.getAmount()){

                Customer customer =depositAccount.getCustomer();

                entityManager.lock(depositAccount,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            //    entityManager.lock(debitCard,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            //    entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

                try {

                    Thread.sleep(5000);
                    depositAccount.setBalance(depositAccount.getBalance()-transaction.getAmount());
                    currencyService.setCustomerAsset(request.getAmount(),depositAccount,"WithdrawBalance");

                    depositAccountRepository.save(depositAccount);

                }catch (Exception e){
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
                }

                saveTransactionServiceImpl.saveDebitCardTransaction(transaction,depositAccount,transactionType,currencyService.setAccountBalance(request.getAmount(),depositAccount));
                saveTransactionServiceImpl.saveBalanceOnAccount("WithdrawBalanceFromDebit",depositAccount,request);


                return ResponseEntity.status(HttpStatus.OK).body("Withdraw balance transaction completed");

            }

            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Insufficient balance ");
        }

        if(transactionType.equals("AddBalance")){

            Customer customer =depositAccount.getCustomer();

            entityManager.lock(depositAccount,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
         //   entityManager.lock(debitCard,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        //    entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

            try {

                Thread.sleep(5000);

                depositAccount.setBalance(depositAccount.getBalance()+transaction.getAmount());
                currencyService.setCustomerAsset(request.getAmount(),depositAccount,"AddBalance");

                customerRepository.save(customer);
                depositAccountRepository.save(depositAccount);


            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
            }


            saveTransactionServiceImpl.saveDebitCardTransaction(transaction,depositAccount,transactionType,currencyService.setAccountBalance(request.getAmount(),depositAccount));
            saveTransactionServiceImpl.saveBalanceOnAccount("AddBalanceFromDebit",depositAccount,request);


            return ResponseEntity.status(HttpStatus.OK).body("Add balance transaction completed");
        }

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout");
    }

    @Transactional
    @Override
    public <T extends Account> ResponseEntity<Object> debtOnCard(T a, DebtOnCardRequest request) throws IOException {

        boolean creditCardNumberChecker = formatChecker.numberFormatChecker(request.getCreditCardNumber());
        boolean debitCardNumberChecker = formatChecker.numberFormatChecker(request.getCardNumber());

        if(!creditCardNumberChecker)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credit card number type");

        if(!debitCardNumberChecker)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid debit card number type");


        CreditCard creditCard = creditCardRepository.findByCardNumber(request.getCreditCardNumber());
        DebitCard debitCard = debitCardRepository.findByCardNumber(request.getCardNumber());

        if(creditCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
        }
        if(debitCard==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found");
        }


        Account account = accountTypeHelper.setAccountType(a,debitCard.getDepositAccount().getAccountNumber());
        AccountRepository accountRepository=accountTypeHelper.setAccountRepo(a);

        Customer customer = account.getCustomer();

        double currencyAsset =currencyService.setCurrencyAsset(request.getDebt(),account);

        if(currencyAsset<request.getDebt()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient balance");
        }

        entityManager.lock(debitCard.getDepositAccount(),LockModeType.PESSIMISTIC_FORCE_INCREMENT);
      //  entityManager.lock(debitCard,LockModeType.PESSIMISTIC_FORCE_INCREMENT);
      //  entityManager.lock(customer,LockModeType.PESSIMISTIC_FORCE_INCREMENT);

        try {

            Thread.sleep(5000);

            creditCard.setCurrentLimit(creditCard.getCurrentLimit()+request.getDebt());
            account.setBalance(account.getBalance()-currencyService.setAccountBalance(request.getDebt(),account));

            customer.setAsset(customer.getAsset()-request.getDebt());
            customerRepository.save(customer);
            accountRepository.save(account);
            creditCardRepository.save(creditCard);



        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account is busy");
        }

        double accountAmount=currencyService.setAccountBalance(request.getDebt(),account);

        CardTransactionRequest debitCardTransaction = new CardTransactionRequest();
        debitCardTransaction.setCardNumber(request.getCardNumber());
        debitCardTransaction.setAmount(request.getDebt());

        saveTransactionServiceImpl.saveDebitCardTransaction(debitCardTransaction,debitCard.getDepositAccount(),"DebtPayment",accountAmount);


        AccountTransactionRequest accountTransactionRequest = new AccountTransactionRequest();
        accountTransactionRequest.setAccountNumber(debitCard.getDepositAccount().getAccountNumber());
        accountTransactionRequest.setAmount(accountAmount);

        saveTransactionServiceImpl.saveBalanceOnAccount("DebtPaymentFromDebit",account,accountTransactionRequest);

        CardTransactionRequest creditCardTransaction = new CardTransactionRequest();
        creditCardTransaction.setCardNumber(request.getCreditCardNumber());
        creditCardTransaction.setAmount(request.getDebt());
        saveTransactionServiceImpl.saveCreditCardTransaction(creditCardTransaction,customer,"DebtPayment");

        return ResponseEntity.status(HttpStatus.OK).body("Credit card debt amounting to "+request.getDebt()+" TL has been paid");

    }
    @Override
    public <T extends Card> List<CardTransaction> findByDateBetweenAndCardNo(T a, TransactionDate date, String cardNo) throws Exception {

        Card card = null;

        if(a instanceof CreditCard)
            card = creditCardRepository.findByCardNumber(cardNo);

        if(a instanceof DebitCard)
            card =debitCardRepository.findByCardNumber(cardNo);

        if(a==null)
            throw new Exception("Card not found");

        String startYear=date.getStartYear() ;
        String startMonth=date.getStartMonth();
        String startDay=date.getStartDay();

        String endYear=date.getEndYear();
        String endMonth =date.getEndMonth();
        String endDay =date.getEndDay();


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = dateFormat.parse(startYear+"-"+startMonth+"-"+startDay);
        Date endDate = dateFormat.parse(endYear+"-"+endMonth+"-"+endDay);

        return cardTransactionRepository.findByDateBetweenAndCardNo(startDate,endDate,cardNo);

    }


}


package org.kodluyoruz.mybank.service.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kodluyoruz.mybank.domain.CurrencyOperation;
import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.CurrencyType;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.operation.OperationType;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.kodluyoruz.mybank.generator.AccountNumberGenerator;
import org.kodluyoruz.mybank.generator.IbanNumberGenerator;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.SystemOperationsRepository;
import org.kodluyoruz.mybank.repository.account.AccountRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.account.SavingAccountRepository;
import org.kodluyoruz.mybank.request.account.CreateAccountRequest;
import org.kodluyoruz.mybank.request.account.CreateSavingAccountRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.service.account.AccountService;
import org.kodluyoruz.mybank.service.transaction.AccountTransactionService;
import org.kodluyoruz.mybank.service.transaction.SaveTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;

    @Autowired
    private AccountTransactionService accountTransactionService;

    @Autowired
    private SaveTransactionService saveTransactionService;



    public AccountServiceImpl(DepositAccountRepository depositAccountRepository, CustomerRepository customerRepository, SavingAccountRepository savingAccountRepository) {
        this.depositAccountRepository = depositAccountRepository;
        this.customerRepository = customerRepository;
        this.savingAccountRepository = savingAccountRepository;
    }

    public AccountServiceImpl() {

    }


    @Override
    public <T extends Account> ResponseEntity<Object> createAccount(T a, CreateAccountRequest request) throws IOException {

        Customer customer = customerRepository.findById(request.getCustomerId());

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Account account = null;
        AccountRepository accountRepository = null;

        if(!(request.getCurrencyType().equals("USD")||request.getCurrencyType().equals("TRY")||request.getCurrencyType().equals("EUR"))){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account type must be USD,EUR or TRY");
        }

        if (a instanceof DepositAccount) {

            account = new DepositAccount();
            accountRepository = depositAccountRepository;
        }
        if (a instanceof SavingAccount) {

            account = new SavingAccount();
            accountRepository = savingAccountRepository;
        }

        boolean isUnique=true;

        while (isUnique){

            AccountNumberGenerator accountNumberGenerator=new AccountNumberGenerator();
            String accountNumber=accountNumberGenerator.generateAccountNumber();

            if(accountRepository.findByAccountNumber(accountNumber)!=null){
                continue;
            }

            IbanNumberGenerator ibanNumberGenerator = new IbanNumberGenerator();
            String ibanNo=ibanNumberGenerator.ibanGenerate(accountNumber);

            if(accountRepository.findByIbanNo(ibanNo)!=null){
                continue;
            }


            account.setAccountNumber(accountNumber);
            account.setIbanNo(ibanNo);
            account.setCustomer(customer);
            account.setCurrencyType(CurrencyType.valueOf(request.getCurrencyType()));
            account.setCustomer(customer);
            account.setAccountType(a.getClass().getSimpleName());


            //customer.getAccounts().add(account);

            accountRepository.save(account);
            customerRepository.save(customer);

            if (a instanceof DepositAccount) {

                SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_DEPOSIT_ACCOUNT.toString(), new Timestamp(System.currentTimeMillis()));
                systemOperations.setCustomer(account.getCustomer());
                systemOperationsRepository.save(systemOperations);
            }


            if (a instanceof SavingAccount) {

                SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_SAVING_ACCOUNT.toString(), new Timestamp(System.currentTimeMillis()));
                systemOperations.setCustomer(account.getCustomer());
                systemOperationsRepository.save(systemOperations);
            }

            isUnique=false;
        }

        return ResponseEntity.status(HttpStatus.OK).body("Account created");
    }

    @Override
    public <T extends Account> ResponseEntity<Object> deleteAccount(T a,String accountNumber){

        Account account =null;
        AccountRepository accountRepository =null;

        if (a instanceof DepositAccount) {

            account = depositAccountRepository.findByAccountNumber(accountNumber);
            accountRepository = depositAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        }
        if (a instanceof SavingAccount) {

            account = savingAccountRepository.findByAccountNumber(accountNumber);
            accountRepository = savingAccountRepository;

            if(account==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        }

        if(account.getBalance()!=0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account have a balance.Delete operation is not allowed.");
        }


        if (a instanceof DepositAccount) {

            SystemOperations systemOperations = new SystemOperations(0L, OperationType.DELETE_DEPOSIT_ACCOUNT.toString(), new Timestamp(System.currentTimeMillis()));
            systemOperations.setCustomer(account.getCustomer());
            systemOperationsRepository.save(systemOperations);
        }


        if (a instanceof SavingAccount) {

            SystemOperations systemOperations = new SystemOperations(0L, OperationType.DELETE_SAVING_ACCOUNT.toString(), new Timestamp(System.currentTimeMillis()));
            systemOperations.setCustomer(account.getCustomer());
            systemOperationsRepository.save(systemOperations);
        }

        accountRepository.delete(account);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account deleted.");


    }

    @Override
    public <T extends Account> ResponseEntity<Object> createSavingAccount(T a, CreateSavingAccountRequest request) throws IOException {
        Customer customer = customerRepository.findById(request.getCustomerId());

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        SavingAccount savingAccount = new SavingAccount();
        AccountRepository accountRepository = savingAccountRepository;

        if(!(request.getCurrencyType().equals("USD")||request.getCurrencyType().equals("TRY")||request.getCurrencyType().equals("EUR"))){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account type must be USD,EUR or TRY");
        }

        if(request.getDay()>365 || request.getDay()<0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Day must be between 0 and 365");

        if(request.getBalance()<1000)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The account can be opened with a minimum of 1000 "+request.getCurrencyType().toString());


        boolean isUnique=true;

        while (isUnique){

            AccountNumberGenerator accountNumberGenerator=new AccountNumberGenerator();
            String accountNumber=accountNumberGenerator.generateAccountNumber();

            if(accountRepository.findByAccountNumber(accountNumber)!=null){
                continue;
            }

            IbanNumberGenerator ibanNumberGenerator = new IbanNumberGenerator();
            String ibanNo=ibanNumberGenerator.ibanGenerate(accountNumber);

            if(accountRepository.findByIbanNo(ibanNo)!=null){
                continue;
            }

            savingAccount.setDay(request.getDay());
            savingAccount.setAccountNumber(accountNumber);
            savingAccount.setIbanNo(ibanNo);
            savingAccount.setCustomer(customer);
            savingAccount.setCurrencyType(CurrencyType.valueOf(request.getCurrencyType()));
            savingAccount.setBalance(request.getBalance());
            savingAccount.setCustomer(customer);
            savingAccount.setAccountType(a.getClass().getSimpleName());

            Date startDate = new Date(System.currentTimeMillis());
            Date endDate=new Date();

            Calendar calendar =Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE,request.getDay());
            endDate=calendar.getTime();

            savingAccount.setCreatedDate(startDate);
            savingAccount.setEndDate(endDate);

            savingAccount.setActive(true);
            savingAccount.setRemainingDay(request.getDay());
            savingAccount.setStarterBalance(request.getBalance());


            ObjectMapper objectMapper =new ObjectMapper();
            URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
            CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

            double newCustomerAsset = request.getBalance()/currencyClass.getRates().get(savingAccount.getCurrencyType().toString());
            customer.setAsset(customer.getAsset()+newCustomerAsset);


            accountRepository.save(savingAccount);
            customerRepository.save(customer);



                AccountTransactionRequest transactionRequest =new AccountTransactionRequest();
                transactionRequest.setAmount(request.getBalance());
                transactionRequest.setAccountNumber(accountNumber);

                saveTransactionService.saveBalanceOnAccount("AddBalance",savingAccount,transactionRequest);


                SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_SAVING_ACCOUNT.toString(), new Timestamp(System.currentTimeMillis()));
                systemOperations.setCustomer(savingAccount.getCustomer());
                systemOperationsRepository.save(systemOperations);


            isUnique=false;
        }

        return ResponseEntity.status(HttpStatus.OK).body("Account created");
    }



}
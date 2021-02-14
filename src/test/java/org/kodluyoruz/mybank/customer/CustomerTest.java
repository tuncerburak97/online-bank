package org.kodluyoruz.mybank.customer;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.repository.account.SavingAccountRepository;
import org.kodluyoruz.mybank.request.CreateAccountRequest;
import org.kodluyoruz.mybank.request.card.CreateCreditCardRequest;
import org.kodluyoruz.mybank.request.customer.create.CreateCustomerAddressRequest;
import org.kodluyoruz.mybank.request.customer.create.CreateCustomerContactRequest;
import org.kodluyoruz.mybank.request.customer.create.CreateCustomerRequest;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;
import org.kodluyoruz.mybank.service.impl.CustomerServiceImpl;
import org.kodluyoruz.mybank.service.impl.account.AccountServiceImpl;
import org.kodluyoruz.mybank.service.impl.card.CardServiceImpl;
import org.kodluyoruz.mybank.service.impl.transaction.AccountTransactionServiceImpl;
import org.kodluyoruz.mybank.service.impl.transaction.CardTransactionServiceImpl;
import org.kodluyoruz.mybank.service.impl.transaction.TransferTransactionServiceImpl;
import org.kodluyoruz.mybank.service.transaction.CardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerServiceImpl customerService;


    @Autowired
    private SavingAccountRepository savingAccountRepository;

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private TransferTransactionServiceImpl transferTransactionService;

    @Autowired
    private CardServiceImpl cardService;

    @Autowired
    private CardTransactionService cardTransactionService;


    @TestConfiguration
    static class CustomerServiceTestContextConfiguration {
        @Bean
        public CustomerServiceImpl customerServiceImplTest() {
            return new CustomerServiceImpl();

        }
    }

    @TestConfiguration
    static class AccountTransactionServiceTestContextConfiguration {
        @Bean
        public AccountTransactionServiceImpl accountTransactionService() {
            return new AccountTransactionServiceImpl();

        }
    }

    @TestConfiguration
    static class AccountServiceTestContextConfiguration {
        @Bean
        public AccountServiceImpl accountService() {
            return new AccountServiceImpl();

        }
    }

    @TestConfiguration
    static class TransferTransactionServiceTestContextConfiguration {
        @Bean
        public TransferTransactionServiceImpl transferTransactionService() {
            return new TransferTransactionServiceImpl();

        }
    }

    @TestConfiguration
    static class CardServiceTestContextConfiguration {
        @Bean
        public CardServiceImpl cardService() {
            return new CardServiceImpl();

        }
    }

    @TestConfiguration
    static class CardTransactionServiceContextConfiguration {
        @Bean
        public CardTransactionServiceImpl cardTransactionService() {
            return new CardTransactionServiceImpl();

        }
    }


    @BeforeEach
    public void createCustomer1() throws Exception {


        CreateCustomerRequest request = new CreateCustomerRequest();

        request.setName("Dilara");
        request.setSurname("Deniz");
        request.setGender("Female");
        request.setIdentificationNumber("28865218606");

        CreateCustomerContactRequest contactRequest = new CreateCustomerContactRequest("dilara@gmail.com", "05417329835");
        CreateCustomerAddressRequest addressRequest = new CreateCustomerAddressRequest("Emrah", "Ankara", "06565");

        request.setContact(contactRequest);
        request.setAddress(addressRequest);

        customerService.create(request);

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        Customer customer = customerRepository.findByIdentificationNumber("28865218606");

        long id =customer.getId();

        createAccountRequest.setCustomerId(customer.getId());
        createAccountRequest.setCurrencyType("USD");

        DepositAccount depositAccount = new DepositAccount();

        accountService.createAccount(depositAccount, createAccountRequest);


    }

    @BeforeEach
    void createCustomer2() throws Exception {

        CreateCustomerRequest request = new CreateCustomerRequest();

        request.setName("Burak");
        request.setSurname("Tuncer");
        request.setGender("Male");
        request.setIdentificationNumber("28865218605");

        CreateCustomerContactRequest contactRequest = new CreateCustomerContactRequest("burak@gmail.com", "05417329834");
        CreateCustomerAddressRequest addressRequest = new CreateCustomerAddressRequest("Doctor", "Istanbul", "34565");

        request.setContact(contactRequest);
        request.setAddress(addressRequest);


        customerService.create(request);

        Customer customer = customerRepository.findByIdentificationNumber("28865218605");

        long id =customer.getId();
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setCustomerId(customer.getId());
        createAccountRequest.setCurrencyType("USD");

        DepositAccount depositAccount = new DepositAccount();

        accountService.createAccount(depositAccount, createAccountRequest);



    }


    @Test
    void createService() throws Exception {

        CreateCustomerRequest request = new CreateCustomerRequest();

        request.setName("Mehmet");
        request.setSurname("Er");
        request.setGender("Male");
        request.setIdentificationNumber("12345678998");

        CreateCustomerContactRequest contactRequest = new CreateCustomerContactRequest("mehmet@gmail.com", "05415658987");
        CreateCustomerAddressRequest addressRequest = new CreateCustomerAddressRequest("Long", "Mersin", "56545");

        request.setContact(contactRequest);
        request.setAddress(addressRequest);


        customerService.create(request);

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();

        Customer customer = customerRepository.findByName("Mehmet");

        createAccountRequest.setCustomerId(customer.getId());
        createAccountRequest.setCurrencyType("TRY");

        DepositAccount depositAccount = new DepositAccount();

        accountService.createAccount(depositAccount, createAccountRequest);
    }


    @Test
    void listAll() {

        List<Customer> findAll = customerRepository.findAll();

        int size = findAll.size();
        Assertions.assertEquals(2, size);

    }

    @Test
    public void findByName() {

        Customer customer = customerRepository.findByName("Dilara");
        Assertions.assertNotNull(customer);


    }


    @Test
    public void createAccount() throws IOException {


        CreateAccountRequest request = new CreateAccountRequest();

        Customer customer = customerRepository.findByName("Dilara");

        request.setCustomerId(customer.getId());
        request.setCurrencyType("TRY");

        DepositAccount depositAccount = new DepositAccount();

        ResponseEntity actual = accountService.createAccount(depositAccount, request);

        ResponseEntity expected = ResponseEntity.status(HttpStatus.OK).body("Account created");

        assertEquals(expected, actual);

    }


    @Test
    public void sendMoney() throws Exception {

        Customer sender = customerRepository.findByName("Dilara");
        Customer receiver = customerRepository.findByName("Burak");

        DepositAccount senderDeposit = depositAccountRepository.findByCustomerId(sender.getId());
        DepositAccount receiverDeposit = depositAccountRepository.findByCustomerId(receiver.getId());

        String senderIban = senderDeposit.getIbanNo();
        String receiverIban = receiverDeposit.getIbanNo();

        AccountTransactionRequest request = new AccountTransactionRequest();

        request.setAccountNumber(senderDeposit.getAccountNumber());
        request.setAmount(100);

        accountTransactionService.balanceOnAccount(senderDeposit, "AddBalance", request);


        TransferTransactionRequest transferTransactionRequest = new TransferTransactionRequest();

        transferTransactionRequest.setAmount(50);
        transferTransactionRequest.setFromIbanNo(senderIban);
        transferTransactionRequest.setToIbanNo(receiverIban);

        transferTransactionService.sendMoney(senderDeposit, receiverDeposit, transferTransactionRequest);

        Assertions.assertEquals(50, senderDeposit.getBalance());

    }

    @Test
    public void createCreditCard() {


        Customer customer = customerRepository.findByName("Dilara");

        CreditCard creditCard = new CreditCard();

        CreateCreditCardRequest request = new CreateCreditCardRequest();

        request.setCustomer_id(customer.getId());
        request.setCreditLimit(5000);

        ResponseEntity actual = cardService.createCreditCard(creditCard, request);

        ResponseEntity expected = ResponseEntity.status(HttpStatus.OK).body("Credit card created");

        Assertions.assertEquals(expected, actual);


    }
}






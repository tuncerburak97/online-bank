package org.kodluyoruz.mybank.service.account;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.request.account.CreateAccountRequest;
import org.kodluyoruz.mybank.request.account.CreateSavingAccountRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AccountService {

    public  <T extends Account> ResponseEntity<Object> createAccount(T a, CreateAccountRequest request) throws IOException;
    public <T extends Account> ResponseEntity<Object> deleteAccount(T a, String accountNumber);
    public <T extends Account> ResponseEntity<Object> createSavingAccount(T a, CreateSavingAccountRequest request) throws IOException;

}

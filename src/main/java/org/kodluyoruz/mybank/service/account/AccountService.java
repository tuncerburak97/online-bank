package org.kodluyoruz.mybank.service.account;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.request.CreateAccountRequest;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    public  <T extends Account> ResponseEntity<Object> createAccount(T a, CreateAccountRequest request);
    public <T extends Account> ResponseEntity<Object> deleteAccount(T a, String accountNumber);
}

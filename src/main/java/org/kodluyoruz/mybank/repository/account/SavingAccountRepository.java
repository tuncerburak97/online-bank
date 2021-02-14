package org.kodluyoruz.mybank.repository.account;

import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingAccountRepository extends AccountRepository {


    SavingAccount findByAccountNumber(String accountNumber);
    SavingAccount findByIbanNo(String ibanNo);
    SavingAccount findByCustomerId(long id);
}
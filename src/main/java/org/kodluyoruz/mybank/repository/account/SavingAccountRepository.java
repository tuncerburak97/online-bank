package org.kodluyoruz.mybank.repository.account;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SavingAccountRepository extends AccountRepository {


    SavingAccount findByAccountNumber(String accountNumber);
    SavingAccount findByIbanNo(String ibanNo);
    SavingAccount findByCustomerId(long id);

    SavingAccount findByAccountId(long id);
    Account findByAccountTypeAndAccountId(String type,long id);
    List<Account> findByAccountType(String accountType);
}
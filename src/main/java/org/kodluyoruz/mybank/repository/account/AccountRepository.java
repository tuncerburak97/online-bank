package org.kodluyoruz.mybank.repository.account;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.*;

@NoRepositoryBean
public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findByAccountNumber(String accountNumber);
    Account findByIbanNo(String ibanNo);




}

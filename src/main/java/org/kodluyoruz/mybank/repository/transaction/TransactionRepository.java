package org.kodluyoruz.mybank.repository.transaction;

import org.kodluyoruz.mybank.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

}

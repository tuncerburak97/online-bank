package org.kodluyoruz.mybank.repository.payment;

import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.payment.AutoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPaymentRepository extends JpaRepository<AutoPayment,Long> {

    AutoPayment findByDepositAccount(DepositAccount depositAccount);

}

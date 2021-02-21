package org.kodluyoruz.mybank.entity.account;

import lombok.Getter;
import lombok.Setter;
import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.kodluyoruz.mybank.entity.payment.AutoPayment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.List;

@Entity
@Getter
@Setter
public class DepositAccount extends Account{


    @OneToMany(mappedBy = "depositAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebitCard> debitCards;

    @OneToMany(mappedBy = "depositAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AutoPayment> autoPayments;

    /*
    @Version
    private long version;


     */
    public DepositAccount() {
    }

    public DepositAccount(long accountId, String accountNumber, String accountType, String ibanNo, CurrencyType currencyType, Customer customer) {
        super(accountId, accountNumber, accountType, ibanNo, currencyType, customer);
    }
}
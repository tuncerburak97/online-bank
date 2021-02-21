package org.kodluyoruz.mybank.entity.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.mybank.entity.Customer;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    private String accountNumber;
    private String accountType;
    private String ibanNo;




    private double balance;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    public Account(long accountId, String accountNumber, String accountType, String ibanNo, CurrencyType currencyType, Customer customer) {
    }
}

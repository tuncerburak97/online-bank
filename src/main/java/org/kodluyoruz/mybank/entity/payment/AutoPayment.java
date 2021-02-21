package org.kodluyoruz.mybank.entity.payment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.mybank.entity.account.DepositAccount;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long auto_id;

    private int day;
    private double amount;
    private String currency;

    @ManyToOne
    @JsonIgnore
    private DepositAccount depositAccount;




    @Version
    @JsonIgnore
    private long version;


}

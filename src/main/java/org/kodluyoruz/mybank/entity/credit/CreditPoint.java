package org.kodluyoruz.mybank.entity.credit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.mybank.entity.customer.Customer;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditPoint{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int ranking;
    private double maxCreditLimit;



}

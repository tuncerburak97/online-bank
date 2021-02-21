package org.kodluyoruz.mybank.entity.interest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double interestRate;
    private String interestType;
    private String currencyType;
}

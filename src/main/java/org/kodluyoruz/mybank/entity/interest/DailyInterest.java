package org.kodluyoruz.mybank.entity.interest;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class DailyInterest extends Interest {


    public DailyInterest() {
    }

    public DailyInterest(long id, double interestRate, String interestType, String currencyType) {
        super(id, interestRate, interestType, currencyType);
    }
}

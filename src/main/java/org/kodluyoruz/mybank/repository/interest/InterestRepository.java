package org.kodluyoruz.mybank.repository.interest;

import org.kodluyoruz.mybank.entity.interest.DailyInterest;
import org.kodluyoruz.mybank.entity.interest.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface InterestRepository extends JpaRepository<Interest,Long> {

    DailyInterest findByCurrencyType(String currencyType);

}

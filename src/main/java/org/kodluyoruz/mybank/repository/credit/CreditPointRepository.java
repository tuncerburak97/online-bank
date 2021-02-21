package org.kodluyoruz.mybank.repository.credit;

import org.kodluyoruz.mybank.entity.credit.CreditPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditPointRepository extends JpaRepository<CreditPoint, Long> {

    CreditPoint findByRanking(int ranking);
}

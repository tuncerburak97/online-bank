package org.kodluyoruz.mybank.service.impl.credit;

import org.kodluyoruz.mybank.entity.credit.CreditPoint;
import org.kodluyoruz.mybank.repository.credit.CreditPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CreditPointServiceImpl {

    @Autowired
    private CreditPointRepository creditPointRepository;


    //@PostConstruct
    public void createCreditPointTable(){

        CreditPoint firstRank = new CreditPoint();
        CreditPoint secondRank = new CreditPoint();
        CreditPoint thirdRank = new CreditPoint();
        CreditPoint fourthRank = new CreditPoint();
        CreditPoint fifthRank = new CreditPoint();

        firstRank.setRanking(1);
        firstRank.setMaxCreditLimit(5000);
        creditPointRepository.save(firstRank);

        secondRank.setRanking(2);
        secondRank.setMaxCreditLimit(10000);
        creditPointRepository.save(secondRank);

        thirdRank.setRanking(3);
        thirdRank.setMaxCreditLimit(20000);
        creditPointRepository.save(thirdRank);

        fourthRank.setRanking(4);
        fourthRank.setMaxCreditLimit(50000);
        creditPointRepository.save(fourthRank);

        fifthRank.setRanking(5);
        fifthRank.setMaxCreditLimit(100000);
        creditPointRepository.save(fifthRank);

    }


}

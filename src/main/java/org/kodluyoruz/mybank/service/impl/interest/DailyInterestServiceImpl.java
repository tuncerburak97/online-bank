package org.kodluyoruz.mybank.service.impl.interest;

import org.kodluyoruz.mybank.entity.interest.DailyInterest;
import org.kodluyoruz.mybank.entity.interest.Interest;
import org.kodluyoruz.mybank.repository.interest.DailyInterestRepository;
import org.kodluyoruz.mybank.repository.interest.InterestRepository;
import org.kodluyoruz.mybank.service.interest.DailyInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DailyInterestServiceImpl implements DailyInterestService {

    @Autowired
    private DailyInterestRepository dailyInterestRepository;

    @Autowired
    private InterestRepository interestRepository;

    public DailyInterestServiceImpl(DailyInterestRepository dailyInterestRepository) {
        this.dailyInterestRepository = dailyInterestRepository;
    }

    public DailyInterestServiceImpl() {

    }


    // @PostConstruct
    public ResponseEntity<Object> createRates(){

        DailyInterest dailyTryRates = new DailyInterest();
        DailyInterest dailyUsdRates = new DailyInterest();
        DailyInterest dailyEurRates = new DailyInterest();


        dailyTryRates.setInterestRate(15);
        dailyTryRates.setInterestType("Daily");
        dailyTryRates.setCurrencyType("TRY");

        dailyUsdRates.setInterestRate(2);
        dailyUsdRates.setCurrencyType("USD");
        dailyUsdRates.setInterestType("Daily");

        dailyEurRates.setInterestRate(1);
        dailyEurRates.setCurrencyType("EUR");
        dailyEurRates.setInterestType("Daily");

        dailyInterestRepository.save(dailyEurRates);
        dailyInterestRepository.save(dailyTryRates);
        dailyInterestRepository.save(dailyUsdRates);


        return ResponseEntity.ok().body("Rates saved");

    }

    @Override
    public ResponseEntity<Object> calculateInterest(String currencyType,double starterBalance,double dayNumber){


        if(dayNumber<0 || dayNumber>365)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Day must be between 0 and 365");

        if(starterBalance<1000){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Starter balance have minimum 1000 "+currencyType);
        }

        double result=starterBalance+this.interestCalculator(currencyType,starterBalance,dayNumber);
        return ResponseEntity.ok().body("Your total balance will be "+result);
    }

    public double interestCalculator(String currencyType,double starterBalance,double dayNumber){

        Interest interest =interestRepository.findByCurrencyType(currencyType);

        double rate = interest.getInterestRate();
        double interestMoneyCalculate=(dayNumber/365)*starterBalance*(rate/100);
        double taxRate=0.1;

        double interestYield = (interestMoneyCalculate)-(taxRate*interestMoneyCalculate);
        return interestYield;
    }



}

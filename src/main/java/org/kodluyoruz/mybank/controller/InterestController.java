package org.kodluyoruz.mybank.controller;

import org.kodluyoruz.mybank.entity.interest.DailyInterest;
import org.kodluyoruz.mybank.service.interest.DailyInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interest")
public class InterestController {

    @Autowired
    private DailyInterestService dailyInterestService;


    @GetMapping("/calculateInterest/{currencyType}/{starterBalance}/{day}")
    public ResponseEntity<Object> getInterest(@PathVariable String currencyType,@PathVariable double starterBalance,@PathVariable double day){

        return dailyInterestService.calculateInterest(currencyType,starterBalance,day);

    }


}

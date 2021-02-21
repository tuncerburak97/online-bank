package org.kodluyoruz.mybank.checker;

import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FormatChecker {


    public boolean numberFormatChecker(String number){

        String regex = "^[0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(number);

        return matcher.matches();
    }


    public boolean nameAndLastnameFormatChecker(String name){

        String regex = "^[a-zA-Z]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();

    }

    public boolean emailChecker(String email){

        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }





}

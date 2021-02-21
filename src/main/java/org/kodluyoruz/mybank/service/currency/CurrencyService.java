package org.kodluyoruz.mybank.service.currency;

import org.kodluyoruz.mybank.entity.account.Account;

import java.io.IOException;
import java.net.MalformedURLException;

public interface CurrencyService {

    public void setCustomerAsset(double amount, Account account,String txnType) throws IOException;

    public double setCurrencyAsset(double amount,Account account) throws IOException;
    public double setAccountBalance(double amount, Account account) throws IOException;
}

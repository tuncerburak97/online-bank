package org.kodluyoruz.mybank.service.impl.payment;

import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.entity.account.DepositAccount;
import org.kodluyoruz.mybank.entity.account.SavingAccount;
import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.payment.AutoPayment;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.account.DepositAccountRepository;
import org.kodluyoruz.mybank.service.currency.CurrencyService;
import org.kodluyoruz.mybank.service.payment.AutoPaymentService;
import org.kodluyoruz.mybank.service.transaction.SaveTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AutoPaymentServiceImpl implements AutoPaymentService {

    @Autowired
    private DepositAccountRepository depositAccountRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SaveTransactionService saveTransactionService;

    //@Scheduled
    public void getAutoPayment() throws IOException {

        List<Account> depositAccounts=depositAccountRepository.findByAccountType("DepositAccount");

        for (int i=0;i<depositAccounts.size();i++){

            DepositAccount depositAccount = depositAccountRepository.findByAccountId(depositAccounts.get(i).getAccountId());

            for(int j=0;j<depositAccount.getAutoPayments().size();j++){

                AutoPayment autoPayment =depositAccount.getAutoPayments().get(j);

                Customer customer = depositAccount.getCustomer();

                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                if(currentDay==autoPayment.getDay()){

                    double accountBalance=currencyService.setCurrencyAsset(autoPayment.getAmount(),depositAccount);

                    if(autoPayment.getAmount()<=accountBalance){

                        double withdrawAmountToDeposit=currencyService.setAccountBalance(autoPayment.getAmount(),depositAccount);
                        depositAccount.setBalance(depositAccount.getBalance()-withdrawAmountToDeposit);
                        customer.setAsset(customer.getAsset()-autoPayment.getAmount());

                        customerRepository.save(customer);
                        depositAccountRepository.save(depositAccount);

                        saveTransactionService.saveAutoPaymentTransaction(depositAccount,withdrawAmountToDeposit,"AutoPayment");
                    }

                }
            }
        }




    }



}

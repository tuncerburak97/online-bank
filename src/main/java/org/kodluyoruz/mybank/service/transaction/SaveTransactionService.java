package org.kodluyoruz.mybank.service.transaction;

import org.kodluyoruz.mybank.domain.CurrencyOperation;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.account.Account;
import org.kodluyoruz.mybank.request.transaction.AccountTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.CardTransactionRequest;
import org.kodluyoruz.mybank.request.transaction.DebtOnAccountRequest;
import org.kodluyoruz.mybank.request.transaction.TransferTransactionRequest;

public interface SaveTransactionService {

    public void saveBalanceOnAccount(String txnType, Account account, AccountTransactionRequest request);
    public void saveDebtOnAccount(Account account, DebtOnAccountRequest request, CurrencyOperation currencyClass);
    public void saveCreditCardTransaction(CardTransactionRequest request, Customer customer, String txnType);
    public void saveDebitCardTransaction(CardTransactionRequest request, Account account, String txnType,CurrencyOperation currencyClass);
    public void saveTransferTransaction(TransferTransactionRequest request, Account sender, Account receiver, String txnType, CurrencyOperation currencyClass);
    public void saveTransferTransactionBetweenAccounts(Account account,double amount,String txnType);

}

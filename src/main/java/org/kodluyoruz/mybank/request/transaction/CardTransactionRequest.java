package org.kodluyoruz.mybank.request.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardTransactionRequest {

    private String cardNumber;
    private double amount;

}

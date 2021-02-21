package org.kodluyoruz.mybank.request.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    private String currencyType;
    private long customerId;




}
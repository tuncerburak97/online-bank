package org.kodluyoruz.mybank.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    private String currencyType;
    public long customerId;



}
package org.kodluyoruz.mybank.request.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndTransactionDate {

    private String year;
    private String month;
    private String day;
}

package org.kodluyoruz.mybank.repository.card;

import org.kodluyoruz.mybank.entity.card.CreditCard;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends CardRepository {

    CreditCard findByCardNumber(String cardNumber);

    CreditCard findByExpiredDate(String expiredDate);


}

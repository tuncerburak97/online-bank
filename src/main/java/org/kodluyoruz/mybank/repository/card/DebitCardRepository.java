package org.kodluyoruz.mybank.repository.card;

import org.kodluyoruz.mybank.entity.card.DebitCard;
import org.springframework.stereotype.Repository;

@Repository
public interface DebitCardRepository extends CardRepository {

    DebitCard findByCardNumber(String cardNumber);
}

package org.kodluyoruz.mybank.repository;

import org.kodluyoruz.mybank.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer findById(long id);
    Customer findByContact_Email(String email);
    Customer findByContact_PhoneNumber(String phoneNumber);
    Customer findByName(String name);
    Customer findByIdentificationNumber(String idNumber);

}
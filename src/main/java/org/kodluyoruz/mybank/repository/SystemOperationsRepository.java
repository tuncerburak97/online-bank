package org.kodluyoruz.mybank.repository;

import org.kodluyoruz.mybank.entity.customer.Customer;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SystemOperationsRepository extends JpaRepository<SystemOperations,Long> {

    List<SystemOperations> findByCustomer(Customer customer);
    SystemOperations findByOperationId(long id);
    SystemOperations findByOperationType(String type);

}


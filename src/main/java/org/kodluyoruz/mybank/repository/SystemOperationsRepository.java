package org.kodluyoruz.mybank.repository;

import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemOperationsRepository extends JpaRepository<SystemOperations,Long> {

    SystemOperations findByCustomer(Customer customer);
    SystemOperations findByOperationId(long id);
    SystemOperations findByOperationType(String type);

}


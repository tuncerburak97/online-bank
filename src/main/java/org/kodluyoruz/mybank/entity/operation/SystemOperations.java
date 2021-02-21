package org.kodluyoruz.mybank.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kodluyoruz.mybank.entity.customer.Customer;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SystemOperations {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long operationId;

    private String operationType;
    private Timestamp operationTime;

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    public SystemOperations(long operationId, String operationType, Timestamp operationTime) {
        this.operationId = operationId;
        this.operationType = operationType;
        this.operationTime = operationTime;
    }
}
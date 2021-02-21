package org.kodluyoruz.mybank.controller;


import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.request.customer.create.CreateCustomerRequest;
import org.kodluyoruz.mybank.request.customer.update.UpdateAddress;
import org.kodluyoruz.mybank.request.customer.update.UpdateEmail;
import org.kodluyoruz.mybank.request.customer.update.UpdatePhoneNumber;
import org.kodluyoruz.mybank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody CreateCustomerRequest request) throws Exception {
        return customerService.create(request);
    }

    @GetMapping("/listAll")
    public List<Customer> listAll(){
        return customerService.findAll();
    }

    @DeleteMapping("/deleteById/{id}")
    private ResponseEntity<Object> deleteById(@PathVariable long id){
        return customerService.deleteCustomer(id);
    }

    @PutMapping("/update/email/{id}")
    private ResponseEntity<Object> updateEmail(@PathVariable long id,@RequestBody UpdateEmail email){
        return customerService.updateEmail(email,id);
    }

    @PutMapping("/update/phoneNumber/{id}")
    private ResponseEntity<Object> updatePhoneNumber(@PathVariable long id,@RequestBody UpdatePhoneNumber number){
        return customerService.updatePhoneNumber(number,id);
    }
    @PutMapping("/update/address/{id}")
    private ResponseEntity<Object> updateAddress(@RequestBody UpdateAddress address, @PathVariable long id){
        return customerService.updateAddress(address,id);
    }


}

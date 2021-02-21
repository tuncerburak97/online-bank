package org.kodluyoruz.mybank.service.impl;

import org.kodluyoruz.mybank.checker.FormatChecker;
import org.kodluyoruz.mybank.entity.Address;
import org.kodluyoruz.mybank.entity.Contact;
import org.kodluyoruz.mybank.entity.Customer;
import org.kodluyoruz.mybank.entity.operation.OperationType;
import org.kodluyoruz.mybank.entity.operation.SystemOperations;
import org.kodluyoruz.mybank.repository.CustomerRepository;
import org.kodluyoruz.mybank.repository.SystemOperationsRepository;
import org.kodluyoruz.mybank.request.customer.create.CreateCustomerRequest;
import org.kodluyoruz.mybank.request.customer.update.UpdateAddress;
import org.kodluyoruz.mybank.request.customer.update.UpdateEmail;
import org.kodluyoruz.mybank.request.customer.update.UpdatePhoneNumber;
import org.kodluyoruz.mybank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;

    private final FormatChecker formatChecker=new FormatChecker();


    public CustomerServiceImpl(CustomerRepository customerRepository,SystemOperationsRepository systemOperationsRepository) {
        this.customerRepository = customerRepository;
        this.systemOperationsRepository = systemOperationsRepository;
    }

    public CustomerServiceImpl() {

    }

    @Override
    public ResponseEntity<Object> create(CreateCustomerRequest request) throws Exception {

        boolean emailChecker=formatChecker.emailChecker(request.getContact().getEmail());
        boolean nameCheck=formatChecker.nameAndLastnameFormatChecker(request.getName());
        boolean lastNameCheck=formatChecker.nameAndLastnameFormatChecker(request.getSurname());
        boolean identificationNumberTypeChecker =formatChecker.numberFormatChecker(request.getIdentificationNumber());
        boolean phoneNumberChecker=formatChecker.numberFormatChecker(request.getContact().getPhoneNumber());


        if(!nameCheck || !lastNameCheck){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid name or lastname type");
        }


        if(!identificationNumberTypeChecker){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid identification type");
        }

        if(!emailChecker){

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid e-mail type");
        }

        if(request.getIdentificationNumber().length()!=11){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Identification number length must be 11");
        }

        if(!phoneNumberChecker){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid phone number type");
        }

        if(request.getContact().getPhoneNumber().length()!=11){
            if(!request.getContact().getPhoneNumber().startsWith("0")){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Phone number must be start with 0");

            }
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Phone number size must be 11");
        }


        boolean identificationNumberCheck=this.identificationNumberChecker(request.getIdentificationNumber());
        if(!identificationNumberCheck)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("A customer with this identification number was found in the records");


        boolean phoneNumberCheck = this.phoneNumberChecker(request.getContact().getPhoneNumber());
        if(!phoneNumberCheck)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("A customer with this phone number was found in the records.");

        boolean emailCheck=this.customerEmailChecker(request.getContact().getEmail());
        if(!emailCheck)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("A customer with this email was found in the records.");

        Customer customer = new Customer();

        customer.setAsset(0);
        customer.setName(request.getName());
        customer.setGender(request.getGender());
        customer.setLastName(request.getSurname());
        customer.setIdentificationNumber(request.getIdentificationNumber());

        Address address = new Address(0L,request.getAddress().getStreet(),request.getAddress().getCity(),request.getAddress().getZipcode(),customer);
        Contact contact = new Contact(0L,request.getContact().getEmail(),request.getContact().getPhoneNumber(),customer);


        customer.setAddress(address);
        customer.setContact(contact);

        customerRepository.save(customer);


        SystemOperations systemOperations = new SystemOperations(0L, OperationType.CREATE_CUSTOMER.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);
        systemOperationsRepository.save(systemOperations);

        return ResponseEntity.status(HttpStatus.OK).body("Customer created");


    }
    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public ResponseEntity<Object> deleteCustomer(long id) {

        Customer customer = customerRepository.findById(id);

        if(customer==null){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer not found");
        }
        return isDeletable(customer);
    }

    @Override
    public Customer findByIdentificationNumber(String idNumber) {
        return customerRepository.findByIdentificationNumber(idNumber);
    }


    ResponseEntity<Object> isDeletable(Customer customer){

        if(customer.getAsset()!=0){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer have a asset.Deletion not allowed");
        }

        for(int i=0;i<customer.getCreditCards().size();i++){

            double debt = customer.getCreditCards().get(i).getCardLimit()- customer.getCreditCards().get(i).getCurrentLimit();

            if(debt!=0){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer have a credit debt.Deletion not allowed");
            }
        }


        SystemOperations systemOperations = new SystemOperations(0L, OperationType.DELETE_CUSTOMER.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);
        systemOperationsRepository.save(systemOperations);

        customerRepository.delete(customer);
        return ResponseEntity.status(HttpStatus.OK).body("Customer deleted");
    }

    @Override
    public ResponseEntity<Object> updateEmail(UpdateEmail email, long id){

        Customer customer = customerRepository.findById(id);

        if(customer==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");

        }
        boolean isUnique =this.customerEmailChecker(email.getEmail());

        if(!isUnique){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This e-mail already used in bank.Update email operation not allowed");
        }

        boolean emailChecker = formatChecker.emailChecker(email.getEmail());

        if(!emailChecker){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid e-mail type");
        }


        Contact contact = customer.getContact();
        contact.setEmail(email.getEmail());

        customer.setContact(contact);
        customerRepository.save(customer);

        SystemOperations systemOperations = new SystemOperations(0L, OperationType.UPDATE_MAIL.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);
        return ResponseEntity.status(HttpStatus.OK).body("E-mail updated");

    }

    @Override
    public ResponseEntity<Object> updatePhoneNumber(UpdatePhoneNumber number, long id) {


        Customer customer = customerRepository.findById(id);

        if(customer==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");

        }
        boolean isUnique =this.phoneNumberChecker(number.getPhoneNumber());

        if(!isUnique){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This phone number already used in bank.Update email operation not allowed");
        }

        boolean phoneNumberChecker =formatChecker.numberFormatChecker(number.getPhoneNumber());

        if(!phoneNumberChecker){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid phone number type");
        }


        Contact contact = customer.getContact();
        contact.setPhoneNumber(number.getPhoneNumber());

        customer.setContact(contact);
        customerRepository.save(customer);
        SystemOperations systemOperations = new SystemOperations(0L, OperationType.UPDATE_PHONE.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);
        return ResponseEntity.status(HttpStatus.OK).body("Phone number updated");
    }

    @Override
    public ResponseEntity<Object> updateAddress(UpdateAddress address, long id) {

        Customer customer = customerRepository.findById(id);

        if(customer==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Address address1 = customer.getAddress();

        address1.setZipcode(address.getZipcode());
        address1.setStreet(address.getStreet());
        address1.setCity(address.getCity());

        customer.setAddress(address1);
        customerRepository.save(customer);
        SystemOperations systemOperations = new SystemOperations(0L, OperationType.UPDATE_ADDRESS.toString(), new Timestamp(System.currentTimeMillis()));
        systemOperations.setCustomer(customer);

        return ResponseEntity.status(HttpStatus.OK).body("Address updated");

    }
    public boolean identificationNumberChecker(String idNumber){

        Customer idNumberChecker =customerRepository.findByIdentificationNumber(idNumber);

        if(idNumberChecker!=null){
            return false;
        }

        return true;
    }

    public boolean customerEmailChecker(String email){

        Customer emailChecker =customerRepository.findByContact_Email(email);

        if(emailChecker!=null){
            return false;
        }
        return true;
    }

    public boolean phoneNumberChecker(String phoneNumber){

        Customer phoneNumberChecker =customerRepository.findByContact_PhoneNumber(phoneNumber);

        if(phoneNumberChecker!=null){
            return false;
        }
        return true;
    }







}

package com.example.customerapi.service;

import com.example.customerapi.exception.CustomerNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository repository;

    public Customer createCustomer(Customer customer) {
        if (repository.findByEmailAddress(customer.getEmailAddress()).isPresent()) {
            throw new IllegalArgumentException("Email address must be unique: " + customer.getEmailAddress());
        }
        logger.debug("Saving customer to the database: {}", customer);
        return repository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        logger.debug("Retrieving all customers from the database");
        return repository.findAll();
    }

    public Customer getCustomerById(UUID id) {
        logger.debug("Retrieving customer with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with ID {} not found", id);
                    throw new CustomerNotFoundException("Customer not found with ID: " + id);
                });
    }

    public Customer updateCustomer(UUID id, Customer customerDetails) {
        logger.debug("Updating customer with ID: {}", id);
        Customer customer = getCustomerById(id);
        customer.setFirstName(customerDetails.getFirstName());
        customer.setMiddleName(customerDetails.getMiddleName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmailAddress(customerDetails.getEmailAddress());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        return repository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        logger.debug("Deleting customer with ID: {}", id);
        Customer customer = getCustomerById(id);
        repository.delete(customer);
        logger.info("Customer with ID {} deleted", id);
    }
}

package com.example.customerapi.service;

import com.example.customerapi.exception.CustomerNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer(null, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(repository.save(customer)).thenReturn(customer);

        Customer createdCustomer = service.createCustomer(customer);

        assertNotNull(createdCustomer);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void testCreateCustomerNotUniqueEmail() {
        Customer customer = new Customer(null, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(repository.findByEmailAddress(customer.getEmailAddress())).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.createCustomer(customer));

        assertEquals("Email address must be unique: " + customer.getEmailAddress(), exception.getMessage());
        verify(repository, times(1)).findByEmailAddress(customer.getEmailAddress());
    }

    @Test
    void testGetAllCustomers() {
        Customer customer1 = new Customer(UUID.randomUUID(), "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        Customer customer2 = new Customer(UUID.randomUUID(), "Jane", null, "Doe", "jane.doe@example.com", "+0987654321");

        when(repository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> customers = service.getAllCustomers();

        assertNotNull(customers);
        assertEquals(2, customers.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer foundCustomer = service.getCustomerById(customerId);

        assertNotNull(foundCustomer);
        assertEquals(customer, foundCustomer);
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    void testGetCustomerByIdNotFound() {
        UUID customerId = UUID.randomUUID();
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> service.getCustomerById(customerId));

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    void testUpdateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        Customer updatedDetails = new Customer(null, "Johnny", "Middle", "Does", "johnny.does@example.com", "+9876543210");

        when(repository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(repository.save(any(Customer.class))).thenReturn(updatedDetails);

        Customer updatedCustomer = service.updateCustomer(customerId, updatedDetails);

        assertNotNull(updatedCustomer);
        assertEquals("Johnny", updatedCustomer.getFirstName());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(repository).delete(customer);

        service.deleteCustomer(customerId);

        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).delete(customer);
    }

    @Test
    void testDeleteCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(customerId));

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        verify(repository, times(1)).findById(customerId);
    }
}

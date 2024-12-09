package com.example.customerapi.controller;

import com.example.customerapi.exception.CustomerNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController controller;

    @Mock
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer(UUID.randomUUID(), "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(service.createCustomer(any(Customer.class))).thenReturn(customer);

        ResponseEntity<Customer> response = controller.createCustomer(customer);

        assertNotNull(response);
        assertEquals(customer, response.getBody());
        verify(service, times(1)).createCustomer(customer);
    }

    @Test
    void testGetAllCustomers() {
        Customer customer1 = new Customer(UUID.randomUUID(), "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        Customer customer2 = new Customer(UUID.randomUUID(), "Jane", null, "Doe", "jane.doe@example.com", "+0987654321");

        when(service.getAllCustomers()).thenReturn(Arrays.asList(customer1, customer2));

        ResponseEntity<List<Customer>> response = controller.getAllCustomers();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(service, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomerById() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(service.getCustomerById(customerId)).thenReturn(customer);

        ResponseEntity<Customer> response = controller.getCustomerById(customerId);

        assertNotNull(response);
        assertEquals(customer, response.getBody());
        verify(service, times(1)).getCustomerById(customerId);
    }

    @Test
    void testGetCustomerByIdNotFound() {
        UUID customerId = UUID.randomUUID();
        when(service.getCustomerById(customerId)).thenThrow(new CustomerNotFoundException("Customer not found"));

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> controller.getCustomerById(customerId));

        assertEquals("Customer not found", exception.getMessage());
        verify(service, times(1)).getCustomerById(customerId);
    }

    @Test
    void testUpdateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer updatedCustomer = new Customer(customerId, "John", "M", "Doe", "john.doe@example.com", "+1234567890");
        when(service.updateCustomer(eq(customerId), any(Customer.class))).thenReturn(updatedCustomer);

        ResponseEntity<Customer> response = controller.updateCustomer(customerId, updatedCustomer);

        assertNotNull(response);
        assertEquals(updatedCustomer, response.getBody());
        verify(service, times(1)).updateCustomer(customerId, updatedCustomer);
    }

    @Test
    void testDeleteCustomer() {
        UUID customerId = UUID.randomUUID();
        doNothing().when(service).deleteCustomer(customerId);

        ResponseEntity<Void> response = controller.deleteCustomer(customerId);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        verify(service, times(1)).deleteCustomer(customerId);
    }

    @Test
    void testDeleteCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        doThrow(new CustomerNotFoundException("Customer not found")).when(service).deleteCustomer(customerId);

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> controller.deleteCustomer(customerId));

        assertEquals("Customer not found", exception.getMessage());
        verify(service, times(1)).deleteCustomer(customerId);
    }
}

package com.example.customerapi.integration;

import com.example.customerapi.CustomerApiApplication;
import com.example.customerapi.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for testing the Customer API.
 *
 * This test class performs end-to-end tests by starting the Spring Boot application context
 * and using TestRestTemplate to interact with the API. No mocking is involved; these tests
 * interact with the real application, including the H2 database configured for testing.
 *
 * Annotations:
 * - @SpringBootTest: Starts the full application context for integration testing.
 * - webEnvironment = RANDOM_PORT: Runs the application on a random available port during tests.
 * - @LocalServerPort: Injects the random port used by the application.
 *
 * How to Run:
 * - Run this test class as a JUnit test from an IDE like IntelliJ or Eclipse.
 * - Use the command `mvn test` to execute this test class along with other test classes.
 *
 * Dependencies:
 * - H2 in-memory database: Used as the database for testing.
 * - TestRestTemplate: A Spring-provided utility for interacting with RESTful APIs in tests.
 */
@SpringBootTest(
        classes = CustomerApiApplication.class, // Specifies the main application class to load
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT // Runs the application on a random port
)
public class CustomerIntegrationTest {

    /**
     * Injects the port on which the application is running during the test.
     * This is necessary because the application runs on a random port to avoid conflicts.
     */
    @LocalServerPort
    private int port;

    /**
     * TestRestTemplate is a Spring-provided utility for testing RESTful APIs.
     * It simplifies sending HTTP requests and handling responses during integration tests.
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Constructs the base URL of the API dynamically using the injected port.
     *
     * @return The base URL of the API, e.g., "http://localhost:8080/api/customers"
     */
    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/customers";
    }

    /**
     * Integration test for creating a customer.
     * Validates that a customer can be successfully created via the POST /api/customers endpoint.
     */
    @Test
    public void testCreateCustomer() {
        // Arrange: Prepare the customer payload
        Customer customer = new Customer(null, "John", "M", "Doe", "john.doe@example.com", "+1234567890");

        // Act: Make the POST request to create a customer
        ResponseEntity<Customer> response = restTemplate.postForEntity(getBaseUrl(), customer, Customer.class);

        // Assert: Validate the response and ensure the customer was created
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Ensure the HTTP response status is 200 OK
        assertNotNull(response.getBody()); // Ensure the response body is not null
        assertNotNull(response.getBody().getId()); // Ensure the customer ID is generated
        assertEquals("John", response.getBody().getFirstName()); // Validate the customer's first name
    }

    /**
     * Integration test for retrieving all customers.
     * Validates that the GET /api/customers endpoint returns a list of customers.
     */
    @Test
    public void testGetAllCustomers() {
        // Act: Send a GET request to retrieve all customers
        ResponseEntity<Customer[]> response = restTemplate.getForEntity(getBaseUrl(), Customer[].class);

        // Assert: Validate the response
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Ensure the HTTP response status is 200 OK
        assertNotNull(response.getBody()); // Ensure the response body is not null
    }

    /**
     * Integration test for retrieving a single customer by ID.
     * Validates that a customer can be retrieved via the GET /api/customers/{id} endpoint.
     */
    @Test
    public void testGetCustomerById() {
        // Arrange: Create a customer to fetch
        Customer customer = new Customer(null, "Jane", null, "Smith", "jane.smith@example.com", "+9876543210");
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(getBaseUrl(), customer, Customer.class);
        UUID customerId = createResponse.getBody().getId(); // Extract the created customer's ID

        // Act: Fetch the customer by ID
        ResponseEntity<Customer> response = restTemplate.getForEntity(getBaseUrl() + "/" + customerId, Customer.class);

        // Assert: Validate the response
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Ensure the HTTP response status is 200 OK
        assertNotNull(response.getBody()); // Ensure the response body is not null
        assertEquals("Jane", response.getBody().getFirstName()); // Validate the customer's first name
    }

    /**
     * Integration test for updating a customer.
     * Validates that a customer's details can be updated via the PUT /api/customers/{id} endpoint.
     */
    @Test
    public void testUpdateCustomer() {
        // Arrange: Create a customer to update
        Customer customer = new Customer(null, "Tom", null, "Riddle", "tom.riddle@example.com", "+1111111111");
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(getBaseUrl(), customer, Customer.class);
        UUID customerId = createResponse.getBody().getId(); // Extract the created customer's ID

        // Prepare updated customer details
        Customer updatedCustomer = new Customer(customerId, "Tommy", null, "Riddle", "tommy.riddle@example.com", "+2222222222");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Customer> request = new HttpEntity<>(updatedCustomer, headers);

        // Act: Send a PUT request to update the customer
        ResponseEntity<Customer> response = restTemplate.exchange(getBaseUrl() + "/" + customerId, HttpMethod.PUT, request, Customer.class);

        // Assert: Validate the response and ensure the customer was updated
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Ensure the HTTP response status is 200 OK
        assertNotNull(response.getBody()); // Ensure the response body is not null
        assertEquals("Tommy", response.getBody().getFirstName()); // Validate the updated first name
    }

    /**
     * Integration test for deleting a customer.
     * Validates that a customer can be deleted via the DELETE /api/customers/{id} endpoint.
     */
    @Test
    public void testDeleteCustomer() {
        // Arrange: Create a customer to delete
        Customer customer = new Customer(null, "Alice", null, "Wonder", "alice.wonder@example.com", "+3333333333");
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(getBaseUrl(), customer, Customer.class);
        UUID customerId = createResponse.getBody().getId(); // Extract the created customer's ID

        // Act: Send a DELETE request to delete the customer
        restTemplate.delete(getBaseUrl() + "/" + customerId);

        // Assert: Ensure the customer is no longer retrievable
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/" + customerId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Ensure the HTTP response status is 404 Not Found
    }
}

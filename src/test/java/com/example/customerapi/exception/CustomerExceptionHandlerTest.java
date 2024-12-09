package com.example.customerapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerExceptionHandlerTest {

    @InjectMocks
    private CustomerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new CustomerExceptionHandler();
    }

    @Test
    void testHandleCustomerNotFoundException() {
        // Arrange
        String errorMessage = "Customer not found";
        CustomerNotFoundException exception = new CustomerNotFoundException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleCustomerNotFoundException(exception);

        // Assert
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Illegal argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        FieldError fieldError1 = new FieldError("customer", "emailAddress", "Invalid email address");
        FieldError fieldError2 = new FieldError("customer", "phoneNumber", "Phone number is required");

        // Mock the BindingResult to return field errors
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        // Create the MethodArgumentNotValidException with the mocked BindingResult
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        Map<String, String> responseBody = response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals("Invalid email address", responseBody.get("emailAddress"));
        assertEquals("Phone number is required", responseBody.get("phoneNumber"));
    }


    @Test
    void testHandleGenericException() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals("An unexpected error occurred.", response.getBody());
    }
}

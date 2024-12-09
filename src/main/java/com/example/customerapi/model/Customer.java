package com.example.customerapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "First Name is mandatory")
    @Size(max = 50, message = "First Name must be at most 50 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last Name is mandatory")
    @Size(max = 50, message = "Last Name must be at most 50 characters")
    private String lastName;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email address is mandatory")
    @Column(unique = true)
    private String emailAddress;

    @NotBlank(message = "Phone Number is mandatory")
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone Number must be valid and contain 10 to 15 digits")
    private String phoneNumber;
}

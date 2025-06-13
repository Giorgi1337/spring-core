package com.gym.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@SuperBuilder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 25, message = "First name must be at most 25 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 25, message = "Last name must be at most 25 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 25, message = "Username must be between 4 and 25 characters")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 50, message = "Password must be between 10 and 50 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Active status is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
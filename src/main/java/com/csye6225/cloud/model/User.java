package com.csye6225.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;


    @Column(unique = true, nullable = false)
    @JsonProperty("username")
    @Email(message = "Username/Email is not in format, please check")
    private String username;

    @JsonProperty("first_name")
    @Column(name="first_name", nullable = false)
    @NotEmpty(message="First Name cannot be null/empty")
    private String firstName;

    @JsonProperty("last_name")
    @Column(name="last_name", nullable = false)
    @NotEmpty(message="Last Name cannot be null/empty")
    private String lastName;


    @JsonProperty("password")
    @NotEmpty(message="Password cannot be null/empty")
    private String password;

    @JsonProperty(value ="account_created",access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="account_created")
    private LocalDateTime accountCreated;

    @JsonProperty(value = "account_updated",access = JsonProperty.Access.READ_ONLY)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="account_updated")
    private LocalDateTime accountUpdated;

    @JsonProperty(value = "verification_status")
    @Column(nullable = false)
    private boolean verified;

}

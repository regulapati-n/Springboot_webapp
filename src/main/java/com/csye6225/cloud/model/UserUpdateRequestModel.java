package com.csye6225.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
@JsonIgnoreProperties(allowGetters = true, ignoreUnknown = false)
public class UserUpdateRequestModel {
    @JsonProperty("first_name")
    @NotEmpty(message="First Name cannot be null/empty")
    private String firstName;

    @JsonProperty("last_name")
    @NotEmpty(message="Last Name cannot be null/empty")
    private String lastName;

    @JsonProperty("password")
    @NotEmpty(message="Password cannot be null/empty")
    private String password;



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

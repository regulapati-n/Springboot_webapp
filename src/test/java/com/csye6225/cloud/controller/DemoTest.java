package com.csye6225.cloud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import java.util.Base64;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoTest {
    @Autowired
    private WebApplicationContext context;

    @LocalServerPort
    private int port;

    @Test
    public void createUserAndRetrieveByUsername() throws Exception {
        // Test data
        String username = "test_user";
        String password = "test_password";
        String first_Name = "John";
        String last_Name = "Doe";

        // Create user
        String createUserJson = createJsonString(username, password, first_Name, last_Name);
        RestAssured.given()
                .port(port)
                .contentType("application/json")
                .body(createUserJson)
                .post("/v1/user")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Retrieve user by username
        String authorizationHeader = createBasicAuthHeader(username, password);
        RestAssured.given()
                .port(port)
                .header("Authorization", authorizationHeader)
                .get("v1/user/self")
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .body("username", equalTo(username))
                .body("firstName", equalTo(first_Name))
                .body("lastName", equalTo(last_Name));





    }
    public void updateuser() throws Exception {
        String username = "test_user";
        String password = "test_password";
        String first_Name = "mike";
        String last_Name = "palmer";


        String authorizationHeader = createBasicAuthHeader(username, password);
        String updateUserJson = createJsonString( first_Name, last_Name);
        RestAssured.given()
                .port(port)
                .contentType("application/json")
                .body(updateUserJson)
                .header("Authorization", authorizationHeader)
                .put("v1/user/self")
                .then()
                .statusCode(HttpStatus.OK.value());

    }
    private String createJsonString(String username, String password, String firstName, String lastName) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
                Map.of("username", username, "password", password, "first_name", firstName, "last_name", lastName)
        );
    }

    private String createJsonString(String firstName, String lastName) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
                Map.of("first_name", firstName, "last_name", lastName)
        );
    }

    private String createBasicAuthHeader(String username, String password) {
        String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encodedCredentials;
    }


}

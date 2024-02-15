//package com.csye6225.cloud.controller;
//
//import com.csye6225.cloud.model.User;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class IntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @BeforeEach
//    public void setUp() {
//        RestAssured.port = port;
//    }
//
//    @Test
//    public void testCreateGetAndUpdateAccount() {
//        // Create an account using POST
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPassword("testpassword");
//        user.setFirstName("Test");
//        user.setLastName("User");
//
//        given()
//                .contentType(ContentType.JSON)
//                .body(user)
//                .when()
//                .post("/v1/user")
//                .then()
//                .statusCode(201);
//
//        // Get the account using GET with Basic Auth
//        given()
//                .auth().preemptive().basic("testuser", "testpassword")
//                .when()
//                .get("/v1/user/self")
//                .then()
//                .statusCode(200)
//                .body("username", equalTo("testuser"))
//                .body("firstName", equalTo("Test"))
//                .body("lastName", equalTo("User"));
//
//        // Update the account using PUT
//        User updatedUser = new User();
//        updatedUser.setFirstName("UpdatedTest");
//        updatedUser.setLastName("UpdatedUser");
//
//        given()
//                .auth().preemptive().basic("testuser", "testpassword")
//                .contentType(ContentType.JSON)
//                .body(updatedUser)
//                .auth().preemptive().basic("testuser", "testpassword")
//                .when()
//                .put("/v1/user/self")
//                .then()
//                .statusCode(201);
//
//        // Get the account again using GET
//        given()
//                .auth().preemptive().basic("testuser", "testpassword")
//                .when()
//                .get("/v1/user/self")
//                .then()
//                .statusCode(200)
//                .body("username", equalTo("testuser"))
//                .body("firstName", equalTo("UpdatedTest"))
//                .body("lastName", equalTo("UpdatedUser"));
//    }
//}

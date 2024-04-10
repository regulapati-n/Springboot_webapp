package com.csye6225.cloud.controller;
import java.util.UUID;
import java.util.stream.Collectors;
import com.csye6225.cloud.constants.UserConstants;
import com.csye6225.cloud.exception.DataNotFoundException;
import com.csye6225.cloud.exception.InvalidInputException;
import com.csye6225.cloud.exception.UserAuthorizationException;
import com.csye6225.cloud.exception.UserExistException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.model.UserDto;
import com.csye6225.cloud.model.UserUpdateRequestModel;
import com.csye6225.cloud.repository.UserRepository;
import com.csye6225.cloud.service.AuthService;
import com.csye6225.cloud.service.UserService;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userrepo;

    @Autowired
    Publisher publisher;

    @GetMapping("v1/verify/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable("id") String id) throws Exception {
        try {
            if (userService.verifyUser(id)) {
                logger.info("User verification successful for ID: {}", id);
                return new ResponseEntity<>("User verification successful!", HttpStatus.OK);
            } else {
                logger.warn("Invalid verification ID: {}", id);
                return new ResponseEntity<>("Invalid verification ID", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            logger.error("Error verifying user: {}", e.getMessage());
            return new ResponseEntity<>("Error verifying user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @GetMapping(value = "v1/user/self")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String username = authService.extractUsernameFromAuthorization(authorizationHeader);
            UserDto user = userService.getUserDetails(username); // Use your repository method
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not verified");
            }
            UserDto userr = userService.getUserDetails(username);
            if (userr != null) {
                logger.info("User details retrieved successfully for user: {}", username);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                logger.warn("User not found: {}", username);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (DataNotFoundException | UserAuthorizationException e) {
            logger.warn("Data not found: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "v1/user/self")
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UserUpdateRequestModel user,
                                               Errors error) {
        try {
            String username = authService.extractUsernameFromAuthorization(authorizationHeader);
            authService.isAuthorised(username, authorizationHeader.split(" ")[1]);
            UserDto userr = userService.getUserDetails(username);
            if (!userr.isVerified()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not verified");
            }

            if (error.hasErrors()) {
                String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(","));
                throw new InvalidInputException(response);
            }
            return new ResponseEntity<>(userService.updateUserDetails(username, user), HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            // TODO Auto-generated catch block
            logger.error("Invalid input while updating user entity: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserAuthorizationException e) {
            // TODO Auto-generated catch block
            logger.error("Invalid input while updating user entity: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (DataNotFoundException e) {
            logger.error("User entity does not exist while updating: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error while updating user entity: {}", e.getMessage());
            return new ResponseEntity<>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("v1/user")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user, Errors error) {
        try {
            if (error.hasErrors()) {
                String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(","));
                throw new InvalidInputException(response);
            }
            String response = userService.createUser(user);
            publishMessageToPubSub(user.getUsername(), user.getId());
            logger.info("User entity created: {}", user);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidInputException | UserExistException e) {
            // TODO Auto-generated catch block
            logger.error("Error creating other entity: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private void publishMessageToPubSub(String username, UUID id) {
        final Logger logger = LoggerFactory.getLogger(UserController.class);
        String messageData = "{\"email\": \"" + username + "\",\"id\": \"" + id + "\"}";
        ByteString data = ByteString.copyFromUtf8(messageData);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(data)
                .build();
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        ApiFutures.addCallback(
                messageIdFuture,
                new ApiFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Failed to publish message to Pub/Sub: {}", t.getMessage());
                    }
                    @Override
                    public void onSuccess(String messageId) {
                        logger.info("Message published to Pub/Sub with ID: {}", messageId);
                    }
                },
                MoreExecutors.directExecutor()
        );
    }
}
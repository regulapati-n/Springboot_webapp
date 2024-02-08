package com.csye6225.cloud.controller;
import java.util.stream.Collectors;
import com.csye6225.cloud.constants.UserConstants;
import com.csye6225.cloud.exception.DataNotFoundException;
import com.csye6225.cloud.exception.InvalidInputException;
import com.csye6225.cloud.exception.UserAuthorizationException;
import com.csye6225.cloud.exception.UserExistException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.model.UserDto;
import com.csye6225.cloud.model.UserUpdateRequestModel;
import com.csye6225.cloud.service.AuthService;
import com.csye6225.cloud.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;


    @GetMapping(value = "v1/user/self")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String username = authService.extractUsernameFromAuthorization(authorizationHeader);
            UserDto user = userService.getUserDetails(username);
            if (user != null){
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }


        } catch (DataNotFoundException | UserAuthorizationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "v1/user/self")
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody UserUpdateRequestModel user,
                                               HttpServletRequest request, Errors error){
        try {
            String username = authService.extractUsernameFromAuthorization(authorizationHeader); // Extracting username from Authorization header
            authService.isAuthorised(username, authorizationHeader.split(" ")[1]);
            if(error.hasErrors()) {
                String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(","));
                throw new InvalidInputException(response);
            }
            return new ResponseEntity<>(userService.updateUserDetails(username, user), HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserAuthorizationException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        catch (DataNotFoundException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch(Exception e) {
            return new ResponseEntity<>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("v1/user")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user, Errors error){
        try {
            if(error.hasErrors()) {
                String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(","));
                throw new InvalidInputException(response);
            }
            return new ResponseEntity<String>( userService.createUser(user),HttpStatus.CREATED);
        } catch (InvalidInputException | UserExistException e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(Exception e) {
            return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.csye6225.cloud.service;

import com.csye6225.cloud.exception.DataNotFoundException;
import com.csye6225.cloud.exception.UserAuthorizationException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthService {
    @Autowired
    UserRepository userrepo;

    public BCryptPasswordEncoder PassEncoder() {
        return new BCryptPasswordEncoder();
    }


    public User getUserDetailsAuth(String username) throws DataNotFoundException{
        User user = userrepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        throw new DataNotFoundException("User Not Found");
    }

    public boolean isAuthorised(String username,String tokenEnc) throws DataNotFoundException, UserAuthorizationException {

        User user = getUserDetailsAuth(username);
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " " + decodedStr);
        if (!((user.getUsername().equals(userName)) && (PassEncoder().matches(passWord, user.getPassword())))) {
            throw new UserAuthorizationException("Forbidden to access");
        }
        return true;
    }


    public String extractUsernameFromAuthorization(String authorizationHeader) throws DataNotFoundException, UserAuthorizationException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new UserAuthorizationException("Invalid Authorization header");
        }

        String base64Credentials = authorizationHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] split = credentials.split(":", 2);
        if (split.length != 2) {
            throw new UserAuthorizationException("Invalid credentials format");
        }
        return split[0]; // Return the username
    }

//    public User getUserDetailsIfAuthorized(String username, String tokenEnc) throws DataNotFoundException, UserAuthorizationException {
//        isAuthorised(username, tokenEnc);
//        return getUserDetailsAuth(username);
//    }
}

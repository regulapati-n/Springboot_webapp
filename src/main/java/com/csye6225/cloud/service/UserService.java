package com.csye6225.cloud.service;

import com.csye6225.cloud.exception.DataNotFoundException;
import com.csye6225.cloud.exception.UserExistException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.model.UserDto;
import com.csye6225.cloud.model.UserUpdateRequestModel;
import com.csye6225.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userrepo;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public String createUser(User user) throws UserExistException {
        User userDto = userrepo.findByUsername(user.getUsername());
        if (userDto == null) {
            user.setPassword(encoder().encode(user.getPassword()));
            user.setFirstName("john");
            user.setLastName("doe");
            userrepo.save(user);
            return "Created User";
        }
        throw new UserExistException("User Exists Already");
    }

    public UserDto getUserDetails(String username) throws DataNotFoundException {
        User user = userrepo.findByUsername(username);
        if (user != null) {
            return UserDto.getUserDto(user);
        }

        throw new DataNotFoundException("User Not Found");
    }

    public String updateUserDetails(String username, UserUpdateRequestModel user) throws DataNotFoundException {
        User existingUser = userrepo.findByUsername(username);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPassword(encoder().encode(user.getPassword()));
            userrepo.save(existingUser);
            return "Updated User Details Successfully";
        }
        throw new DataNotFoundException("User Not Found");
    }

}

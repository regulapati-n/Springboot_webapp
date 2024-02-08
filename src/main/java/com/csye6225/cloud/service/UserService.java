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

    public User getUserByUsername(String username) {
        return userrepo.findByUsername(username);
    }

    public String createUser(User user) throws UserExistException {
        User userDto = userrepo.findByUsername(user.getUsername());
        if (userDto == null) {
            user.setPassword(encoder().encode(user.getPassword()));
            userrepo.save(user);
            return "Created User";
        }
        throw new UserExistException("User Exists Already");
    }

    public UserDto getUserDetails(String username) throws DataNotFoundException {
        Optional<User> user = userrepo.findById(username);
        if (user.isPresent()) {
            UserDto dto=UserDto.getUserDto(user.get());
            return dto;
        }
        throw new DataNotFoundException("User Not Found");
    }

    public String updateUserDetails(String username, UserUpdateRequestModel user) throws DataNotFoundException {
        Optional<User> userObj = userrepo.findById(username);
        if (userObj.isPresent()) {
            User dto=userObj.get();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPassword(encoder().encode(user.getPassword()));
            userrepo.save(dto);
            return "Updated User Details Successfully";

        }
        throw new DataNotFoundException("User Not Found");
    }

}

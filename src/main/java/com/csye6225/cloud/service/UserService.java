package com.csye6225.cloud.service;
import com.csye6225.cloud.exception.DataNotFoundException;
import com.csye6225.cloud.exception.UserExistException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.model.UserDto;
import com.csye6225.cloud.model.UserUpdateRequestModel;
import com.csye6225.cloud.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService {
    @Autowired
    UserRepository userrepo;
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final long VERIFICATION_TOKEN_EXPIRY_MINUTES = 2;
    public String createUser(User user) throws UserExistException {
        User userDto = userrepo.findByUsername(user.getUsername());
        if (userDto == null) {
            user.setPassword(encoder().encode(user.getPassword()));
            user.setFirstName(user.getFirstName());
            user.setLastName(user.getLastName());
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
    public boolean verifyUser(String id) throws Exception {
        try {
            User user = userrepo.findById(UUID.fromString(id));
            if (user != null) {
                if (isVerificationTokenValid(user)) {
                    user.setVerified(true);
                    userrepo.save(user);
                    return true;
                } else {
                    logger.warn("Verification ID expired for user: {}", user.getUsername());
                    return false;
                }
            } else {
                logger.warn("User not found with verification ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error verifying user: {}", e.getMessage());
            throw new Exception("Error during user verification"); //
        }
    }
    private boolean isVerificationTokenValid(User user) {
        LocalDateTime tokenCreatedAt = user.getAccountCreated();
        if (tokenCreatedAt == null) {
            logger.warn("User verification token creation time not set");
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = Duration.between(tokenCreatedAt, now).toMinutes();
        return minutesElapsed <= VERIFICATION_TOKEN_EXPIRY_MINUTES;
    }
}
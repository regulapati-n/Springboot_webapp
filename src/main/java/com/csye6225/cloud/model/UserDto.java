package com.csye6225.cloud.model;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private LocalDateTime accountCreated;
    private LocalDateTime accountUpdated;
    private boolean verified;

    public static UserDto getUserDto(User user) {
        UserDto dto=new UserDto();
        dto.setAccountCreated(user.getAccountCreated());
        dto.setAccountUpdated(user.getAccountUpdated());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setVerified(user.isVerified());
        return dto;
    }
}

package com.prpo.chat.notification.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private Profile profile;
}

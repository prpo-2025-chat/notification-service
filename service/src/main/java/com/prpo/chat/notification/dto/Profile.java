package com.prpo.chat.notification.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Profile {
    private String avatarUrl;
    private String bio;
    private Date birthdate;
}

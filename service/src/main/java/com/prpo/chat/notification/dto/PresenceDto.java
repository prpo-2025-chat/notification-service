package com.prpo.chat.notification.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PresenceDto {
    private String userId;
    private PresenceStatus status;
    private Date lastSeen;
}

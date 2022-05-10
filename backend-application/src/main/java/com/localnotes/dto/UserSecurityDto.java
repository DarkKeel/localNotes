package com.localnotes.dto;

import lombok.Data;

@Data
public class UserSecurityDto {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

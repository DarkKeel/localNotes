package com.localnotes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSecurityDto {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

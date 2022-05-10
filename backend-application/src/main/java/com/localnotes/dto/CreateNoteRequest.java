package com.localnotes.dto;

import lombok.Data;

@Data
public class CreateNoteRequest {

    private String name;
    private String description;
    private CategoryDto category;
    private Boolean favorite;
    private String userId;
}

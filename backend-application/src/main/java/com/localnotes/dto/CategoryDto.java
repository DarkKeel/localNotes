package com.localnotes.dto;

import com.localnotes.entity.Status;
import lombok.Data;

@Data
public class CategoryDto {

    private String id;
    private String name;
    private String description;
    private String userId;
    private Status status;
}

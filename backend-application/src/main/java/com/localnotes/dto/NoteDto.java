package com.localnotes.dto;

import com.localnotes.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDto {

    private String id;
    private String name;
    private String description;
    private CategoryDto category;
    private Boolean favorite;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Status status;
    private String userId;
}

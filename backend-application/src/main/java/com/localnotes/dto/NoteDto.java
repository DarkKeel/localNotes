package com.localnotes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localnotes.entity.Status;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDto {

    private String id;
    private String name;
    private String description;
    private CategoryDto category;
    private Boolean favorite;
    private Date created;
    private Date updated;
    private Status status;
}

package com.localnotes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private String userId;
}

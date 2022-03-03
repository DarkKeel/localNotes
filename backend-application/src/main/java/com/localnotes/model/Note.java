package com.localnotes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notes")
@Data
public class Note extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cat_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "favorite")
    private boolean isFavorite;
}

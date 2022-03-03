package com.localnotes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localnotes.entity.Category;
import com.localnotes.entity.Note;
import com.localnotes.entity.Status;
import com.localnotes.repository.NoteRepository;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDto {

    @Autowired
    private NoteRepository noteRepository;

    private String id;
    private String name;
    private String description;
    private Category category;
    private boolean isFavorite;
    private Date created;
    private Date updated;
    private Status status;


    public Note toNote() {
        Note note = noteRepository.findByPublicId(id).orElse(null);
        if (note == null) {
            note = new Note();
        }
        note.setPublicId(id);
        note.setName(name);
        note.setDescription(description);
        note.setCategory(category);
        note.setFavorite(isFavorite);
        note.setCreated(created);
        note.setUpdated(updated);
        note.setStatus(status);

        return note;
    }

    public static NoteDto fromNote(Note note) {
        NoteDto noteDto = new NoteDto();
        noteDto.id = note.getPublicId();
        noteDto.name = note.getName();
        noteDto.description = note.getDescription();
        noteDto.category = note.getCategory();
        noteDto.created = note.getCreated();
        noteDto.updated = note.getUpdated();
        noteDto.isFavorite = note.isFavorite();
        noteDto.status = note.getStatus();

        return noteDto;
    }
}

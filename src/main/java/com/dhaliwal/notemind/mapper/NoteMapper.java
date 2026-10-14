package com.dhaliwal.notemind.mapper;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.Note;
import org.mapstruct.Mapper;

public interface NoteMapper {
    NoteDto toDto(Note note);
    Note toEntity(NoteDto dto);
}

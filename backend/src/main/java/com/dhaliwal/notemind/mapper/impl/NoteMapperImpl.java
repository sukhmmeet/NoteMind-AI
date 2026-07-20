package com.dhaliwal.notemind.mapper.impl;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteMapperImpl implements NoteMapper {

    private final TagMapper tagMapper;

    @Override
    public NoteDto toDto(Note note) {
        Long folderId = null;
        if(note.getFolder() != null){
            folderId = note.getFolder().getId();
        }
        return NoteDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .summary(note.getSummary())
                .imageUrl(note.getImageUrl())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .summaryType(note.getSummaryType())
                .tags(note.getTags().stream().map(tagMapper::toDto).toList())
                .folderId(folderId)
                .build();
    }

    @Override
    public Note toEntity(NoteDto dto) {
        return null; // not implemented yet
    }
}

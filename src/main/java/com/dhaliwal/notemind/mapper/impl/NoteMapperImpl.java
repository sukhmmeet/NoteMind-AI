package com.dhaliwal.notemind.mapper.impl;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.dto.TagDto;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        List<TagDto> tags = new ArrayList<>();
        if(note.getTags() != null){
            tags = note.getTags().stream().map(tagMapper::toDto).toList();
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
                .tags(tags)
                .folderId(folderId)
                .build();
    }

    @Override
    public Note toEntity(NoteDto dto) {
        return null; // not implemented yet
    }
}

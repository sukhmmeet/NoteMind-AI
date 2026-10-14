package com.dhaliwal.notemind.mapper.impl;

import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.mapper.FolderMapper;
import com.dhaliwal.notemind.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FolderMapperImpl implements FolderMapper {

    private final NoteMapper noteMapper;

    @Override
    public FolderDto toDto(Folder folder) {
        return FolderDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .notes(
                        folder.getNotes().stream().map(noteMapper::toDto).toList()
                )
                .build();
    }

    @Override
    public Folder toEntity(FolderDto folderDto) {
        return Folder.builder()
                .name(folderDto.getName())
                .build();
    }

    @Override
    public FolderDtoWithoutNotes toDtoWithoutNotes(Folder folder) {
        return FolderDtoWithoutNotes.builder()
                .id(folder.getId())
                .name(folder.getName())
                .build();
    }
}

package com.dhaliwal.notemind.mapper;

import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.entity.Folder;

public interface FolderMapper {
    FolderDto toDto(Folder folder);
    Folder toEntity(FolderDto folderDto);
    FolderDtoWithoutNotes  toDtoWithoutNotes(Folder folder);
}

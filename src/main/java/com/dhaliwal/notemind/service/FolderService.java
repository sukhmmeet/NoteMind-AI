package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FolderService {
    FolderDtoWithoutNotes createFolder(FolderRequestDto folderRequestDto);
    List<FolderDtoWithoutNotes> GetAllFoldersWithoutNotes();
    List<FolderDto>  GetAllFoldersWithNotes();
    FolderDtoWithoutNotes updateFolder(Long id, FolderRequestDto folderRequestDto);
    void deleteFolder(Long folderId);
    FolderDtoWithoutNotes getFolderById(Long id);
}

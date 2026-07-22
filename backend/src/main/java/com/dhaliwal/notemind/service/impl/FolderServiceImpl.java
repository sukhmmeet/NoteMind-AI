package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.exception.*;
import com.dhaliwal.notemind.mapper.FolderMapper;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.FolderRepository;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.repository.UserRepository;
import com.dhaliwal.notemind.security.util.SecurityUtils;
import com.dhaliwal.notemind.service.FolderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final NoteMapper  noteMapper;
    private final FolderMapper folderMapper;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public FolderDtoWithoutNotes createFolder(FolderRequestDto  folderRequestDto) {
        if (folderRequestDto.getName().isEmpty()) {
            throw new InvalidFolderException("Folder name cannot be empty");
        }
        if (folderRepository.findByName(folderRequestDto.getName()).isPresent()){
            throw new FolderAlreadyExistsException("Folder already exists");
        }

        Long userId = securityUtils.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Folder folder = Folder.builder()
                .name(folderRequestDto.getName())
                .user(user)
                .build();
        folderRepository.save(folder);
        return folderMapper.toDtoWithoutNotes(folder);
    }

    @Override
    public List<FolderDtoWithoutNotes> GetAllFoldersWithoutNotes() {
        Long userId =  securityUtils.getUserId();

        List<Folder> folders = folderRepository.findAllByUserId(userId);
        return folders.stream().map(folderMapper::toDtoWithoutNotes).toList();
    }

    @Override
    public List<FolderDto>  GetAllFoldersWithNotes() {
        Long userId =  securityUtils.getUserId();

        List<Folder> folders = folderRepository.findAllByUserId(userId);
        List<Note> notesWithoutFolder =
                noteRepository.findNotesWithoutFolderByUserId(userId);
        List<FolderDto> folderDtos = new java.util.ArrayList<>(folders.stream().map(folderMapper::toDto).toList());
        folderDtos.add(
                FolderDto.builder()
                        .id(null)
                        .name("NotInFolder")
                        .notes(notesWithoutFolder.stream().map(noteMapper::toDto).toList())
                        .build()
        );
        return folderDtos;
    }

    @Override
    @Transactional
    public FolderDtoWithoutNotes updateFolder(Long id, FolderRequestDto folderRequestDto) {
        if (folderRequestDto.getName().isEmpty()) {
            throw new InvalidFolderException("Folder name cannot be empty");
        }
        Folder folder = folderRepository.findById(id).orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        Long userId = securityUtils.getUserId();
        if (!folder.getUser().getId().equals(userId)) {
            throw new InvalidCredentialsException("User not authorized");
        }

        folder.setName(folderRequestDto.getName());
        folderRepository.save(folder);
        return folderMapper.toDtoWithoutNotes(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        Long userId = securityUtils.getUserId();
        if (!folder.getUser().getId().equals(userId)) {
            throw new InvalidCredentialsException("User not authorized");
        }

        folderRepository.delete(folder);
    }

    @Override
    public FolderDtoWithoutNotes getFolderById(Long id) {
        Folder folder = folderRepository.findById(id).orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        Long userId = securityUtils.getUserId();
        if (!folder.getUser().getId().equals(userId)) {
            throw new InvalidCredentialsException("User not authorized");
        }

        return folderMapper.toDtoWithoutNotes(folder);
    }

}

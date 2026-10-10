package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.type.SummaryType;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface NotesService {

    NoteDto createNote(NoteDto noteDto, MultipartFile image);

    List<NoteDto> getAllNotes();

    Page<NoteDto> getAllNotes(Pageable pageable);

    NoteDto getNoteById(Long id);

    NoteDto updateNote(Long id, NoteDto noteDto, MultipartFile image);

    void deleteNote(Long id);

    List<NoteDto> searchNotes(String searchTerm);

    NoteDto moveToFolder(Long noteId, Long folderId);

    NoteDto removeFromFolder(Long noteId);

    NoteDto refreshSummary(Long noteId, SummaryType summaryType);

}


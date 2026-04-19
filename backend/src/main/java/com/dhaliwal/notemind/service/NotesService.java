package com.dhaliwal.notemind.service;

import com.dhaliwal.notemind.dto.NoteDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotesService {

    NoteDto createNote(NoteDto noteDto);

    List<NoteDto> getAllNotes();

    NoteDto getNoteById(Long id);

    NoteDto updateNote(Long id, NoteDto noteDto);

    void deleteNote(Long id);
}


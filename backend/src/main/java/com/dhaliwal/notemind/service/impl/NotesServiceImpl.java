package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.dto.TagDto;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.Tag;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.repository.TagRepository;
import com.dhaliwal.notemind.service.NotesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotesServiceImpl implements NotesService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final TagRepository tagRepository;

    public NotesServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.tagRepository = tagRepository;
    }
    @Override
    public NoteDto createNote(NoteDto noteDto) {

        Note note = new Note();

        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setSummary(noteDto.getSummary()); // it will goes under AI

        Set<Tag> tagEntities = new HashSet<>();

        for (TagDto tagDto : noteDto.getTags()) {

            Tag tag = tagRepository.findByName(tagDto.getName())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagDto.getName());
                        return newTag;
                    });

            tagEntities.add(tag);
        }

        note.setTags(tagEntities);

        Note saved = noteRepository.save(note);

        return noteMapper.toDto(saved);
    }

    @Override
    public List<NoteDto> getAllNotes() {

        List<Note> notes = noteRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return notes.stream()
                .map(noteMapper::toDto)
                .toList();
    }

    @Override
    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not find note of this id:" + id));
        return noteMapper.toDto(note);
    }

    @Override
    public NoteDto updateNote(Long id, NoteDto noteDto) {

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        existingNote.setTitle(noteDto.getTitle());
        existingNote.setContent(noteDto.getContent());
        existingNote.setSummary(noteDto.getSummary());

        Note updated = noteRepository.save(existingNote);

        return noteMapper.toDto(updated);
    }

    @Override
    public void deleteNote(Long id) {
        if(!noteRepository.existsById(id)){
            throw new IllegalArgumentException("Note not found");
        }
        noteRepository.deleteById(id);
    }
}

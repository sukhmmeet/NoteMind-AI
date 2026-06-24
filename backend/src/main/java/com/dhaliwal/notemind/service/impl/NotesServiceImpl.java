package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.Tag;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.repository.TagRepository;
import com.dhaliwal.notemind.security.UserRepository;
import com.dhaliwal.notemind.security.util.SecurityUtils;
import com.dhaliwal.notemind.service.AIService;
import com.dhaliwal.notemind.service.ImageManagerService;
import com.dhaliwal.notemind.service.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final TagRepository tagRepository;
    private final AIService aiService;
    private final ImageManagerService imageManagerService;
    private final UserRepository  userRepository;
    private final SecurityUtils  securityUtils;

    @Override
    public NoteDto createNote(NoteDto noteDto, MultipartFile image) {
        User user = userRepository.findById(securityUtils.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = imageManagerService.uploadAndGetUrl(image);
        }

        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setImageUrl(imageUrl);
        AINoteResponse aiResponse = aiService.getAIResponse(note.getTitle(), note.getContent(), imageUrl);
        note.setSummary(aiResponse.getSummary());

        Set<Tag> tagEntities = new HashSet<>();

        for (String tagName : aiResponse.getTags()) {

            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
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

        List<Note> notes = noteRepository.findAllByUserId(
                securityUtils.getUserId()
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
    public NoteDto updateNote(Long id, NoteDto noteDto, MultipartFile image) {

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        existingNote.setTitle(noteDto.getTitle());
        existingNote.setContent(noteDto.getContent());
        if (image != null & !image.isEmpty()){
            String url = imageManagerService.uploadAndGetUrl(image);
            existingNote.setImageUrl(url);
        }else{
            existingNote.setImageUrl(noteDto.getImageUrl());
        }
        AINoteResponse aiResponse = aiService.getAIResponse(noteDto.getTitle(), noteDto.getContent(),noteDto.getImageUrl());
        existingNote.setSummary(aiResponse.getSummary());
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

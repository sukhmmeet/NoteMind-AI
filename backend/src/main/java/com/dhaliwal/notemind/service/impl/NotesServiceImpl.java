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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

        Long userId = securityUtils.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageManagerService.uploadAndGetUrl(image);
        }

        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setImageUrl(imageUrl);

        Note savedNote = noteRepository.save(note);

        try {
            AINoteResponse aiResponse =
                    aiService.getAIResponse(savedNote.getTitle(),
                            savedNote.getContent(),
                            imageUrl);

            if (aiResponse != null) {

                savedNote.setSummary(aiResponse.getSummary());

                // 4. Optimize tag fetching (avoid N queries)
                Set<String> tagNames = new HashSet<>(aiResponse.getTags());

                List<Tag> existingTags = tagRepository.findByNameIn(tagNames);

                Map<String, Tag> tagMap = existingTags.stream()
                        .collect(Collectors.toMap(Tag::getName, t -> t));

                Set<Tag> finalTags = new HashSet<>();

                for (String tagName : tagNames) {

                    Tag tag = tagMap.get(tagName);

                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag = tagRepository.save(tag); // ensure persistence
                    }

                    finalTags.add(tag);
                }

                savedNote.setTags(finalTags);
            }
        } catch (Exception e) {
            log.error("AI enrichment failed for noteId={}", savedNote.getId(), e);

            savedNote.setSummary("Summary is unavailable at this moment");
        }

        Note finalNote = noteRepository.save(savedNote);

        return noteMapper.toDto(finalNote);
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

package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.Tag;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.entity.type.SummaryType;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.FolderRepository;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesServiceImpl implements NotesService {
    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final TagRepository tagRepository;
    private final AIService aiService;
    private final ImageManagerService imageManagerService;
    private final UserRepository  userRepository;
    private final SecurityUtils  securityUtils;
    private final String Cache_name = "notes";

    @Override
    @CachePut(cacheNames = Cache_name, key = "#result.id")
    public NoteDto createNote(NoteDto noteDto, MultipartFile image) {
        if(noteDto.getSummaryType() == null){
            noteDto.setSummaryType(SummaryType.SHORT);
        }

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
                            imageUrl,
                            noteDto.getSummaryType()
                    );

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
    @Cacheable(cacheNames = Cache_name, key = "#id")
    public NoteDto getNoteById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not find note of this id:" + id));
        return noteMapper.toDto(note);
    }

    @Override
    public NoteDto updateNote(Long id, NoteDto noteDto, MultipartFile image) {
        if (noteDto.getSummaryType() == null){
            noteDto.setSummaryType(SummaryType.SHORT);
        }

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if(!existingNote.getTitle().equals(noteDto.getTitle())){
            existingNote.setTitle(noteDto.getTitle());
        }
        if(!existingNote.getContent().equals(noteDto.getContent())){
            existingNote.setContent(noteDto.getContent());
        }
        if (image != null && !image.isEmpty()){
            String url = imageManagerService.uploadAndGetUrl(image);
            existingNote.setImageUrl(url);
        }else{
            existingNote.setImageUrl(noteDto.getImageUrl());
        }
        AINoteResponse aiResponse = aiService.getAIResponse(noteDto.getTitle(), noteDto.getContent(),noteDto.getImageUrl(), noteDto.getSummaryType());
        existingNote.setSummary(aiResponse.getSummary());
        Note updated = noteRepository.save(existingNote);

        return noteMapper.toDto(updated);
    }

    @Override
    @CacheEvict(cacheNames =  Cache_name, key = "#id")
    public void deleteNote(Long id) {
        if(!noteRepository.existsById(id)){
            throw new IllegalArgumentException("Note not found");
        }
        noteRepository.deleteById(id);
    }

    @Override
    public List<NoteDto> searchNotes(String searchTerm) {
        Long userId = securityUtils.getUserId();
        if(userId == null){
            throw new IllegalArgumentException("User not login");
        }
        if(Objects.equals(searchTerm, "") ||  searchTerm == null){
            throw new IllegalArgumentException("Search term is empty");
        }
        List<Note> notes = noteRepository.searchNotes(userId, searchTerm);
        return notes.stream().map(noteMapper::toDto).toList();
    }

    @Override
    @Transactional
    public NoteDto moveToFolder(Long noteId, Long folderId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        User user =  userRepository.findById(securityUtils.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(!note.getUser().equals(user) || !folder.getUser().equals(user)){
            throw new IllegalArgumentException("Unauthorized move");
        }
        note.setFolder(folder);
        noteRepository.save(note);
        return noteMapper.toDto(note);
    }

    @Override
    @Transactional
    public NoteDto removeFromFolder(Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        User user =  userRepository.findById(securityUtils.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(!note.getUser().equals(user)){
            throw new IllegalArgumentException("Unauthorized move");
        }
        note.setFolder(null);
        return noteMapper.toDto(noteRepository.save(note));
    }
}

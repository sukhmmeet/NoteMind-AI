package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.AINoteResponse;
import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.Tag;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.entity.type.SummaryType;
import com.dhaliwal.notemind.exception.*;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.FolderRepository;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.repository.TagRepository;
import com.dhaliwal.notemind.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private static final String CACHE_NAME = "notes";
    private static final String SUMMARY_UNAVAILABLE =
            "Summary is unavailable at this moment";

    /* ==========================================================
                        Helper Methods
       ========================================================== */

    private User getCurrentUser() {
        Long userId = securityUtils.getUserId();

        if (userId == null) {
            throw new UserNotLoggedInException("User not logged in");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private boolean validateNoteDto(NoteDto noteDto) {
        return noteDto.getTitle() != null
                && !noteDto.getTitle().isBlank()
                && noteDto.getContent() != null
                && !noteDto.getContent().isBlank();
    }

    private Note enrichNote(Note note, SummaryType summaryType) {

        try {

            AINoteResponse response =
                    aiService.getAIResponse(
                            note.getTitle(),
                            note.getContent(),
                            note.getImageUrl(),
                            summaryType
                    );

            if (response == null) {
                note.setSummary(SUMMARY_UNAVAILABLE);
                return note;
            }

            note.setSummary(
                    Optional.ofNullable(response.getSummary())
                            .orElse(SUMMARY_UNAVAILABLE)
            );
            note.setSummaryType(summaryType);

            Set<String> tagNames =
                    Optional.ofNullable(response.getTags())
                            .map(HashSet::new)
                            .orElse(new HashSet<>());

            List<Tag> existingTags =
                    tagRepository.findByNameIn(tagNames);

            Map<String, Tag> tagMap =
                    existingTags.stream()
                            .collect(Collectors.toMap(Tag::getName, t -> t));

            Set<Tag> finalTags = new HashSet<>();

            for (String tagName : tagNames) {

                Tag tag = tagMap.get(tagName);

                if (tag == null) {
                    tag = tagRepository.save(
                            Tag.builder()
                                    .name(tagName)
                                    .build()
                    );
                }

                finalTags.add(tag);
            }

            note.setTags(finalTags);

            return note;

        } catch (Exception e) {

            log.error(
                    "AI enrichment failed for note {}",
                    note.getId(),
                    e
            );

            note.setSummary(SUMMARY_UNAVAILABLE);

            return note;
        }
    }

    /* ==========================================================
                        Main Logic Methods
       ========================================================== */

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME, key = "#result.id")
    public NoteDto createNote(NoteDto noteDto, MultipartFile image) {
        if(!validateNoteDto(noteDto)) {
            throw new InvalidNoteException(
                    "Title and content are required."
            );
        }

        User user = getCurrentUser();

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageManagerService.uploadAndGetUrl(image);
        }
        if(noteDto.getSummaryType() == null){
            noteDto.setSummaryType(SummaryType.SHORT);
        }

        Note note = new Note();
        note.setUser(user);
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setImageUrl(imageUrl);
        note.setSummaryType(noteDto.getSummaryType());

        Note saved = noteRepository.save(note);


        saved = noteRepository.save(enrichNote(saved, noteDto.getSummaryType()));

        return noteMapper.toDto(saved);
    }

    @Override
    public List<NoteDto> getAllNotes() {
        User user = getCurrentUser();

        return noteRepository.findAllByUserId(user.getId())
                .stream()
                .map(noteMapper::toDto)
                .toList();
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public NoteDto getNoteById(Long id) {
        User user = getCurrentUser();
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException("Not find note of this id:" + id));
        if(!Objects.equals(note.getUser().getId(), user.getId())){
            throw new InvalidCredentialsException("Access denied.");
        }
        return noteMapper.toDto(note);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#id")
    public NoteDto updateNote(Long id, NoteDto noteDto, MultipartFile image) {
        if(!validateNoteDto(noteDto)) {
            throw new InvalidNoteException(
                    "Title and content are required."
            );
        }

        User user = getCurrentUser();

        if (noteDto.getSummaryType() == null){
            noteDto.setSummaryType(SummaryType.SHORT);
        }

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
        if(!Objects.equals(existingNote.getUser().getId(), user.getId())){
            throw new InvalidCredentialsException("Access denied.");
        }
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
        Note finalNote = noteRepository.save(enrichNote(existingNote, noteDto.getSummaryType()));

        return noteMapper.toDto(finalNote);
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, key = "#id")
    public void deleteNote(Long id) {
        User user = getCurrentUser();
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException("Not find note of this id:" + id));
        if(!Objects.equals(note.getUser().getId(), user.getId())){
            throw new InvalidCredentialsException("Access denied.");
        }
        noteRepository.delete(note);
    }

    @Override
    public List<NoteDto> searchNotes(String searchTerm) {
        Long userId = securityUtils.getUserId();
        if(userId == null){
            throw new UserNotLoggedInException("User not login");
        }
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new SearchTermIsEmptyException("Search term is empty");
        }
        List<Note> notes = noteRepository.searchNotes(userId, searchTerm);
        return notes.stream().map(noteMapper::toDto).toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#noteId")
    public NoteDto moveToFolder(Long noteId, Long folderId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note not found"));
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new FolderNotFoundException("Folder not found"));
        User user =  getCurrentUser();
        if(!note.getUser().equals(user) || !folder.getUser().equals(user)){
            throw new InvalidCredentialsException("Unauthorized move");
        }
        if (Objects.equals(note.getFolder(), folder)) {
            return noteMapper.toDto(note);
        }
        note.setFolder(folder);
        
        Note updated = noteRepository.save(note);

        return noteMapper.toDto(updated);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#noteId")
    public NoteDto removeFromFolder(Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note not found"));
        User user =  getCurrentUser();
        if(!note.getUser().equals(user)){
            throw new InvalidCredentialsException("Unauthorized move");
        }
        note.setFolder(null);
        return noteMapper.toDto(noteRepository.save(note));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#noteId")
    public NoteDto refreshSummary(Long noteId, SummaryType summaryType) {
        User user = getCurrentUser();
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note not found"));
        if(!Objects.equals(note.getUser().getId(), user.getId())){
            throw new InvalidCredentialsException("Access denied.");
        }
        if (summaryType == null) {
            summaryType = SummaryType.SHORT;
        }
        note.setSummaryType(summaryType);
        Note updated = noteRepository.save(enrichNote(note, summaryType));

        return noteMapper.toDto(updated);
    }
}

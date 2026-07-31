package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.flashCard.ai.AIFlashCardResponse;
import com.dhaliwal.notemind.dto.flashCard.request.FlashCardRequestDto;
import com.dhaliwal.notemind.dto.flashCard.response.FlashCardDto;
import com.dhaliwal.notemind.entity.FlashCard;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.exception.FlashCardNotFoundException;
import com.dhaliwal.notemind.exception.InvalidCredentialsException;
import com.dhaliwal.notemind.exception.NoteNotFoundException;
import com.dhaliwal.notemind.mapper.FlashCardMapper;
import com.dhaliwal.notemind.repository.FlashCardRepository;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.security.CurrentUserProvider;
import com.dhaliwal.notemind.service.AIService;
import com.dhaliwal.notemind.service.FlashCardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class FlashCardServiceImpl implements FlashCardService {

    private final FlashCardRepository flashCardRepository;
    private final CurrentUserProvider currentUserProvider;
    private final NoteRepository noteRepository;
    private final AIService aiService;
    private final FlashCardMapper flashCardMapper;

    @Override
    @Transactional
    public List<FlashCardDto> generateFlashCards(Long noteId, int count) {
        User user = currentUserProvider.getCurrentUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!user.getId().equals(note.getUser().getId())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        AIFlashCardResponse response = aiService.getAIFlashCardResponse(
                note.getTitle(),
                note.getContent(),
                note.getImageUrl(),
                count
        );

        List<FlashCard> flashCards = response.getFlashcards().stream()
                .map(flashCardMapper::toEntity)
                .peek(flashCard -> flashCard.setNote(note))
                .toList();

        flashCards = flashCardRepository.saveAll(flashCards);

        note.getFlashCards().addAll(flashCards);
        noteRepository.save(note);

        return flashCards.stream()
                .map(flashCardMapper::toDto)
                .toList();
    }

    @Override
    public List<FlashCardDto> getFlashCardsByNote(Long noteId) {
        User user = currentUserProvider.getCurrentUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!user.getId().equals(note.getUser().getId())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return note.getFlashCards().stream()
                .map(flashCardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public FlashCardDto getFlashCard(Long flashCardId) {
        User user = currentUserProvider.getCurrentUser();
        FlashCard flashCard = flashCardRepository.findById(flashCardId)
                .orElseThrow(() -> new FlashCardNotFoundException("FlashCard not found"));
        if(!Objects.equals(user.getId(), flashCard.getNote().getId())){
            throw new InvalidCredentialsException("Unauthorized access");
        }
        return flashCardMapper.toDto(flashCard);
    }

    @Override
    @Transactional
    public FlashCardDto createFlashCard(Long noteId, FlashCardRequestDto request) {
        User user = currentUserProvider.getCurrentUser();
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
        if(!Objects.equals(user.getId(), note.getUser().getId())){
            throw new InvalidCredentialsException("Unauthorized access");
        }
        assert(request.getQuestion() != null);
        assert(request.getAnswer() != null);
        FlashCard flashCard = flashCardRepository.save(
                FlashCard.builder()
                        .question(request.getQuestion())
                        .answer(request.getAnswer())
                        .note(note)
                        .build()
        );
        note.getFlashCards().add(flashCard);
        noteRepository.save(note);
        return flashCardMapper.toDto(flashCard);
    }

    @Override
    @Transactional
    public FlashCardDto updateFlashCard(Long flashCardId, FlashCardRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        FlashCard flashCard = flashCardRepository.findById(flashCardId)
                .orElseThrow(() -> new FlashCardNotFoundException("FlashCard not found"));

        if (!Objects.equals(user.getId(), flashCard.getNote().getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized access");
        }

        if (request.getQuestion() != null) {
            flashCard.setQuestion(request.getQuestion());
        }

        if (request.getAnswer() != null) {
            flashCard.setAnswer(request.getAnswer());
        }

        flashCard = flashCardRepository.save(flashCard);

        return flashCardMapper.toDto(flashCard);
    }

    @Override
    @Transactional
    public void deleteFlashCard(Long flashCardId) {
        User user = currentUserProvider.getCurrentUser();

        FlashCard flashCard = flashCardRepository.findById(flashCardId)
                .orElseThrow(() -> new FlashCardNotFoundException("FlashCard not found"));

        if (!Objects.equals(user.getId(), flashCard.getNote().getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized access");
        }

        flashCardRepository.delete(flashCard);
    }

    @Override
    @Transactional
    public void clearAllFlashCardsOfNote(Long noteId) {
        User user = currentUserProvider.getCurrentUser();
        Note note =  noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!Objects.equals(user.getId(), note.getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized access");
        }

        note.setFlashCards(null);
        noteRepository.save(note);
    }

    @Override
    @Transactional
    public List<FlashCardDto> regenerateFlashCards(Long noteId, int count) {
        User user = currentUserProvider.getCurrentUser();
        Note note =  noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!Objects.equals(user.getId(), note.getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized access");
        }

        flashCardRepository.deleteAll(note.getFlashCards());
        note.setFlashCards(null);

        AIFlashCardResponse response = aiService.getAIFlashCardResponse(
                note.getTitle(),
                note.getContent(),
                note.getImageUrl(),
                count
        );

        List<FlashCard> flashCards = response.getFlashcards().stream()
                .map(flashCardMapper::toEntity)
                .peek(flashCard -> flashCard.setNote(note))
                .toList();

        flashCards = flashCardRepository.saveAll(flashCards);

        note.setFlashCards(flashCards);
        noteRepository.save(note);

        return flashCards.stream()
                .map(flashCardMapper::toDto)
                .toList();
    }
}

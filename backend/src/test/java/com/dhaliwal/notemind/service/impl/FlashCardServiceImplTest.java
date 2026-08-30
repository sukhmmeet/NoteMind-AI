package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.flashCard.ai.AIFlashCardResponse;
import com.dhaliwal.notemind.dto.flashCard.ai.Flashcard;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashCardServiceImplTest {

    @Mock
    private FlashCardRepository flashCardRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private AIService aiService;

    @Mock
    private FlashCardMapper flashCardMapper;

    @InjectMocks
    private FlashCardServiceImpl flashCardService;

    @Test
    void shouldGenerateFlashCardsSuccessfully() {
        Long noteId = 1L;

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .id(noteId)
                .title("Title")
                .content("Content")
                .imageUrl(null)
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        Flashcard aiFlashCard = new Flashcard("Q1", "A1");

        AIFlashCardResponse aiResponse =
                new AIFlashCardResponse(List.of(aiFlashCard));

        FlashCard flashCard = FlashCard.builder()
                .question("Q1")
                .answer("A1")
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Q1")
                .answer("A1")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        when(aiService.getAIFlashCardResponse(
                "Title",
                "Content",
                null,
                5
        )).thenReturn(aiResponse);

        when(flashCardMapper.toEntity(aiFlashCard))
                .thenReturn(flashCard);

        when(flashCardRepository.saveAll(any()))
                .thenReturn(List.of(flashCard));

        when(flashCardMapper.toDto(flashCard))
                .thenReturn(dto);

        List<FlashCardDto> result =
                flashCardService.generateFlashCards(noteId, 5);

        assertEquals(1, result.size());

        verify(flashCardRepository).saveAll(any());
        verify(noteRepository).save(note);
    }

    @Test
    void shouldThrowWhenGenerateFlashCardsNoteNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoteNotFoundException.class,
                () -> flashCardService.generateFlashCards(1L, 5)
        );

        verify(aiService, never())
                .getAIFlashCardResponse(any(), any(), any(), anyInt());
    }

    @Test
    void shouldThrowWhenGenerateFlashCardsUnauthorized() {

        User current = User.builder().id(1L).build();

        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .build();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(current);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.generateFlashCards(1L, 5)
        );

        verify(aiService, never())
                .getAIFlashCardResponse(any(), any(), any(), anyInt());
    }

    @Test
    void shouldReturnFlashCardsByNote() {

        User user = User.builder().id(1L).build();

        FlashCard flashCard = FlashCard.builder()
                .question("Q")
                .answer("A")
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Q")
                .answer("A")
                .build();

        Note note = Note.builder()
                .user(user)
                .flashCards(List.of(flashCard))
                .build();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(flashCardMapper.toDto(flashCard))
                .thenReturn(dto);

        List<FlashCardDto> result =
                flashCardService.getFlashCardsByNote(1L);

        assertEquals(1, result.size());
        assertEquals("Q", result.getFirst().getQuestion());
    }

    @Test
    void shouldThrowWhenGetFlashCardsByNoteUnauthorized() {

        User current = User.builder().id(1L).build();

        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .build();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(current);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.getFlashCardsByNote(1L)
        );
    }
    @Test
    void shouldReturnFlashCardSuccessfully() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .id(10L)
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .id(100L)
                .question("Q1")
                .answer("A1")
                .note(note)
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Q1")
                .answer("A1")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(100L)).thenReturn(Optional.of(flashCard));
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result = flashCardService.getFlashCard(100L);

        assertNotNull(result);
        assertEquals("Q1", result.getQuestion());
        assertEquals("A1", result.getAnswer());
    }

    @Test
    void shouldThrowWhenGetFlashCardNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(flashCardRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                FlashCardNotFoundException.class,
                () -> flashCardService.getFlashCard(100L)
        );
    }

    @Test
    void shouldThrowWhenGetFlashCardUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .id(10L)
                .user(owner)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .id(100L)
                .note(note)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(flashCardRepository.findById(100L)).thenReturn(Optional.of(flashCard));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.getFlashCard(100L)
        );
    }

    @Test
    void shouldCreateFlashCardSuccessfully() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .id(10L)
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Question")
                .answer("Answer")
                .build();

        FlashCard flashCard = FlashCard.builder()
                .id(100L)
                .question("Question")
                .answer("Answer")
                .note(note)
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Question")
                .answer("Answer")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(flashCardRepository.save(any(FlashCard.class))).thenReturn(flashCard);
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result = flashCardService.createFlashCard(10L, request);

        assertNotNull(result);
        assertEquals("Question", result.getQuestion());

        verify(noteRepository).save(note);
    }

    @Test
    void shouldThrowWhenCreateFlashCardUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Q")
                .answer("A")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.createFlashCard(10L, request)
        );
    }
    @Test
    void shouldThrowWhenCreateFlashCardNoteNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(noteRepository.findById(10L))
                .thenReturn(Optional.empty());

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Q")
                .answer("A")
                .build();

        assertThrows(
                NoteNotFoundException.class,
                () -> flashCardService.createFlashCard(10L, request)
        );
    }

    @Test
    void shouldUpdateFlashCardSuccessfully() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .question("Old Question")
                .answer("Old Answer")
                .note(note)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("New Question")
                .answer("New Answer")
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("New Question")
                .answer("New Answer")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));
        when(flashCardRepository.save(flashCard)).thenReturn(flashCard);
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result = flashCardService.updateFlashCard(1L, request);

        assertEquals("New Question", result.getQuestion());
        assertEquals("New Answer", result.getAnswer());

        verify(flashCardRepository).save(flashCard);
    }


    @Test
    void shouldThrowWhenUpdateFlashCardNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(flashCardRepository.findById(1L))
                .thenReturn(Optional.empty());

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Q")
                .answer("A")
                .build();

        assertThrows(
                FlashCardNotFoundException.class,
                () -> flashCardService.updateFlashCard(1L, request)
        );
    }

    @Test
    void shouldThrowWhenUpdateFlashCardUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .note(note)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Q")
                .answer("A")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.updateFlashCard(1L, request)
        );
    }

    @Test
    void shouldUpdateOnlyQuestionWhenAnswerIsNull() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .question("Old Question")
                .answer("Old Answer")
                .note(note)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question("Updated Question")
                .answer(null)
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Updated Question")
                .answer("Old Answer")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));
        when(flashCardRepository.save(flashCard)).thenReturn(flashCard);
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result = flashCardService.updateFlashCard(1L, request);

        assertEquals("Updated Question", result.getQuestion());
        assertEquals("Old Answer", result.getAnswer());
    }
    @Test
    void shouldUpdateOnlyAnswerWhenQuestionIsNull() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .question("Old Question")
                .answer("Old Answer")
                .note(note)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question(null)
                .answer("Updated Answer")
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Old Question")
                .answer("Updated Answer")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));
        when(flashCardRepository.save(flashCard)).thenReturn(flashCard);
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result = flashCardService.updateFlashCard(1L, request);

        assertEquals("Old Question", result.getQuestion());
        assertEquals("Updated Answer", result.getAnswer());
    }

    @Test
    void shouldDeleteFlashCardSuccessfully() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .id(1L)
                .note(note)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));

        flashCardService.deleteFlashCard(1L);

        verify(flashCardRepository).delete(flashCard);
    }

    @Test
    void shouldThrowWhenDeleteFlashCardNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(flashCardRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                FlashCardNotFoundException.class,
                () -> flashCardService.deleteFlashCard(1L)
        );
    }

    @Test
    void shouldThrowWhenDeleteFlashCardUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .note(note)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.deleteFlashCard(1L)
        );
    }

    @Test
    void shouldClearAllFlashCardsSuccessfully() {

        User user = User.builder().id(1L).build();

        FlashCard flashCard = FlashCard.builder().build();

        Note note = Note.builder()
                .user(user)
                .flashCards(new ArrayList<>(List.of(flashCard)))
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        flashCardService.deleteAllFlashCardsOfNote(1L);

        verify(noteRepository).save(note);
    }
    @Test
    void shouldThrowWhenClearAllFlashCardsNoteNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoteNotFoundException.class,
                () -> flashCardService.deleteAllFlashCardsOfNote(1L)
        );
    }

    @Test
    void shouldThrowWhenClearAllFlashCardsUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .flashCards(new ArrayList<>())
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.deleteAllFlashCardsOfNote(1L)
        );
    }

    @Test
    void shouldRegenerateFlashCardsSuccessfully() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .imageUrl(null)
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        Flashcard aiFlashCard = new Flashcard("Q1", "A1");

        AIFlashCardResponse response =
                new AIFlashCardResponse(List.of(aiFlashCard));

        FlashCard flashCard = FlashCard.builder()
                .question("Q1")
                .answer("A1")
                .note(note)
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Q1")
                .answer("A1")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(aiService.getAIFlashCardResponse(
                "Title",
                "Content",
                null,
                5
        )).thenReturn(response);

        when(flashCardMapper.toEntity(aiFlashCard))
                .thenReturn(flashCard);

        when(flashCardRepository.saveAll(any()))
                .thenReturn(List.of(flashCard));

        when(flashCardMapper.toDto(flashCard))
                .thenReturn(dto);

        List<FlashCardDto> result =
                flashCardService.regenerateFlashCards(1L, 5);

        assertEquals(1, result.size());

        verify(flashCardRepository).deleteAll(any());
        verify(noteRepository, atLeastOnce()).save(note);
    }

    @Test
    void shouldThrowWhenRegenerateFlashCardsNoteNotFound() {

        when(currentUserProvider.getCurrentUser())
                .thenReturn(User.builder().id(1L).build());

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoteNotFoundException.class,
                () -> flashCardService.regenerateFlashCards(1L, 5)
        );
    }

    @Test
    void shouldThrowWhenRegenerateFlashCardsUnauthorized() {

        User current = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();

        Note note = Note.builder()
                .user(owner)
                .flashCards(new ArrayList<>())
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(current);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        assertThrows(
                InvalidCredentialsException.class,
                () -> flashCardService.regenerateFlashCards(1L, 5)
        );
    }
    @Test
    void shouldReturnEmptyFlashCardsWhenNoteHasNoFlashCards() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        List<FlashCardDto> result =
                flashCardService.getFlashCardsByNote(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateNothingWhenQuestionAndAnswerAreNull() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .user(user)
                .build();

        FlashCard flashCard = FlashCard.builder()
                .question("Q")
                .answer("A")
                .note(note)
                .build();

        FlashCardRequestDto request = FlashCardRequestDto.builder()
                .question(null)
                .answer(null)
                .build();

        FlashCardDto dto = FlashCardDto.builder()
                .question("Q")
                .answer("A")
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(flashCardRepository.findById(1L)).thenReturn(Optional.of(flashCard));
        when(flashCardRepository.save(flashCard)).thenReturn(flashCard);
        when(flashCardMapper.toDto(flashCard)).thenReturn(dto);

        FlashCardDto result =
                flashCardService.updateFlashCard(1L, request);

        assertEquals("Q", result.getQuestion());
        assertEquals("A", result.getAnswer());
    }

    @Test
    void shouldGenerateZeroFlashCardsWhenAiReturnsEmptyList() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .title("Title")
                .content("Content")
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        AIFlashCardResponse response =
                new AIFlashCardResponse(new ArrayList<>());

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        when(aiService.getAIFlashCardResponse(
                anyString(),
                anyString(),
                any(),
                anyInt()
        )).thenReturn(response);

        when(flashCardRepository.saveAll(any()))
                .thenReturn(new ArrayList<>());

        List<FlashCardDto> result =
                flashCardService.generateFlashCards(1L, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRegenerateZeroFlashCardsWhenAiReturnsEmptyList() {

        User user = User.builder().id(1L).build();

        Note note = Note.builder()
                .title("Title")
                .content("Content")
                .user(user)
                .flashCards(new ArrayList<>())
                .build();

        AIFlashCardResponse response =
                new AIFlashCardResponse(new ArrayList<>());

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        when(aiService.getAIFlashCardResponse(
                anyString(),
                anyString(),
                any(),
                anyInt()
        )).thenReturn(response);

        when(flashCardRepository.saveAll(any()))
                .thenReturn(new ArrayList<>());

        List<FlashCardDto> result =
                flashCardService.regenerateFlashCards(1L, 5);

        assertTrue(result.isEmpty());
    }
}
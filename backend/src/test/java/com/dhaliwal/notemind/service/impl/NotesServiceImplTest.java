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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class NotesServiceImplTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private AIService aiService;

    @Mock
    private ImageManagerService imageManagerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private NotesServiceImpl notesService;

    private User user;
    private Note note;
    private NoteDto noteDto;
    private Folder folder;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("dhaliwal");

        folder = new Folder();
        folder.setId(1L);
        folder.setName("Spring");
        folder.setUser(user);

        note = new Note();
        note.setId(1L);
        note.setTitle("Spring Boot");
        note.setContent("Learning Spring Boot");
        note.setSummary("Summary");
        note.setUser(user);
        note.setFolder(folder);

        noteDto = new NoteDto();
        noteDto.setId(1L);
        noteDto.setTitle("Spring Boot");
        noteDto.setContent("Learning Spring Boot");
        noteDto.setSummary("Summary");
        noteDto.setFolderId(1L);
    }

    /* ==========================================================
                        Helper Methods
       ========================================================== */

    private MockMultipartFile createImage() {
        return new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "dummy-image".getBytes()
        );
    }

    private AINoteResponse createAIResponse() {

        AINoteResponse response = new AINoteResponse();
        response.setSummary("AI Summary");
        response.setTags(List.of("java", "spring"));

        return response;
    }

    private Tag createTag(String name) {

        Tag tag = new Tag();
        tag.setId((long) name.hashCode());
        tag.setName(name);

        return tag;
    }

    private Note createNoteWithoutFolder() {

        Note note = new Note();
        note.setId(10L);
        note.setTitle("Docker");
        note.setContent("Docker Notes");
        note.setUser(user);

        return note;
    }
    private NoteDto getNoteDtoByGivenSummaryType(SummaryType summaryType) {
        noteDto = new NoteDto();
        noteDto.setId(1L);
        noteDto.setTitle("Spring Boot");
        noteDto.setContent("Learning Spring Boot");
        noteDto.setSummary("Summary");
        noteDto.setFolderId(1L);
        noteDto.setSummaryType(summaryType);
        return noteDto;
    }
    @Test
    void createNote_ShouldCreateWithoutImage() {

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                isNull(),
                eq(SummaryType.SHORT)
        )).thenReturn(createAIResponse());

        when(tagRepository.findByNameIn(anySet()))
                .thenReturn(Collections.emptyList());

        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.createNote(noteDto, null);

        assertNotNull(result);

        verify(noteRepository, times(2)).save(any(Note.class));
        verify(aiService).getAIResponse(
                anyString(),
                anyString(),
                isNull(),
                eq(SummaryType.SHORT)
        );
        verify(noteMapper).toDto(any(Note.class));
    }
    @Test
    void createNote_ShouldCreateWithImage() {

        MockMultipartFile image = createImage();

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(imageManagerService.uploadAndGetUrl(image))
                .thenReturn("http://image-url");

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                anyString(),
                eq(SummaryType.SHORT)
        )).thenReturn(createAIResponse());

        when(tagRepository.findByNameIn(anySet()))
                .thenReturn(Collections.emptyList());

        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.createNote(noteDto, image);

        assertNotNull(result);

        verify(imageManagerService).uploadAndGetUrl(image);
        verify(aiService).getAIResponse(
                anyString(),
                anyString(),
                eq("http://image-url"),
                eq(SummaryType.SHORT)
        );
    }
    @Test
    void createNote_ShouldHandleAiFailure() {

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("AI Error"));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.createNote(noteDto, null);

        assertNotNull(result);

        verify(noteRepository, times(2)).save(any(Note.class));
        verify(noteMapper).toDto(any(Note.class));
    }

    @Test
    void createNote_ShouldReuseExistingTags() {

        Tag javaTag = createTag("java");
        Tag springTag = createTag("spring");

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(anyString(), anyString(), any()))
                .thenReturn(createAIResponse());

        when(tagRepository.findByNameIn(anySet()))
                .thenReturn(List.of(javaTag, springTag));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        notesService.createNote(noteDto, null);

        verify(tagRepository, never()).save(any(Tag.class));
    }
    @Test
    void createNote_ShouldCreateNewTags() {

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                any(),
                eq(SummaryType.SHORT)
        )).thenReturn(createAIResponse());

        when(tagRepository.findByNameIn(anySet()))
                .thenReturn(Collections.emptyList());

        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        notesService.createNote(noteDto, null);

        verify(tagRepository, times(2)).save(any(Tag.class));
    }
    @ParameterizedTest
    @EnumSource(value = SummaryType.class, names = {
            "SHORT",
            "DETAILED",
            "BULLET_POINTS"
    })
    void shouldCreateNoteWithDifferentSummaryTypes(SummaryType summaryType) {

        when(securityUtils.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                isNull(),
                eq(summaryType)
        )).thenReturn(createAIResponse());

        when(tagRepository.findByNameIn(anySet()))
                .thenReturn(Collections.emptyList());

        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.createNote(getNoteDtoByGivenSummaryType(summaryType), null);

        assertNotNull(result);

        verify(noteRepository, times(2)).save(any(Note.class));

        verify(aiService, times(1)).getAIResponse(
                anyString(),
                anyString(),
                isNull(),
                eq(summaryType)
        );

        verify(noteMapper).toDto(any(Note.class));
    }
    @Test
    void getAllNotes_ShouldReturnAllNotesOfCurrentUser() {

        when(securityUtils.getUserId()).thenReturn(1L);

        Note note1 = createNoteWithoutFolder();
        Note note2 = new Note();
        note2.setId(20L);
        note2.setTitle("Java");
        note2.setContent("OOP");
        note2.setUser(user);

        when(noteRepository.findAllByUserId(1L))
                .thenReturn(List.of(note1, note2));

        NoteDto dto1 = new NoteDto();
        dto1.setId(10L);

        NoteDto dto2 = new NoteDto();
        dto2.setId(20L);

        when(noteMapper.toDto(note1)).thenReturn(dto1);
        when(noteMapper.toDto(note2)).thenReturn(dto2);

        List<NoteDto> result = notesService.getAllNotes();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(noteRepository).findAllByUserId(1L);
        verify(noteMapper).toDto(note1);
        verify(noteMapper).toDto(note2);
    }
    @Test
    void getNoteById_ShouldReturnNote() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(noteMapper.toDto(note))
                .thenReturn(noteDto);

        NoteDto result = notesService.getNoteById(1L);

        assertNotNull(result);
        assertEquals(noteDto.getId(), result.getId());

        verify(noteRepository).findById(1L);
        verify(noteMapper).toDto(note);
    }
    @Test
    void getNoteById_ShouldThrowException_WhenNoteNotFound() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.getNoteById(1L)
                );

        assertTrue(exception.getMessage().contains("Not find note"));

        verify(noteMapper, never()).toDto(any());
    }
    @Test
    void searchNotes_ShouldReturnMatchingNotes() {

        when(securityUtils.getUserId()).thenReturn(1L);

        Note note1 = createNoteWithoutFolder();

        when(noteRepository.searchNotes(1L, "Docker"))
                .thenReturn(List.of(note1));

        NoteDto dto = new NoteDto();
        dto.setId(10L);

        when(noteMapper.toDto(note1))
                .thenReturn(dto);

        List<NoteDto> result =
                notesService.searchNotes("Docker");

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getId());

        verify(noteRepository)
                .searchNotes(1L, "Docker");
    }
    @Test
    void searchNotes_ShouldThrowException_WhenUserNotLoggedIn() {

        when(securityUtils.getUserId()).thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.searchNotes("Spring")
                );

        assertEquals("User not login", exception.getMessage());

        verify(noteRepository, never()).searchNotes(anyLong(), anyString());
    }
    @Test
    void searchNotes_ShouldThrowException_WhenSearchTermIsEmpty() {

        when(securityUtils.getUserId()).thenReturn(1L);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> notesService.searchNotes("")
                );

        assertEquals("Search term is empty", exception.getMessage());

        verify(noteRepository, never()).searchNotes(anyLong(), anyString());
    }
    @Test
    void updateNote_ShouldUpdateWithoutImage() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                any(),
                eq(SummaryType.SHORT)
        )).thenReturn(createAIResponse());

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto dto = new NoteDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setImageUrl("old-url");

        NoteDto result = notesService.updateNote(1L, dto, null);

        assertNotNull(result);

        verify(noteRepository).findById(1L);
        verify(aiService).getAIResponse(
                eq("Updated Title"),
                eq("Updated Content"),
                eq("old-url"),
                eq(SummaryType.SHORT)
        );
        verify(noteRepository).save(any(Note.class));
        verify(noteMapper).toDto(any(Note.class));
    }

    @Test
    void updateNote_ShouldUpdateWithImage() {

        MockMultipartFile image = createImage();

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(imageManagerService.uploadAndGetUrl(image))
                .thenReturn("new-image-url");

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                any(),
                eq(SummaryType.SHORT)
        )).thenReturn(createAIResponse());

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto dto = new NoteDto();
        dto.setTitle("Updated");
        dto.setContent("Updated Content");

        NoteDto result = notesService.updateNote(1L, dto, image);

        assertNotNull(result);

        verify(imageManagerService).uploadAndGetUrl(image);
        verify(noteRepository).save(any(Note.class));
        verify(noteMapper).toDto(any(Note.class));
    }

    @Test
    void updateNote_ShouldThrowException_WhenNoteNotFound() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notesService.updateNote(1L, noteDto, null)
        );

        assertEquals("Note not found", exception.getMessage());

        verify(noteRepository, never()).save(any());
        verify(aiService, never()).getAIResponse(any(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = SummaryType.class, names = {
            "SHORT",
            "DETAILED",
            "BULLET_POINTS"
    })
    void shouldUpdateNoteWithDifferentSummaryTypes(SummaryType summaryType) {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(aiService.getAIResponse(
                anyString(),
                anyString(),
                any(),
                eq(summaryType)
        )).thenReturn(createAIResponse());

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto dto = new NoteDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated Content");
        dto.setImageUrl("old-url");
        dto.setSummaryType(summaryType);

        NoteDto result =
                notesService.updateNote(1L, dto, null);

        assertNotNull(result);

        verify(noteRepository).findById(1L);

        verify(aiService).getAIResponse(
                eq("Updated Title"),
                eq("Updated Content"),
                eq("old-url"),
                eq(summaryType)
        );

        verify(noteRepository).save(any(Note.class));
        verify(noteMapper).toDto(any(Note.class));
    }

    @Test
    void deleteNote_ShouldDeleteSuccessfully() {

        when(noteRepository.existsById(1L))
                .thenReturn(true);

        notesService.deleteNote(1L);

        verify(noteRepository).deleteById(1L);
    }

    @Test
    void deleteNote_ShouldThrowException_WhenNoteNotFound() {

        when(noteRepository.existsById(1L))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notesService.deleteNote(1L)
        );

        assertEquals("Note not found", exception.getMessage());

        verify(noteRepository, never()).deleteById(anyLong());
    }
    @Test
    void moveToFolder_ShouldMoveSuccessfully() {

        note.setFolder(null);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.moveToFolder(1L, 1L);

        assertNotNull(result);
        assertEquals(folder, note.getFolder());

        verify(noteRepository).save(note);
    }

    @Test
    void moveToFolder_ShouldThrowException_WhenNoteNotFound() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.moveToFolder(1L, 1L)
                );

        assertEquals("Note not found", exception.getMessage());

        verify(folderRepository, never()).findById(anyLong());
    }

    @Test
    void moveToFolder_ShouldThrowException_WhenFolderNotFound() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(folderRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.moveToFolder(1L, 1L)
                );

        assertEquals("Folder not found", exception.getMessage());
    }

    @Test
    void moveToFolder_ShouldThrowException_WhenUnauthorized() {

        User anotherUser = new User();
        anotherUser.setId(99L);

        note.setUser(anotherUser);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.moveToFolder(1L, 1L)
                );

        assertEquals("Unauthorized move", exception.getMessage());

        verify(noteRepository, never()).save(any());
    }

    @Test
    void removeFromFolder_ShouldRemoveSuccessfully() {

        note.setFolder(folder);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(noteDto);

        NoteDto result = notesService.removeFromFolder(1L);

        assertNotNull(result);
        assertNull(note.getFolder());

        verify(noteRepository).save(note);
    }

    @Test
    void removeFromFolder_ShouldThrowException_WhenNoteNotFound() {

        when(noteRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.removeFromFolder(1L)
                );

        assertEquals("Note not found", exception.getMessage());
    }

    @Test
    void removeFromFolder_ShouldThrowException_WhenUnauthorized() {

        User anotherUser = new User();
        anotherUser.setId(100L);

        note.setUser(anotherUser);

        when(noteRepository.findById(1L))
                .thenReturn(Optional.of(note));

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> notesService.removeFromFolder(1L)
                );

        assertEquals("Unauthorized move", exception.getMessage());

        verify(noteRepository, never()).save(any());
    }

}
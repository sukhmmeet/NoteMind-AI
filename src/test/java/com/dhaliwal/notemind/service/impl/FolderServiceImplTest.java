package com.dhaliwal.notemind.service.impl;

import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.dto.NoteDto;
import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import com.dhaliwal.notemind.entity.Folder;
import com.dhaliwal.notemind.entity.Note;
import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.exception.*;
import com.dhaliwal.notemind.mapper.FolderMapper;
import com.dhaliwal.notemind.mapper.NoteMapper;
import com.dhaliwal.notemind.repository.FolderRepository;
import com.dhaliwal.notemind.repository.NoteRepository;
import com.dhaliwal.notemind.repository.UserRepository;
import com.dhaliwal.notemind.security.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FolderServiceImpl folderService;

    private User user;
    private Folder folder;
    private FolderRequestDto requestDto;
    private FolderDtoWithoutNotes folderDto;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .username("john")
                .build();

        folder = Folder.builder()
                .id(10L)
                .name("Work")
                .user(user)
                .build();

        requestDto = new FolderRequestDto();
        requestDto.setName("Work");

        folderDto = FolderDtoWithoutNotes.builder()
                .id(10L)
                .name("Work")
                .build();
    }

    @Test
    void shouldCreateFolderSuccessfully() {

        when(folderRepository.findByName("Work"))
                .thenReturn(Optional.empty());

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(folderMapper.toDtoWithoutNotes(any(Folder.class)))
                .thenReturn(folderDto);

        FolderDtoWithoutNotes result =
                folderService.createFolder(requestDto);

        assertNotNull(result);
        assertEquals("Work", result.getName());

        ArgumentCaptor<Folder> captor =
                ArgumentCaptor.forClass(Folder.class);

        verify(folderRepository).save(captor.capture());

        Folder savedFolder = captor.getValue();

        assertEquals("Work", savedFolder.getName());
        assertEquals(user, savedFolder.getUser());
    }

    @Test
    void shouldThrowExceptionWhenFolderNameIsEmpty() {

        requestDto.setName("");

        InvalidFolderException exception =
                assertThrows(
                        InvalidFolderException.class,
                        () -> folderService.createFolder(requestDto)
                );

        assertEquals(
                "Folder name cannot be empty",
                exception.getMessage()
        );

        verify(folderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFolderAlreadyExists() {

        when(folderRepository.findByName("Work"))
                .thenReturn(Optional.of(folder));

        FolderAlreadyExistsException exception =
                assertThrows(
                        FolderAlreadyExistsException.class,
                        () -> folderService.createFolder(requestDto)
                );

        assertEquals(
                "Folder already exists",
                exception.getMessage()
        );

        verify(folderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(folderRepository.findByName("Work"))
                .thenReturn(Optional.empty());

        when(securityUtils.getUserId())
                .thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> folderService.createFolder(requestDto)
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(folderRepository, never()).save(any());
    }
    @Test
    void shouldReturnAllFoldersWithoutNotes() {

        when(securityUtils.getUserId()).thenReturn(1L);

        Folder folder1 = Folder.builder()
                .id(1L)
                .name("Work")
                .user(user)
                .build();

        Folder folder2 = Folder.builder()
                .id(2L)
                .name("Personal")
                .user(user)
                .build();

        FolderDtoWithoutNotes dto1 = FolderDtoWithoutNotes.builder()
                .id(1L)
                .name("Work")
                .build();

        FolderDtoWithoutNotes dto2 = FolderDtoWithoutNotes.builder()
                .id(2L)
                .name("Personal")
                .build();

        when(folderRepository.findAllByUserId(1L))
                .thenReturn(List.of(folder1, folder2));

        when(folderMapper.toDtoWithoutNotes(folder1))
                .thenReturn(dto1);

        when(folderMapper.toDtoWithoutNotes(folder2))
                .thenReturn(dto2);

        List<FolderDtoWithoutNotes> result =
                folderService.GetAllFoldersWithoutNotes();

        assertEquals(2, result.size());
        assertEquals("Work", result.get(0).getName());
        assertEquals("Personal", result.get(1).getName());

        verify(folderRepository).findAllByUserId(1L);
    }
    @Test
    void shouldReturnEmptyFolderListWhenUserHasNoFolders() {

        when(securityUtils.getUserId()).thenReturn(1L);

        when(folderRepository.findAllByUserId(1L))
                .thenReturn(List.of());

        List<FolderDtoWithoutNotes> result =
                folderService.GetAllFoldersWithoutNotes();

        assertTrue(result.isEmpty());

        verify(folderRepository).findAllByUserId(1L);
    }
    @Test
    void shouldReturnFoldersWithNotInFolderCategory() {

        when(securityUtils.getUserId()).thenReturn(1L);

        Folder folder = Folder.builder()
                .id(1L)
                .name("Programming")
                .user(user)
                .build();

        FolderDto folderDto = FolderDto.builder()
                .id(1L)
                .name("Programming")
                .notes(List.of())
                .build();

        Note note = Note.builder()
                .id(10L)
                .title("Java")
                .user(user)
                .build();

        when(folderRepository.findAllByUserId(1L))
                .thenReturn(List.of(folder));

        when(noteRepository.findNotesWithoutFolderByUserId(1L))
                .thenReturn(List.of(note));

        when(folderMapper.toDto(folder))
                .thenReturn(folderDto);

        when(noteMapper.toDto(note))
                .thenReturn(
                        com.dhaliwal.notemind.dto.NoteDto.builder()
                                .id(10L)
                                .title("Java")
                                .build()
                );

        List<FolderDto> result =
                folderService.GetAllFoldersWithNotes();

        assertEquals(2, result.size());

        assertEquals("Programming",
                result.get(0).getName());

        assertEquals("NotInFolder",
                result.get(1).getName());

        assertEquals(1,
                result.get(1).getNotes().size());

        assertEquals("Java",
                result.get(1).getNotes().get(0).getTitle());

        verify(folderRepository).findAllByUserId(1L);
        verify(noteRepository).findNotesWithoutFolderByUserId(1L);
    }
    @Test
    void shouldReturnOnlyNotInFolderWhenNoFoldersExist() {

        when(securityUtils.getUserId()).thenReturn(1L);

        Note note = Note.builder()
                .id(10L)
                .title("Java")
                .user(user)
                .folder(null)
                .build();

        when(folderRepository.findAllByUserId(1L))
                .thenReturn(List.of());

        when(noteRepository.findNotesWithoutFolderByUserId(user.getId()))
                .thenReturn(List.of(note));

        when(noteMapper.toDto(any(Note.class)))
                .thenReturn(
                        NoteDto.builder()
                                .id(10L)
                                .title("Java")
                                .build()
                );

        List<FolderDto> result =
                folderService.GetAllFoldersWithNotes();

        assertEquals(1, result.size());

        assertEquals("NotInFolder",
                result.get(0).getName());

        assertEquals(1,
                result.get(0).getNotes().size());

        verify(folderRepository).findAllByUserId(1L);
        verify(noteRepository).findNotesWithoutFolderByUserId(user.getId());
    }
    @Test
    void shouldUpdateFolderSuccessfully() {

        FolderRequestDto dto = new FolderRequestDto();
        dto.setName("Updated");

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FolderDtoWithoutNotes response = FolderDtoWithoutNotes.builder()
                .id(10L)
                .name("Updated")
                .build();

        when(folderMapper.toDtoWithoutNotes(any(Folder.class)))
                .thenReturn(response);

        FolderDtoWithoutNotes result =
                folderService.updateFolder(10L, dto);

        assertEquals("Updated", result.getName());

        verify(folderRepository).save(folder);
    }

    @Test
    void shouldThrowWhenUpdatingFolderWithEmptyName() {

        FolderRequestDto dto = new FolderRequestDto();
        dto.setName("");

        InvalidFolderException exception =
                assertThrows(
                        InvalidFolderException.class,
                        () -> folderService.updateFolder(10L, dto)
                );

        assertEquals(
                "Folder name cannot be empty",
                exception.getMessage()
        );

        verify(folderRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdatingMissingFolder() {

        FolderRequestDto dto = new FolderRequestDto();
        dto.setName("Updated");

        when(folderRepository.findById(10L))
                .thenReturn(Optional.empty());

        FolderNotFoundException exception =
                assertThrows(
                        FolderNotFoundException.class,
                        () -> folderService.updateFolder(10L, dto)
                );

        assertEquals("Folder not found", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUpdatingFolderOfAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .username("alice")
                .build();

        folder.setUser(anotherUser);

        FolderRequestDto dto = new FolderRequestDto();
        dto.setName("Updated");

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> folderService.updateFolder(10L, dto)
                );

        assertEquals("User not authorized", exception.getMessage());

        verify(folderRepository, never()).save(any());
    }

    @Test
    void shouldDeleteFolderSuccessfully() {

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        folderService.deleteFolder(10L);

        verify(folderRepository).delete(folder);
    }

    @Test
    void shouldThrowWhenDeletingMissingFolder() {

        when(folderRepository.findById(10L))
                .thenReturn(Optional.empty());

        FolderNotFoundException exception =
                assertThrows(
                        FolderNotFoundException.class,
                        () -> folderService.deleteFolder(10L)
                );

        assertEquals("Folder not found", exception.getMessage());

        verify(folderRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeletingFolderOfAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .build();

        folder.setUser(anotherUser);

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> folderService.deleteFolder(10L)
                );

        assertEquals("User not authorized", exception.getMessage());

        verify(folderRepository, never()).delete(any());
    }

    @Test
    void shouldReturnFolderById() {

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        when(folderMapper.toDtoWithoutNotes(folder))
                .thenReturn(folderDto);

        FolderDtoWithoutNotes result =
                folderService.getFolderById(10L);

        assertNotNull(result);
        assertEquals("Work", result.getName());

        verify(folderMapper).toDtoWithoutNotes(folder);
    }

    @Test
    void shouldThrowWhenFolderNotFoundById() {

        when(folderRepository.findById(10L))
                .thenReturn(Optional.empty());

        FolderNotFoundException exception =
                assertThrows(
                        FolderNotFoundException.class,
                        () -> folderService.getFolderById(10L)
                );

        assertEquals("Folder not found", exception.getMessage());
    }

    @Test
    void shouldThrowWhenGettingFolderOfAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .build();

        folder.setUser(anotherUser);

        when(folderRepository.findById(10L))
                .thenReturn(Optional.of(folder));

        when(securityUtils.getUserId()).thenReturn(1L);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> folderService.getFolderById(10L)
                );

        assertEquals("User not authorized", exception.getMessage());
    }
}
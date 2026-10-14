package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDto;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FolderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FolderService folderService;

    @InjectMocks
    private FolderController folderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(folderController).build();
    }

    @Test
    void testCreateFolder() throws Exception {
        FolderRequestDto request = new FolderRequestDto();
        request.setName("New Folder");

        FolderDtoWithoutNotes response = FolderDtoWithoutNotes.builder()
                .id(1L)
                .name("New Folder")
                .build();

        when(folderService.createFolder(any(FolderRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/folder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Folder created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("New Folder"));
    }

    @Test
    void testGetFoldersWithoutNotes() throws Exception {
        FolderDtoWithoutNotes folder1 = FolderDtoWithoutNotes.builder().id(1L).name("Folder 1").build();
        FolderDtoWithoutNotes folder2 = FolderDtoWithoutNotes.builder().id(2L).name("Folder 2").build();

        when(folderService.GetAllFoldersWithoutNotes()).thenReturn(List.of(folder1, folder2));

        mockMvc.perform(get("/folder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Folder 1"));
    }

    @Test
    void testGetFoldersWithNotes() throws Exception {
        FolderDto folder1 = FolderDto.builder().id(1L).name("Folder 1").build();

        when(folderService.GetAllFoldersWithNotes()).thenReturn(List.of(folder1));

        mockMvc.perform(get("/folder").param("includeNotes", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Folder 1"));
    }

    @Test
    void testGetFolderById() throws Exception {
        FolderDtoWithoutNotes folder = FolderDtoWithoutNotes.builder().id(1L).name("Folder 1").build();

        when(folderService.getFolderById(1L)).thenReturn(folder);

        mockMvc.perform(get("/folder/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Folder 1"));
    }

    @Test
    void testUpdateFolder() throws Exception {
        FolderRequestDto request = new FolderRequestDto();
        request.setName("Updated Folder");

        FolderDtoWithoutNotes response = FolderDtoWithoutNotes.builder()
                .id(1L)
                .name("Updated Folder")
                .build();

        when(folderService.updateFolder(eq(1L), any(FolderRequestDto.class))).thenReturn(response);

        mockMvc.perform(patch("/folder/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Folder updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Folder"));
    }

    @Test
    void testDeleteFolder() throws Exception {
        doNothing().when(folderService).deleteFolder(1L);

        mockMvc.perform(delete("/folder/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Folder deleted successfully"));
    }
}

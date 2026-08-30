package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.ApiResponse;
import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import com.dhaliwal.notemind.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<ApiResponse<FolderDtoWithoutNotes>> createFolder(@RequestBody FolderRequestDto folderRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("Folder created successfully", folderService.createFolder(folderRequestDto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFolders(
            @RequestParam(defaultValue = "false") boolean includeNotes
    ) {
        if (includeNotes) {
            return ResponseEntity.ok(ApiResponse.success(folderService.GetAllFoldersWithNotes()));
        }
        return ResponseEntity.ok(ApiResponse.success(folderService.GetAllFoldersWithoutNotes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FolderDtoWithoutNotes>> getFolderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(folderService.getFolderById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FolderDtoWithoutNotes>> updateFolder(
            @PathVariable Long id,
            @RequestBody FolderRequestDto folderRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("Folder updated successfully", folderService.updateFolder(id, folderRequestDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
        return ResponseEntity.ok(ApiResponse.success("Folder deleted successfully"));
    }
}

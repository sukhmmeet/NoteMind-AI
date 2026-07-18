package com.dhaliwal.notemind.controller;

import com.dhaliwal.notemind.dto.folder.response.FolderDtoWithoutNotes;
import com.dhaliwal.notemind.dto.folder.request.FolderRequestDto;
import com.dhaliwal.notemind.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<FolderDtoWithoutNotes> createFolder(@RequestBody FolderRequestDto folderRequestDto){
        return  ResponseEntity.ok(folderService.createFolder(folderRequestDto));
    }

    @GetMapping
    public ResponseEntity<?> getFolders(
            @RequestParam(defaultValue = "false") boolean includeNotes
    ) {
        if (includeNotes) {
            return ResponseEntity.ok(folderService.GetAllFoldersWithNotes());
        }

        return ResponseEntity.ok(folderService.GetAllFoldersWithoutNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderDtoWithoutNotes> GetFolderById(@PathVariable Long id){
        return ResponseEntity.ok(folderService.getFolderById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FolderDtoWithoutNotes> updateFolder(@PathVariable Long id,@RequestBody FolderRequestDto folderRequestDto){
        return ResponseEntity.ok(folderService.updateFolder(id,folderRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long id){
        folderService.deleteFolder(id);
        return ResponseEntity.ok("Deleted");
    }
    


}

package com.dhaliwal.notemind.dto.folder.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderDtoWithoutNotes {
    private Long id;
    private String name;
}

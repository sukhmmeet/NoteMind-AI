package com.dhaliwal.notemind.dto;

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

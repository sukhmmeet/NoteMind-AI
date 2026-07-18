package com.dhaliwal.notemind.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderDto {
    private Long id;
    private String name;
    @Builder.Default
    private List<NoteDto> notes = new ArrayList<>();
}

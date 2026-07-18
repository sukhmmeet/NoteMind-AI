package com.dhaliwal.notemind.dto;

import com.dhaliwal.notemind.entity.type.SummaryType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteDto implements Serializable {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private SummaryType summaryType;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TagDto> tags;
    private Long folderId;
}

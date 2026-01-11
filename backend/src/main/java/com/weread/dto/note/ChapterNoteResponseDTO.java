package com.weread.dto.note;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 章节笔记响应DTO
 * 对应接口：GET /books/{bookId}/chapters/{chapterId}/notes
 */
@Data
public class ChapterNoteResponseDTO {
    private Integer noteId;
    private String quote;
    private Integer startIndex;
    private Integer endIndex;
    private List<String> lineType;
    private String thought;
    private LocalDateTime createdAt;
    private Integer pageNumber;
}


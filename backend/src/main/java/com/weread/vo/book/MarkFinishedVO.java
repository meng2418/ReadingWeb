package com.weread.vo.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.Instant;

/**
 * 标记书籍为已读完的返回VO
 */
@Data
public class MarkFinishedVO {
    private Integer bookId;
    private String newStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant finishedAt;
    
    private Integer totalFinishedBooks;
    private Boolean milestoneAchieved;
    private String milestoneMessage;
}


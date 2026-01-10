package com.weread.vo.user;

import lombok.Data;
import java.time.LocalDate;

/**
 * 划线VO，用于返回用户的最新划线记录
 */
@Data
public class HighlightVO {
    
    /**
     * 书籍标题
     */
    private String bookTitle;
    
    /**
     * 划线内容
     */
    private String content;
    
    /**
     * 章节名称
     */
    private String chapterName;
    
    /**
     * 划线日期（格式：YYYY-MM-DD）
     */
    private LocalDate highlightDate;
}


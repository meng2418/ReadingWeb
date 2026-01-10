package com.weread.dto.book;

import lombok.Data;

/**
 * 章节目录DTO
 * 用于返回书籍目录列表，包含章节的起始页码、章节序号和章节名称
 */
@Data
public class ChapterDTO {
    
    /**
     * 章节起始页码
     */
    private Integer startPage;
    
    /**
     * 章节序号
     */
    private Integer chapterNumber;
    
    /**
     * 章节名称
     */
    private String chapterName;
}


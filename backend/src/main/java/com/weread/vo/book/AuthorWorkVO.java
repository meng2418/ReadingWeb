package com.weread.vo.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 作者作品VO（用于获取作者代表作）
 * 只包含3个属性：bookId, cover, bookTitle
 */
@Data
public class AuthorWorkVO {
    
    /**
     * 书籍ID（字符串类型）
     */
    @JsonProperty("bookId")
    private String bookId;
    
    /**
     * 封面
     */
    @JsonProperty("cover")
    private String cover;
    
    /**
     * 书籍标题
     */
    @JsonProperty("bookTitle")
    private String bookTitle;
}


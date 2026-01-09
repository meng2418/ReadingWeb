package com.weread.vo.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 相关推荐作品VO（用于获取相关推荐作品）
 * 包含9个属性：bookId, cover, title, description, authorName, rating, readCount, price, isFree
 */
@Data
public class RelatedBookVO {
    
    /**
     * 书籍ID
     */
    @JsonProperty("bookId")
    private Integer bookId;
    
    /**
     * 封面
     */
    @JsonProperty("cover")
    private String cover;
    
    /**
     * 书籍标题
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * 书籍简介
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * 作者名
     */
    @JsonProperty("authorName")
    private String authorName;
    
    /**
     * 推荐值（评分）
     */
    @JsonProperty("rating")
    private Float rating;
    
    /**
     * 阅读量
     */
    @JsonProperty("readCount")
    private Integer readCount;
    
    /**
     * 价格（分）
     */
    @JsonProperty("price")
    private Integer price;
    
    /**
     * 是否免费
     */
    @JsonProperty("isFree")
    private Boolean isFree;
}


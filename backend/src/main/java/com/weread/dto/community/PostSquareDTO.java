package com.weread.dto.community;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostSquareDTO {
    private Integer postId;
    private AuthorInfoDTO author;
    private LocalDateTime publishTime;
    private String publishLocation;
    private Boolean isFollowingAuthor;
    private String postTitle;
    private String content;
    private BookSimpleDTO mentionedFirstBook; 
    private Integer commentCount;
    private Integer likeCount;
    private Boolean isLiked;
    private List<String> topics;
}
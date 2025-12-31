package com.weread.vo.community;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.weread.vo.book.AuthorVO;
import com.weread.vo.book.MentionedBookVO;

@Data
public class TopicPostVO {
    private Integer postId;
    private String postTitle;
    private String content;
    private Integer authorId;
    private String authorName;
    private String authorAvatar;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private Boolean isLiked;
    private List<MentionedBookVO> mentionedBooks;
    private List<TopicDetailRelatedVO> relatedTopics;  // 关联话题
    public void setPublishTime(LocalDateTime createdAt2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPublishTime'");
    }
    public void setPublishLocation(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPublishLocation'");
    }
    public void setAuthor(AuthorVO authorVO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAuthor'");
    }
    public void setIsFollowingAuthor(Object following) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setIsFollowingAuthor'");
    }
    public void setTopics(List<Object> emptyList) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTopics'");
    }
    public void setRelatedTopics(List<TopicPostRelatedVO> relatedTopicVOs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRelatedTopics'");
    }
}
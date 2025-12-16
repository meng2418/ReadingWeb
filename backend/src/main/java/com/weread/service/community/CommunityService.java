package com.weread.service.community;

import com.weread.dto.community.CommentCreationDTO;
import com.weread.vo.community.CommentListVO;
import com.weread.vo.community.LikeInfoVO; // 假设您已经创建了这个VO
import com.weread.vo.community.CommentVO;

import java.util.List;

public interface CommunityService {

    // --- 点赞目标常量 ---
    String TARGET_TYPE_POST = "POST";
    String TARGET_TYPE_COMMENT = "COMMENT";

    // =========================================================
    // 评论功能 (Comment)
    // =========================================================
    
    /**
     * 发表评论 (支持一级评论和回复)
     * @param postId 目标帖子ID
     * @param userId 评论者ID
     * @param dto 评论内容和父评论ID
     * @return 评论的VO
     */
    CommentVO createComment(Long postId, Long userId, CommentCreationDTO dto);

    /**
     * 获取帖子的一级评论列表 (带分页)
     * @param postId 帖子ID
     * @param page 页码 (从1开始)
     * @param limit 每页数量
     * @param currentUserId 当前登录用户ID (用于检查点赞状态)
     * @return 评论列表的分页响应
     */
    CommentListVO getPostComments(Long postId, int page, int limit, Long currentUserId);
    
    /**
     * 获取某条一级评论下的二级评论（回复）列表
     * @param parentCommentId 父评论ID
     * @param currentUserId 当前登录用户ID (用于检查点赞状态)
     * @return 回复列表
     */
    List<CommentVO> getCommentReplies(Long parentCommentId, int page, int limit, Long currentUserId);


    // =========================================================
    // 点赞功能 (Like)
    // =========================================================

    /**
     * 对目标（帖子或评论）执行点赞操作
     * @param targetType 目标类型 (POST 或 COMMENT)
     * @param targetId 目标ID (帖子ID 或 评论ID)
     * @param userId 操作用户ID
     */
    void likeTarget(String targetType, Long targetId, Long userId);
    
    /**
     * 对目标（帖子或评论）执行取消点赞操作
     * @param targetType 目标类型 (POST 或 COMMENT)
     * @param targetId 目标ID (帖子ID 或 评论ID)
     * @param userId 操作用户ID
     */
    void unlikeTarget(String targetType, Long targetId, Long userId);

    /**
     * 获取目标的总赞数和点赞用户列表
     * @param targetType 目标类型 (POST 或 COMMENT)
     * @param targetId 目标ID (帖子ID 或 评论ID)
     * @param limit 限制返回的点赞用户数量
     * @return 包含总赞数和部分点赞用户信息的响应对象
     */
    LikeInfoVO getTargetLikes(String targetType, Long targetId, int limit);
    

    
}
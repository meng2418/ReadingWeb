package com.weread.controller.community;

import com.weread.common.ApiResponse;
import com.weread.dto.response.community.TopicListResponse;
import com.weread.entity.user.UserEntity;
import com.weread.service.community.TopicService;
import com.weread.service.community.PostService;
import com.weread.vo.community.HotTopicVO;
import com.weread.vo.community.TopicDetailVO;
import com.weread.vo.community.TopicPostVO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {
    
    private final TopicService topicService;
    private final PostService postService;
    
    @GetMapping
    public ApiResponse<TopicListResponse> getTopics(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        
        try {
            // 验证参数
            if (limit != null && (limit < 1 || limit > 100)) {
                return ApiResponse.error(400, "limit参数必须在1-100之间");
            }
            
            TopicListResponse data = topicService.getTopics(cursor, limit);
            return ApiResponse.ok(data);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    @GetMapping("/hot")
    public ApiResponse<List<HotTopicVO>> getHotTopics() {
        try {
            List<HotTopicVO> data = topicService.getHotTopics();
            return ApiResponse.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    @GetMapping("/{topicId}")
    public ApiResponse<TopicDetailVO> getTopicDetail(
            @PathVariable Integer topicId,
            @AuthenticationPrincipal UserEntity loginUser) {
    
        try {
            // 获取当前用户ID（如果已登录）
            Integer currentUserId = loginUser != null ? loginUser.getUserId() : null;
        
            // 调用Service
            TopicDetailVO data = topicService.getTopicDetail(topicId, currentUserId);
            return ApiResponse.ok(data);
        
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }

    @GetMapping("/{topicId}/posts")
    public ApiResponse<List<TopicPostVO>> getTopicPosts(
            @PathVariable Integer topicId,
            @RequestParam String sort,
            @RequestParam(required = false) Integer cursor,  // 改为cursor参数
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @AuthenticationPrincipal UserEntity loginUser) {
    
        try {
            // 检查登录状态
            if (loginUser == null) {
                return ApiResponse.error(401, "请先登录");
            }
        
            // 验证参数
            if (!List.of("latest", "hot", "quality").contains(sort)) {
                return ApiResponse.error(400, "sort参数必须是latest、hot或quality");
            }
            if (limit < 1 || limit > 100) limit = 20;
        
            // 获取当前用户ID
            Integer currentUserId = loginUser.getUserId();
        
            // 调用Service（使用cursor）
            List<TopicPostVO> data = postService.getTopicPosts(topicId, sort, cursor, limit, currentUserId);
        
            // 可以计算nextCursor
            Integer nextCursor = null;
            if (!data.isEmpty()) {
                // 取最后一个帖子的ID作为nextCursor
                nextCursor = data.get(data.size() - 1).getPostId();
            }
        
            // 返回结果（可以在response中带上nextCursor）
            return ApiResponse.ok(data);
            // 或者如果需要在响应中返回nextCursor，可以创建一个包装类
        
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "服务器内部错误");
        }
    }
}
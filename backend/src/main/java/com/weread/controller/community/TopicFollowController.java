package com.weread.controller.community;

import com.weread.common.ApiResponse;
import com.weread.service.community.TopicFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "话题关注", description = "话题关注相关接口")
public class TopicFollowController {

    private final TopicFollowService topicFollowService;

    @PostMapping("/{topicId}/follow")
    @Operation(summary = "关注话题", description = "用户关注指定话题")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> followTopic(
            @Parameter(description = "话题ID", required = true)
            @PathVariable Integer topicId,
            @AuthenticationPrincipal Integer userId) {
        
        topicFollowService.followTopic(userId, topicId);

        
        return ResponseEntity.ok(ApiResponse.ok(Map.of("isFollowing", true)));
    }

    @DeleteMapping("/{topicId}/follow")
    @Operation(summary = "取消关注话题", description = "用户取消关注指定话题")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> unfollowTopic(
            @Parameter(description = "话题ID", required = true)
            @PathVariable Integer topicId,
            @AuthenticationPrincipal Integer userId) {
        
        topicFollowService.unfollowTopic(userId, topicId);
        
        return ResponseEntity.ok(ApiResponse.ok(Map.of("isFollowing", false)));
    }
}
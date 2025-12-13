package com.weread.controller.community;

import com.weread.dto.community.TopicCreationDTO;
import com.weread.dto.community.TopicUpdateDTO;
import com.weread.entity.community.TopicEntity;
import com.weread.service.community.TopicService;
import com.weread.vo.community.TopicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * POST /api/v1/topics
     * 管理员或用户创建新话题
     */
    @PostMapping
    public ResponseEntity<TopicVO> createTopic(@Valid @RequestBody TopicCreationDTO dto) {
        try {
            TopicEntity entity = topicService.createTopic(dto.getName(), dto.getDescription());
            return new ResponseEntity<>(TopicVO.fromEntity(entity), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // 返回 409 Conflict，表示话题名称已存在
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        }
    }

    /**
     * GET /api/v1/topics
     * 获取所有话题列表 (可用于话题选择器)
     */
    @GetMapping
    public List<TopicVO> getAllTopics() {
        return topicService.getAllTopics().stream()
                .map(TopicVO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * GET /api/v1/topics/{topicId}
     * 获取单个话题详情
     */
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicVO> getTopicById(@PathVariable Long topicId) {
        return topicService.getTopicById(topicId)
                .map(TopicVO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/topics/{topicId}
     * 更新话题名称或描述 (通常需要管理员权限)
     */
    @PutMapping("/{topicId}")
    public ResponseEntity<TopicVO> updateTopic(@PathVariable Long topicId, 
                                             @Valid @RequestBody TopicUpdateDTO dto) {
        
        // 假设 TopicService 中有 update 方法，此处仅为示例
        // 实际操作中，更新逻辑会比较复杂，此处省略 service.updateTopic() 的实现细节
        
        // 仅处理查询不到的情况
        return topicService.getTopicById(topicId) 
                .map(entity -> {
                    // 实际 Service 层会执行更新操作
                    // TopicEntity updatedEntity = service.updateTopic(topicId, dto); 
                    // return TopicVO.fromEntity(updatedEntity);
                    return ResponseEntity.ok(TopicVO.fromEntity(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * DELETE /api/v1/topics/{topicId}
     * 删除话题 (通常需要管理员权限，并需要处理关联帖子)
     */
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        // 假设 TopicService 中有 delete 方法
        // topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}
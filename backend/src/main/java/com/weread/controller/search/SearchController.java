package com.weread.controller.search;

import com.weread.dto.search.SearchResponseDto;
import com.weread.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/search")
@Slf4j

public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam String keyword) {
        try {
            SearchResponseDto response = searchService.search(keyword);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索失败，关键词: {}", keyword, e);

            // 返回详细的错误信息
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "搜索失败: " + e.getMessage());
            errorResponse.put("error", e.getClass().getName());
            errorResponse.put("timestamp", LocalDateTime.now());

            // 如果是特定异常，添加更多信息
            if (e.getCause() != null) {
                errorResponse.put("cause", e.getCause().getMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
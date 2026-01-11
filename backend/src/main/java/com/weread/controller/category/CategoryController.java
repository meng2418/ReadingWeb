package com.weread.controller.category;

import com.weread.common.ApiResponse;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "分类", description = "分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{categoryId}/books")
    @Operation(summary = "获取分类下的书籍（最多50本）", description = "获取指定分类下的书籍列表，支持分页，最多返回50本")
    public ApiResponse<Map<String, Object>> getCategoryBooks(
            @Parameter(description = "分类ID", required = true, example = "1") @PathVariable Integer categoryId,

            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页数量，最多50本", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

        try {
            // 限制每页最多50本
            int validLimit = Math.min(limit, 50);

            log.info("获取分类书籍 - categoryId: {}, page: {}, limit: {}", categoryId, page, validLimit);

            Map<String, Object> result = categoryService.getCategoryBooks(categoryId, page, validLimit);
            return ApiResponse.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("参数错误: {}", e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取分类书籍失败", e);
            return ApiResponse.error(500, "获取分类书籍失败: " + e.getMessage());
        }
    }
}
package com.weread.controller.category;

import com.weread.common.ApiResponse;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.service.category.CategoryService;
import com.weread.service.list.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "分类", description = "分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;
    private final RankingService rankingService;

    // 特殊分类标识符（榜单类型）
    private static final List<String> SPECIAL_CATEGORIES = List.of("weekly", "monthly", "new", "masterpiece");

    @GetMapping("/{categoryId}/books")
    @Operation(summary = "获取分类下的书籍（最多50本）", description = "获取指定分类下的书籍列表，支持分页，最多返回50本。支持特殊分类：weekly(周榜)、monthly(月榜)、new(新书榜)、masterpiece(神作榜)")
    public ApiResponse<Map<String, Object>> getCategoryBooks(
            @Parameter(description = "分类ID或特殊标识符（weekly/monthly/new/masterpiece）", required = true, example = "1") @PathVariable String categoryId,

            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页数量，最多50本", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

        try {
            // 限制每页最多50本
            int validLimit = Math.min(limit, 50);
            int validPage = Math.max(page, 1);

            log.info("获取分类书籍 - categoryId: {}, page: {}, limit: {}", categoryId, page, validLimit);

            Map<String, Object> result;

            // 检查是否是特殊分类（榜单类型）
            String lowerCategoryId = categoryId.toLowerCase();
            if (SPECIAL_CATEGORIES.contains(lowerCategoryId)) {
                // 处理特殊分类（榜单）
                List<SimpleBookDTO> rankingBooks = rankingService.getRankingBooks(lowerCategoryId);
                
                // 应用分页逻辑
                int startIndex = (validPage - 1) * validLimit;
                int endIndex = Math.min(startIndex + validLimit, rankingBooks.size());
                List<SimpleBookDTO> pagedBooks = startIndex < rankingBooks.size() 
                    ? rankingBooks.subList(startIndex, endIndex) 
                    : new ArrayList<>();

                result = new HashMap<>();
                result.put("books", pagedBooks);
                result.put("total", rankingBooks.size());
                result.put("page", validPage);
                result.put("limit", validLimit);
                result.put("totalPages", (int) Math.ceil((double) rankingBooks.size() / validLimit));
                result.put("hasNext", endIndex < rankingBooks.size());
                result.put("hasPrevious", validPage > 1);
            } else {
                // 尝试解析为整数ID
                try {
                    Integer intCategoryId = Integer.parseInt(categoryId);
                    result = categoryService.getCategoryBooks(intCategoryId, validPage, validLimit);
                } catch (NumberFormatException e) {
                    log.warn("无效的分类ID: {}", categoryId);
                    return ApiResponse.error(400, "无效的分类ID: " + categoryId);
                }
            }

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
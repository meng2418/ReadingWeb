package com.weread.service.category;

import java.util.Map;

public interface CategoryService {

    /**
     * 获取分类下的书籍列表
     * 
     * @param categoryId 分类ID
     * @param page       页码（从1开始）
     * @param limit      每页数量（最多50）
     * @return 包含书籍列表、总数、页码等信息
     */
    Map<String, Object> getCategoryBooks(Integer categoryId, Integer page, Integer limit);
}

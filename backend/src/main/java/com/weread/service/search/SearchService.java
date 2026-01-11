package com.weread.service.search;

import com.weread.dto.search.SearchResponseDto;
import com.weread.dto.book.SimpleBookDTO;
import com.weread.dto.search.AuthorSearchResultDto;

import java.util.List;

public interface SearchService {

    /**
     * 综合搜索（同时搜索书籍和作者）
     */
    SearchResponseDto search(String keyword);

    /**
     * 只搜索书籍（如果需要）
     */
    List<SimpleBookDTO> searchBooks(String keyword);

    /**
     * 只搜索作者（如果需要）
     */
    List<AuthorSearchResultDto> searchAuthors(String keyword);
}
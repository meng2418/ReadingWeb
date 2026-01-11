package com.weread.dto.search;

import com.weread.dto.search.AuthorSearchResultDto;
import com.weread.dto.book.SimpleBookDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "搜索响应结果")
public class SearchResponseDto {

    @Schema(description = "书籍搜索结果")
    private List<SimpleBookDTO> books;

    @Schema(description = "作者搜索结果")
    private List<AuthorSearchResultDto> authors;
}

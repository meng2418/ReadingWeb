// BookSearchResponseDTO.java
package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchResponseDTO {
    private List<BookSearchResultDTO> books;
    private String nextCursor;
    private Boolean hasMore;
}

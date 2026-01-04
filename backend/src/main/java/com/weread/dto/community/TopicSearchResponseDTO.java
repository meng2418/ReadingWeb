package com.weread.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicSearchResponseDTO {
    private List<TopicSearchResultDTO> topics;
    private String nextCursor;
    private Boolean hasMore;
}
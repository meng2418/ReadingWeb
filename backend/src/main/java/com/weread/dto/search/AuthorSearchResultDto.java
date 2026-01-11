package com.weread.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "作者搜索结果")
public class AuthorSearchResultDto {

    @Schema(description = "作者头像URL", format = "uri")
    private String avatar;

    @Schema(description = "粉丝数量")
    @JsonProperty("followerCount")
    private Integer followerCount;

    @Schema(description = "作者姓名")
    @JsonProperty("authorName")
    private String authorName;

    @Schema(description = "代表作列表")
    @JsonProperty("representativeWorks")
    private List<String> representativeWorks;

    @Schema(description = "作者简介")
    @JsonProperty("authorBio")
    private String authorBio;
}
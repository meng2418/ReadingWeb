package com.weread.vo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostListVO {
    private List<PostVO> posts;
    private Long total;
    private int page;
    private int limit;
}
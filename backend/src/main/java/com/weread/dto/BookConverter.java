package com.weread.dto;

import com.weread.vo.bookshelf.BookShelfVO;
import java.util.List; // 添加这个导入
import java.util.stream.Collectors; // 添加这个导入
import com.weread.dto.book.SimpleBookDTO;

public class BookConverter {

    public static SimpleBookDTO toSimpleBookDTO(BookShelfVO vo) {
        SimpleBookDTO dto = new SimpleBookDTO();
        dto.setBookId(vo.getBookId());
        dto.setBookTitle(vo.getTitle());
        dto.setAuthorName(vo.getAuthor());
        dto.setCover(vo.getCoverUrl());
        dto.setReadingStatus(vo.getStatus());
        return dto;
    }

    // 批量转换
    public static List<SimpleBookDTO> toSimpleBookDTOList(List<BookShelfVO> vos) {
        return vos.stream()
                .map(BookConverter::toSimpleBookDTO)
                .collect(Collectors.toList());
    }
}

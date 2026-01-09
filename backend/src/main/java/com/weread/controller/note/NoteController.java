package com.weread.controller.note;

import com.weread.dto.Result;
import com.weread.dto.note.NoteCreateDTO;
import com.weread.dto.note.NoteCreateResponseDTO;
import com.weread.dto.note.NoteResponseDTO;
import com.weread.service.note.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 笔记控制器
 */
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "阅读器", description = "笔记的创建、查询、删除")
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "发布笔记", description = "用户发布阅读笔记")
    public ResponseEntity<Result<NoteCreateResponseDTO>> createNote(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody NoteCreateDTO dto) {
        
        NoteResponseDTO noteResponse = noteService.createNoteFromDTO(
                userId,
                dto.getBookId(),
                dto.getChapterId(),
                dto.getQuote(),
                dto.getLineType() != null ? dto.getLineType() : "marker",
                dto.getThought()
        );
        
        // 使用专门的响应DTO，确保data字段是object类型
        NoteCreateResponseDTO response = new NoteCreateResponseDTO();
        response.setNote(noteResponse);
        
        // HTTP状态码为201（CREATED），但响应体中的code字段应为200（成功）
        Result<NoteCreateResponseDTO> result = Result.success(response);
        // 显式确保code字段为200，符合枚举值规范（200、400、401）
        result.setCode(200);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}


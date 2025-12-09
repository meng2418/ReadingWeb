package com.weread.controller;

import com.weread.dto.Result;
import com.weread.dto.bookshelf.*;
import com.weread.service.BookshelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ���ģ�����������������棩
 * ���нӿ���Ӧ��ʽͳһΪ Result ���ͣ�����ȫ���쳣����
 */
@RestController
@RequestMapping("/api/bookshelf")
@RequiredArgsConstructor
@Tag(name = "���", description = "���ģ����ؽӿ�")
@SecurityRequirement(name = "bearerAuth") // ��JWT��֤
public class BookshelfController {

    private final BookshelfService bookshelfService;

    /**
     * ��ȡ����鼮�б���֧�ְ�״̬ɸѡ��
     */
    @GetMapping
    @Operation(summary = "��ȡ���", description = "��ѯ�û�����е��鼮���ɰ�״̬ɸѡ��all/unread/reading/finished��")
    public Result<Map<String, Object>> getBookshelf(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader("userId") Long userId) {

        // ������ѯ����DTO
        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        if (!"all".equals(status)) {
            dto.setStatus(status);
        }

        // ����Service��ѯ
        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);

        // ͳһ��Result��װ��Ӧ
        return Result.success(Map.of("books", books));
    }

    /**
     * �����鼮�����
     */
    @PostMapping
    @Operation(summary = "���ӵ����", description = "��ָ���鼮���ӵ��û���ܣ�Ĭ��״̬Ϊδ��")
    public Result<Void> addToBookshelf(
            @RequestBody BookAddDTO dto,
            @RequestHeader("userId") Long userId) {

        // ����Service����
        bookshelfService.addBookToShelf(dto, userId);

        // �����ݷ��أ�����ʾ�ɹ�
        return Result.success();
    }

    /**
     * ������Ƴ��鼮
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "������Ƴ�", description = "��ָ���鼮���û������ɾ��")
    public Result<Void> removeFromBookshelf(
            @PathVariable Integer bookId,
            @RequestHeader("userId") Long userId) {

        // ����Service�Ƴ�
        bookshelfService.removeBookFromShelf(bookId, userId);

        return Result.success();
    }

    /**
     * �����鼮�Ķ�״̬
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "�����Ķ�״̬", description = "�޸�������鼮���Ķ�״̬��unread/reading/finished��")
    public Result<Void> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody BookStatusUpdateDTO dto,
            @RequestHeader("userId") Long userId) {

        // ��װ״̬����DTO
        BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
        statusDTO.setBookId(bookId);
        statusDTO.setStatus(dto.getStatus());

        // ����Service����
        bookshelfService.updateBookStatus(statusDTO, userId);

        return Result.success();
    }

    /**
     * �������������ɾ��/����״̬��
     */
    @PostMapping("/batch")
    @Operation(summary = "��������", description = "����ɾ������鼮�����������Ķ�״̬")
    public Result<Void> batchOperation(
            @RequestBody BookshelfBatchDTO dto,
            @RequestHeader("userId") Long userId) {

        // ����ɾ��
        if ("delete".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                bookshelfService.removeBookFromShelf(bookId, userId);
            }
        }
        // ��������״̬
        else if ("update-status".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
                statusDTO.setBookId(bookId);
                statusDTO.setStatus(dto.getStatus());
                bookshelfService.updateBookStatus(statusDTO, userId);
            }
        }
        // ��֧�ֵĲ������ͣ��ᱻȫ���쳣����������
        else {
            throw new RuntimeException("��֧�ֵĲ������ͣ�" + dto.getAction());
        }

        return Result.success();
    }
}
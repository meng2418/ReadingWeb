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
 * Bookshelf Module Controller.
 * All interface return results are encapsulated with Result<T>.
 */
@RestController
@RequestMapping("/api/bookshelf")
@RequiredArgsConstructor
@Tag(name = "Bookshelf", description = "Interfaces for bookshelf management")
@SecurityRequirement(name = "bearerAuth") // Require JWT authentication
public class BookshelfController {

    private final BookshelfService bookshelfService;

    /**
     * Retrieve the user's bookshelf list, supports filtering by read status.
     */
    @GetMapping
    @Operation(summary = "Get Bookshelf", description = "Query books in the user's bookshelf, filterable by status (all/unread/reading/finished)")
    public Result<Map<String, Object>> getBookshelf(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestHeader("userId") Long userId) {

        BookshelfQueryDTO dto = new BookshelfQueryDTO();
        if (!"all".equals(status)) {
            dto.setStatus(status);
        }

        List<BookShelfVO> books = bookshelfService.getUserBooks(dto, userId);

        return Result.success(Map.of("books", books));
    }

    /**
     * Add a book to the bookshelf.
     */
    @PostMapping
    @Operation(summary = "Add to Bookshelf", description = "Add a specified book to the user's bookshelf (default status: unread)")
    public Result<Void> addToBookshelf(
            @RequestBody BookAddDTO dto,
            @RequestHeader("userId") Long userId) {

        bookshelfService.addBookToShelf(dto, userId);

        return Result.success();
    }

    /**
     * Remove a book from the bookshelf.
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove from Bookshelf", description = "Delete the specified book from the user's bookshelf")
    public Result<Void> removeFromBookshelf(
            @PathVariable Integer bookId,
            @RequestHeader("userId") Long userId) {

        bookshelfService.removeBookFromShelf(bookId, userId);

        return Result.success();
    }

    /**
     * Update the book's reading status.
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "Update Reading Status", description = "Modify the reading status of a book in the bookshelf (unread/reading/finished)")
    public Result<Void> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody BookStatusUpdateDTO dto,
            @RequestHeader("userId") Long userId) {

        BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
        statusDTO.setBookId(bookId);
        statusDTO.setStatus(dto.getStatus());

        bookshelfService.updateBookStatus(statusDTO, userId);

        return Result.success();
    }

    /**
     * Batch operation (delete or update status).
     */
    @PostMapping("/batch")
    @Operation(summary = "Batch Operation", description = "Batch delete books or batch update reading status")
    public Result<Void> batchOperation(
            @RequestBody BookshelfBatchDTO dto,
            @RequestHeader("userId") Long userId) {

        // Batch delete
        if ("delete".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                bookshelfService.removeBookFromShelf(bookId, userId);
            }
        }
        // Batch update status
        else if ("update-status".equals(dto.getAction())) {
            for (Integer bookId : dto.getBookIds()) {
                BookStatusUpdateDTO statusDTO = new BookStatusUpdateDTO();
                statusDTO.setBookId(bookId);
                statusDTO.setStatus(dto.getStatus());
                bookshelfService.updateBookStatus(statusDTO, userId);
            }
        }
        // Unsupported operation type
        else {
            throw new RuntimeException("Unsupported operation type: " + dto.getAction());
        }

        return Result.success();
    }
}
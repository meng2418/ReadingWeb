package com.weread.controller.reading;

import com.weread.dto.Result;
import com.weread.dto.reading.ReadingRecordDTO;
import com.weread.service.reading.ReadingService;
import com.weread.vo.reading.ReadingStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 阅读控制器
 */
@RestController
@RequestMapping("/api/v1/reading")
@RequiredArgsConstructor
@Tag(name = "阅读管理", description = "在线阅读、阅读进度、阅读统计")
public class ReadingController {

    private final ReadingService readingService;

    @PostMapping("/progress")
    @Operation(summary = "记录阅读进度", description = "记录用户阅读进度")
    public Result<Void> recordProgress(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ReadingRecordDTO dto) {
        readingService.recordReadingProgress(userId, dto);
        return Result.success();
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取阅读统计", description = "获取用户的阅读统计数据")
    public Result<ReadingStatVO> getStatistics(@RequestAttribute("userId") Long userId) {
        return Result.success(readingService.getReadingStatistics(userId));
    }

    @PostMapping("/duration")
    @Operation(summary = "记录阅读时长", description = "记录用户阅读时长（分钟）")
    public Result<Void> recordDuration(
            @RequestAttribute("userId") Long userId,
            @RequestParam Integer minutes) {
        readingService.recordReadingDuration(userId, minutes);
        return Result.success();
    }
}


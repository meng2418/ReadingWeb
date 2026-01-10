package com.weread.service.note;

import com.weread.dto.note.NoteResponseDTO;
import com.weread.dto.note.UserNotesResponseDTO;
import com.weread.entity.note.NoteEntity;
import com.weread.vo.note.NoteVO;
import com.weread.vo.user.HighlightVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService {

    /**
     * 创建一篇新笔记或高亮
     * @param userId 笔记作者ID
     * @param noteEntity 笔记内容实体
     * @return 创建的笔记实体
     */
    NoteEntity createNote(Integer userId, NoteEntity noteEntity);

    /**
     * 发布笔记（根据API规范）
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param chapterId 章节ID（可选）
     * @param quote 引用内容
     * @param lineType 划线类型（marker, wavy, underline）
     * @param thought 想法/笔记内容（可选）
     * @return 笔记响应DTO
     */
    NoteResponseDTO createNoteFromDTO(Integer userId, Integer bookId, Integer chapterId, 
                                      String quote, String lineType, String thought);

    /**
     * 更新笔记内容或公开状态（只允许作者修改）
     */
    NoteEntity updateNote(Integer noteId, Integer userId, String content, Boolean isPublic, String color);

    /**
     * 删除笔记（只允许作者删除）
     */
    void deleteNote(Integer noteId, Integer userId);

    /**
     * 分页查询用户的个人笔记列表
     */
    Page<NoteVO> getUserNotes(Integer userId, Pageable pageable);

    /**
     * 游标分页查询用户的笔记瀑布流
     * @param userId 用户ID
     * @param cursor 游标（noteId），第一次请求为null
     * @param limit 每页数量
     * @return 笔记瀑布流响应DTO
     */
    UserNotesResponseDTO getUserNotesWithCursor(Integer userId, Integer cursor, Integer limit);

    /**
     * 获取某章节的所有公开笔记
     */
    List<NoteVO> getPublicChapterNotes(Integer chapterId);

    /**
     * 获取指定用户、指定书籍和章节的笔记列表
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param chapterId 章节ID
     * @return 章节笔记响应DTO列表
     */
    List<com.weread.dto.note.ChapterNoteResponseDTO> getChapterNotes(Integer userId, Integer bookId, Integer chapterId);

    /**
     * 获取指定用户、指定书籍的所有笔记列表（全书笔记）
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 全书笔记响应DTO列表
     */
    List<com.weread.dto.note.BookNoteResponseDTO> getBookNotes(Integer userId, Integer bookId);

    /**
     * 获取用户最新的6条笔记
     * @param userId 用户ID
     * @return 最新6条笔记DTO列表
     */
    List<com.weread.dto.note.BookNoteDTO> getUserRecentNotes6(Integer userId);

    /**
     * 获取用户最新的3条划线
     * @param userId 用户ID
     * @return 最新3条划线VO列表
     */
    List<HighlightVO> getUserRecentHighlights3(Integer userId);

    /**
     * 获取用户的笔记总数
     * @param userId 用户ID
     * @return 笔记总数
     */
    Integer getUserNoteCount(Integer userId);
}
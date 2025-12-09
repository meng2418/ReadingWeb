package com.weread.service;

import com.weread.dto.bookshelf.*;
import java.util.List;

/**
 * ���ģ��ҵ��ӿ�
 */
public interface BookshelfService {

    /**
     * �����鼮�����
     * 
     * @param dto    �����鼮ID�ͳ�ʼ״̬
     * @param userId ��ǰ�û�ID
     * @return ���ӽ��VO
     */
    BookAddVO addBookToShelf(BookAddDTO dto, Long userId);

    /**
     * ������Ƴ��鼮
     * 
     * @param bookId �鼮ID
     * @param userId ��ǰ�û�ID
     * @return �Ƴ������ʾ
     */
    String removeBookFromShelf(Integer bookId, Long userId);

    /**
     * �����鼮�Ķ�״̬
     * 
     * @param dto    �����鼮ID����״̬
     * @param userId ��ǰ�û�ID
     * @return ״̬���½��VO
     */
    BookStatusVO updateBookStatus(BookStatusUpdateDTO dto, Long userId);

    /**
     * �����Ķ����ȣ��½ڡ�ҳ�룩
     * 
     * @param dto    �����鼮ID���½�������ҳ��
     * @param userId ��ǰ�û�ID
     * @return ���ȸ��½��VO
     */
    ReadingProgressVO updateReadingProgress(ReadingProgressDTO dto, Long userId);

    /**
     * ��ҳ��ѯ�û�����е��鼮��֧�ְ�״̬ɸѡ��
     * 
     * @param dto    ����״̬ɸѡ�����ͷ�ҳ����
     * @param userId ��ǰ�û�ID
     * @return �鼮�б�VO������ҳ��Ϣ��
     */
    List<BookShelfVO> getUserBooks(BookshelfQueryDTO dto, Long userId);
}
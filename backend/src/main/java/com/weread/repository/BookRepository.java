package com.weread.repository;

import com.weread.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * �鼮���ݷ��ʽӿ�
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    /**
     * �����鼮ID��ѯ�鼮
     * �����ģ����ķ���������/��ѯ�鼮ʱ����֤�鼮�Ƿ���ڣ�
     */
    Optional<BookEntity> findByBookId(Integer bookId);

    /**
     * ����ISBN��ѯ�鼮������У���鼮Ψһ�ԣ�
     */
    Optional<BookEntity> findByIsbn(String isbn);
}
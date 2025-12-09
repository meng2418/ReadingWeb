package com.weread.repository;

import com.weread.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Book Data Access Interface.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    /**
     * Finds a book by its Book ID.
     * This method can be used to validate if a book exists when adding/querying a book.
     */
    Optional<BookEntity> findByBookId(Integer bookId);

    /**
     * Finds a book by its ISBN (used for unique validation).
     */
    Optional<BookEntity> findByIsbn(String isbn);
}
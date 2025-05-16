package com.lighthouse.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lighthouse.library.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository <Book, Long> {

}

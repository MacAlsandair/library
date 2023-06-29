package com.macalsandair.library.book;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

	Optional<Book> findBookById(Long id);
	
	Optional<Book> findByTitleAndAuthor(String title, String author);
}

package com.macalsandair.library.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
	public Book findBookById (Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book by id " + id + " was not found"));
	}
}

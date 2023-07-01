package com.macalsandair.library.book;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
    public List<Book> searchBook(String searchText) {
        return bookRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(searchText, searchText);
    }
	
	public Book findBookById (Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("Book by id " + id + " was not found"));
	}
}

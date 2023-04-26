package com.macalsandair.library.book;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController {
	
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private BookService bookService;
	
	@GetMapping("/all")
	public ResponseEntity<List<Book>> getAllBooks() {
		List<Book> books = bookRepository.findAll();
		return new ResponseEntity<List<Book>>(books, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<Book> addBook(@RequestBody Book book) {
		bookRepository.save(book);
		return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Book> updateBook(@RequestBody Book book) {
		bookRepository.save(book);
		return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	}
	
	@GetMapping("find/{id}")
	public ResponseEntity<Book> findBookById(@PathVariable("id") Long id) {
		Optional<Book> findedBook = bookRepository.findById(id);			
	}
	
	@Transactional
	@DeleteMapping("delete/{id}")
	
	
}

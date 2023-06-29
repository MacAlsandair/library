package com.macalsandair.library.book;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.auth.Roles;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

//@RolesAllowed("ADMINISTRATOR")
//@PreAuthorize("hasRole('ADMINISTRATOR')")
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
	    if (!bookRepository.existsById(book.getId())) {
	        bookRepository.save(book);
	        return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	    } else {
	        return new ResponseEntity<Book>(HttpStatus.CONFLICT);
	    }
	}
	
	@PutMapping("/update")
	public ResponseEntity<Book> updateBook(@RequestBody Book book) {
		bookRepository.save(book);
		return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	}
	
	@GetMapping("find/{id}")
	public ResponseEntity<Book> findBookById(@PathVariable("id") Long id) {
		Book findedBook = bookService.findBookById(id);
		return new ResponseEntity<>(findedBook, HttpStatus.OK);
	}
	
	@Transactional
	@DeleteMapping("delete/{id}")
	public ResponseEntity<?> deleteBookById(@PathVariable("id") Long id) {
		bookRepository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}

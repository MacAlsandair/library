package com.macalsandair.library.book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.macalsandair.library.auth.Role;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/book")
public class BookController {
	@Autowired
	private BookService bookService;
	
	@GetMapping("/all")
	public ResponseEntity<List<Book>> getAllBooks() {
		List<Book> books = bookService.getAllBooks();
		return new ResponseEntity<List<Book>>(books, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/add")
	public ResponseEntity<Book> addBook(@RequestBody Book book) {
	    Book savedBook = bookService.addBook(book);
	    return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/update")
	public ResponseEntity<Book> updateBook(@RequestBody Book book) {
		Book updatedBook = bookService.updateBook(book);
		return new ResponseEntity<Book>(updatedBook, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/add-with-image")
	public ResponseEntity<Book> addBook(@RequestPart("book") Book book, @RequestPart("image") MultipartFile imageFile) {
		Book savedBook = bookService.addBookWithImage(book, imageFile);
		return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/update-with-image")
	public ResponseEntity<Book> updateBook(@RequestPart("book") Book book, @RequestPart("image") MultipartFile imageFile) {
		Book updatedBook = bookService.updateBookWithImage(book, imageFile);
		return new ResponseEntity<>(updatedBook, HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/update-image/{id}")
	public ResponseEntity<Book> updateBookImage(@PathVariable("id") Long id, @RequestPart("image") MultipartFile imageFile) {
	    Book updatedBook = bookService.updateBookImage(id, imageFile);
	    return new ResponseEntity<>(updatedBook, HttpStatus.CREATED);
	}

	@GetMapping("find/{id}")
	public ResponseEntity<Book> findBookById(@PathVariable("id") Long id) {
		Book findedBook = bookService.findBookById(id);
		return new ResponseEntity<>(findedBook, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@Transactional
	@DeleteMapping("delete/{id}")
	public ResponseEntity<?> deleteBookById(@PathVariable("id") Long id) {
		bookService.deleteBookById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/search/{text}")
	public ResponseEntity<List<Book>> searchBook(@PathVariable("text") String text) {
		List<Book> books = bookService.searchBook(text);
		if (books.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(books, HttpStatus.OK);
	}
	
}

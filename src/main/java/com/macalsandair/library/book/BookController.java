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
import com.macalsandair.library.auth.Roles;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

//@RolesAllowed("ADMINISTRATOR")
//@PreAuthorize("hasRole('ADMINISTRATOR')")
@RestController
@RequestMapping("/api/book")
public class BookController {
	
	private Cloudinary cloudinary = new Cloudinary(
			ObjectUtils.asMap("cloud_name", "dl1raltsg", 
								"api_key", "883681461635441", 
								"api_secret", "4rpQSvE3KUuYK4rISWlLp63haio"));
	
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
	    Optional<Book> existingBook = bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor());

	    if (!existingBook.isPresent()) {
	        bookRepository.save(book);
	        return new ResponseEntity<>(book, HttpStatus.CREATED);
	    } else {
	        return new ResponseEntity<>(HttpStatus.CONFLICT);
	    }
	}

	
	@PutMapping("/update")
	public ResponseEntity<Book> updateBook(@RequestBody Book book) {
		bookRepository.save(book);
		return new ResponseEntity<Book>(book, HttpStatus.CREATED);
	}
	
	@PostMapping("/add-with-image")
	public ResponseEntity<Book> addBook(@RequestPart("book") Book book, @RequestPart("image") MultipartFile imageFile) {
			try {
				// Upload image and get url
				File file = convert(imageFile);
				Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
				String url = (String) uploadResult.get("url");

				// Set book coverImageUrl
				book.setCoverImageUrl(url);

				bookRepository.save(book);
			} catch (IOException e) {
				//handle exception
			}

			return new ResponseEntity<>(book, HttpStatus.CREATED);
	}

	//Similar approach for updateBook
	@PutMapping("/update-with-image")
	public ResponseEntity<Book> updateBook(@RequestPart("book") Book book, @RequestPart("image") MultipartFile imageFile) {
			try {
				File file = convert(imageFile);
				Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
				String url = (String) uploadResult.get("url");

				book.setCoverImageUrl(url);

				bookRepository.save(book);
			} catch (IOException e) {
				//handle exception
			}

			return new ResponseEntity<>(book, HttpStatus.CREATED);
	}
	
	@PutMapping("/update-image/{id}")
	public ResponseEntity<Book> updateBookImage(@PathVariable("id") Long id, @RequestPart("image") MultipartFile imageFile) {
	    Optional<Book> existingBookOptional = bookRepository.findById(id);
	    
	    if (!existingBookOptional.isPresent()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } else {
	        Book existingBook = existingBookOptional.get();
	        try {
	            File file = convert(imageFile);
	            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
	            String url = (String) uploadResult.get("url");
	            existingBook.setCoverImageUrl(url);

	            bookRepository.save(existingBook);
	        } catch (IOException e) {
	            // handle exception
	        }
	        return new ResponseEntity<>(existingBook, HttpStatus.CREATED);
	    }
	}

	//Utility method to convert MultipartFile to File
	private File convert(MultipartFile file) throws IOException {
	    File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
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
	
	@GetMapping("/search/{text}")
	public ResponseEntity<List<Book>> searchBook(@PathVariable("text") String text) {
		List<Book> books = bookService.searchBook(text);
		if (books.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(books, HttpStatus.OK);
	}
	
	
}

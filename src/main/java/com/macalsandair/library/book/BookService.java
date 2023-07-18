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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
    private Cloudinary cloudinary = new Cloudinary(
			ObjectUtils.asMap("cloud_name", "dl1raltsg", 
								"api_key", "883681461635441", 
								"api_secret", "4rpQSvE3KUuYK4rISWlLp63haio"));

    public Book addBook(Book book) {
        Optional<Book> existingBook = bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor());

        if (existingBook.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists");
        }

        return bookRepository.save(book);
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public Book addBookWithImage(Book book, MultipartFile imageFile) {
        Book savedBook = null;
        try {
            savedBook = uploadImageAndSaveBook(book, imageFile);
        } catch (IOException e) {
        	throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cannot add image");
        }
        return savedBook;
    }

    public Book updateBookWithImage(Book book, MultipartFile imageFile) {
		Book updatedBook = null;
		try {
			updatedBook = uploadImageAndSaveBook(book, imageFile);
		} catch (IOException e) {
        	throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cannot update image");
		}
		return updatedBook;
    }

    public Book updateBookImage(Long id, MultipartFile imageFile) {
        Optional<Book> existingBookOptional = bookRepository.findById(id);

        if (!existingBookOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        Book existingBook = existingBookOptional.get();
		try {
			existingBook = uploadImageAndSaveBook(existingBook, imageFile);
		} catch (IOException e) {
        	throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cannot update image");
		}
        
		return existingBook;
    }

    private Book uploadImageAndSaveBook(Book book, MultipartFile imageFile) throws IOException {
        File file = convert(imageFile);
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        String url = (String) uploadResult.get("url");
        book.setCoverImageUrl(url);
        return bookRepository.save(book);
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
	
    public List<Book> searchBook(String searchText) {
        return bookRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(searchText, searchText);
    }
	
	public Book findBookById (Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book by id " + id + " was not found"));
	}

	public Book updateBook(Book book) {
		return bookRepository.save(book);
	}
	
	public void deleteBookById(Long id) {
		if (!bookRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book by id " + id + " was not found");
		}
		bookRepository.deleteById(id);
	}
}

package com.macalsandair.testlibrary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.book.BookService;

import jakarta.persistence.Convert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Cloudinary cloudinary;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void cleanup() {
        reset(bookRepository, cloudinary);
    }

    @Test
    public void whenAddBookValidBook_thenReturnSameBook() {
        // Arrange
        Book book = new Book("Book1", "Author1", (short) 2000, "Genre1");
        when(bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookService.addBook(book);

        // Assert
        assertEquals(book, result);
    }

    @Test
    public void whenAddBookExistingBook_thenThrowException() {
        // Arrange
        Book book = new Book("Book1", "Author1", (short) 2000, "Genre1");
        when(bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor())).thenReturn(Optional.of(book));

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> bookService.addBook(book));
    }

    @Test
    public void whenGetAllBooks_thenReturnListOfBooks() {
        // Arrange
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book1", "Author1", (short) 2000, "Genre1"));
        books.add(new Book("Book2", "Author2", (short) 2001, "Genre2"));
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(books, result);
    }

//    @Test
//    public void whenAddBookWithImageValidBook_thenReturnSameBookWithImageUrl() throws IOException, NoSuchFieldException, IllegalAccessException {
//        // Arrange
//        Book book = new Book("Book1", "Author1", (short) 2000, "Genre1");
//        MultipartFile imageFile = new MockMultipartFile("image.jpg", new byte[0]);
//        when(bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor())).thenReturn(Optional.empty());
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        Class<BookService> bookServiceClass = BookService.class;
//        Field cloudinaryField = bookServiceClass.getDeclaredField("cloudinary");
//        cloudinaryField.setAccessible(true);
//
//        Cloudinary cloudinary = mock(Cloudinary.class);
//        when(cloudinary.uploader()).thenReturn(mock(Uploader.class));
//        when(cloudinary.uploader().upload(any(File.class), anyMap())).thenReturn(Collections.singletonMap("url", "https://example.com/image.jpg"));
//
//        cloudinaryField.set(bookService, cloudinary);
//
//        // Act
//        Book result = bookService.addBookWithImage(book, imageFile);
//
//        // Assert
//        assertEquals(book, result);
//        assertEquals("https://example.com/image.jpg", book.getCoverImageUrl());
//    }


    @Test
    public void whenAddBookWithImageErrorUploadingImage_thenThrowException() throws IOException {
        // Arrange
        Book book = new Book("Book1", "Author1", (short) 2000, "Genre1");
        MultipartFile imageFile = new MockMultipartFile("image.jpg", new byte[0]);
        when(bookRepository.findByNameAndAuthor(book.getName(), book.getAuthor())).thenReturn(Optional.empty());
        //when(cloudinary.uploader().upload(any(File.class), anyMap())).thenThrow(new IOException());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> bookService.addBookWithImage(book, imageFile));
    }
    
    @Test
    void whenConvertMultipartFileToValidFile_thenFileIsConverted() throws IOException {
        // Arrange
        byte[] fileContent = {1, 2, 3};
        MockMultipartFile multipartFile = new MockMultipartFile("test.txt", "test.txt", "text/plain", fileContent);
        BookService bookService = new BookService();
        
        Method method;
		try {
			method = BookService.class.getDeclaredMethod("convert", MultipartFile.class);
	        method.setAccessible(true);
	        // Act
	        File result;
			try {
				result = (File) method.invoke(bookService, multipartFile);
		        // Assert
		        assertTrue(result.exists());
		        assertEquals("test.txt", result.getName());
		        assertEquals(fileContent.length, result.length());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//        method.invoke(bookService, multipartFile);


    }

    @Test
    void whenConvertEmptyMultipartFile_thenFileIsEmpty() throws IOException {
        // Arrange
        byte[] fileContent = {};
        MockMultipartFile multipartFile = new MockMultipartFile("empty.txt", "empty.txt", "text/plain", fileContent);
        BookService bookService = new BookService();
        
        Method method;
		try {
			method = BookService.class.getDeclaredMethod("convert", MultipartFile.class);
	        method.setAccessible(true);
	        // Act
	        File result;
			try {
				result = (File) method.invoke(bookService, multipartFile);
		        // Assert
		        assertTrue(result.exists());
		        assertEquals("empty.txt", result.getName());
		        assertEquals(fileContent.length, result.length());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    
    @Test
    void whenSearchBookWithValidText_thenReturnListOfBooks() {
        // Arrange
        String searchText = "Book1";
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(new Book("Book1", "Author1", (short) 2000, "Genre1"));
        when(bookRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(searchText, searchText)).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.searchBook(searchText);

        // Assert
        assertEquals(expectedBooks, result);
    }

    @Test
    void whenFindBookByIdWithValidId_thenReturnBook() {
        // Arrange
        Long id = 1L;
        Book expectedBook = new Book("Book1", "Author1", (short) 2000, "Genre1");
        when(bookRepository.findById(id)).thenReturn(Optional.of(expectedBook));

        // Act
        Book result = bookService.findBookById(id);

        // Assert
        assertEquals(expectedBook, result);
    }

    @Test
    void whenFindBookByIdWithInvalidId_thenThrowException() {
        // Arrange
        Long id = 999L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> bookService.findBookById(id));
    }

    @Test
    void whenUpdateBookWithValidBook_thenReturnUpdatedBook() {
        // Arrange
        Book originalBook = new Book("Book1", "Author1", (short) 2000, "Genre1");
        Book updatedBook = new Book("Book1 Updated", "Author1 Updated", (short) 2001, "Genre1 Updated");
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // Act
        Book result = bookService.updateBook(originalBook);

        // Assert
        assertEquals(updatedBook, result);
    }

    @Test
    void whenDeleteBookByIdWithValidId_thenBookIsDeleted() {
        // Arrange
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> bookService.deleteBookById(id));

        // Assert
        // Verify that the deleteById method was called in the repository
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    void whenDeleteBookByIdWithInvalidId_thenThrowException() {
        // Arrange
        Long id = 999L;
        when(bookRepository.existsById(id)).thenReturn(false);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> bookService.deleteBookById(id));
    }
    

}

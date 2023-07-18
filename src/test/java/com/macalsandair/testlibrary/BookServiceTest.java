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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

    // Add tests for other methods in the BookService class

}

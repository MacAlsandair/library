package com.macalsandair.testlibrary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.recommendation.BookRecommendationService;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class BookRecommendationServiceTest {

    @InjectMocks
    private BookRecommendationService bookRecommendationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Test
    public void whenValidUsername_thenUserShouldBeFound() {
        String john = "John";
        User johnUser = new User();
        johnUser.setUsername(john);

        when(userRepository.findByUsername(john)).thenReturn(Optional.of(johnUser));
        User found = bookRecommendationService.findUserByUsername(john);

        assertEquals(john, found.getUsername());
    }

    @Test
    public void whenInvalidUsername_thenThrowException() {
        String fakeUser = "notARealUser";

        when(userRepository.findByUsername(fakeUser)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bookRecommendationService.findUserByUsername(fakeUser));
    }

    @Test
    public void whenValidBookId_thenBookShouldBeFound() {
        Long id = 1L;
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        Book found = bookRecommendationService.findBookById(id);

        assertEquals(id, found.getId());
    }

    @Test
    public void whenInvalidBookId_thenThrowException() {
        Long id = -1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> bookRecommendationService.findBookById(id));
    }

    
    @Test
    public void whenAddFavoriteBook_thenBookShouldBeAdded() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        //user.addFavoriteBook(book);
        
        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        String response = bookRecommendationService.addFavoriteBook(id, user);
        
        assertEquals("Book added to favorites successfully", response);
    }

    @Test
    public void whenDeleteFavoriteBook_thenBookShouldBeRemoved() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        user.addFavoriteBook(book);
        
        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        String response = bookRecommendationService.deleteFavoriteBook(id, user);
        
        assertEquals("Book removed from favorites successfully", response);
    }

    @Test
    public void whenCheckFavoriteBook_thenShouldReturnBoolean() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        user.addFavoriteBook(book);
        
        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        Boolean response = bookRecommendationService.checkFavoriteBook(id, user);
        
        assertTrue(response);
    }
    
    @Test
    public void whenAddFavoriteBook_thatAlreadyInFavorites_thenThrowConflictException() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        user.addFavoriteBook(book);
        
        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        // Verifying that method throws ResponseStatusException 
        assertThrows(ResponseStatusException.class, () -> bookRecommendationService.addFavoriteBook(id, user));
    }

    @Test
    public void whenDeleteFavoriteBook_thatNotInFavorites_thenThrowConflictException() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        // Do not add book1 to user's favorites

        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        // Verifying that method throws ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> bookRecommendationService.deleteFavoriteBook(id, user));
    }

    @Test
    public void whenCheckFavoriteBook_thatNotInFavorites_thenShouldReturnFalse() {
        Long id = 1L;
        User user = new User();
        user.setUsername("John");
        Book book = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        book.setId(id);
        // Do not add book1 to user's favorites

        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        
        Boolean response = bookRecommendationService.checkFavoriteBook(id, user);
        
        // Verifying that favorite book check returns false
        assertFalse(response);
    }


    @Test
    public void whenRecommendBooks_thenShouldReturnNothing() {
        User user = new User();
        user.setUsername("John");
        Book book1 = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", (short) 1937, "Fantasy");
        user.addFavoriteBook(book1);
        user.addFavoriteBook(book2);
        
        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        
        Set<Book> response = bookRecommendationService.recommendBooks(user);
        
        assertEquals(0, response.size());
    }
    
    @Test
    public void whenRecommendBooks_thenReturnRecommendedBooks() {
        // Prepare data
        User user = new User();
        user.setUsername("John");

        Book book1 = new Book("Harry Potter", "J.K. Rowling", (short) 2000, "Fantasy");
        Book book2 = new Book("The Hobbit", "J.R.R. Tolkien", (short) 1937, "Fantasy");
        Book book3 = new Book("The Lord of the Rings", "J.R.R. Tolkien", (short) 1954, "Fantasy");
        Book book4 = new Book("The Silmarillion", "J.R.R. Tolkien", (short) 1977, "Fantasy");
        Book book5 = new Book("Unfinished Tales", "J.R.R. Tolkien", (short) 1980, "Fantasy");
        Book book6 = new Book("The Children of Húrin", "J.R.R. Tolkien", (short) 2007, "Fantasy");
        Book book7 = new Book("1984", "George Orwell", (short) 1932, "Dystopian");


        user.addFavoriteBook(book1);
        user.addFavoriteBook(book2);

        //when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3, book4, book5, book6, book7));

        // Call the method to test
        Set<Book> response = bookRecommendationService.recommendBooks(user);

        // Assert the result
        assertEquals(4, response.size());
        assertTrue(response.contains(book3));
        assertTrue(response.contains(book4));
        assertTrue(response.contains(book5));
    }


}

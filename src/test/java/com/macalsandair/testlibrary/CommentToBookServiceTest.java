package com.macalsandair.testlibrary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import com.macalsandair.library.LibraryApplication;
import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.comment.CommentToBook;
import com.macalsandair.library.comment.CommentToBookRepository;
import com.macalsandair.library.comment.CommentToBookService;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CommentToBookServiceTest {

    @Mock
    private CommentToBookRepository commentToBookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CommentToBookService commentToBookService;

    @Test
    public void saveComment_success(){
        //Setup data
        User user = new User();
        user.setUsername("username");
        Book book = new Book();
        book.setName("BookTitle");
        CommentToBook comment = new CommentToBook(user, book, "commentText");

        //Setup mocks
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(commentToBookRepository.save(any(CommentToBook.class))).thenReturn(comment);

        CommentToBook returnedComment = commentToBookService.saveComment(123L, "commentText", "username");

        //Assertion
        assertNotNull(returnedComment);
        assertEquals("commentText", returnedComment.getCommentText());
        assertEquals(user, returnedComment.getAuthor());
        assertEquals(book, returnedComment.getBook());
    }
    
    @Test
    public void saveComment_userNotFound(){
        //Setup mocks
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            commentToBookService.saveComment(123L, "commentText", "username");
        });
    }
    
    @Test
    public void updateComment_success(){
        //Setup data
        CommentToBook comment = new CommentToBook();
        comment.setId(1L);
        comment.setCommentText("Old Comment");

        //Setup mocks
        when(commentToBookRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentToBookRepository.save(any(CommentToBook.class))).thenAnswer(i -> i.getArguments()[0]);

        CommentToBook updatedComment = commentToBookService.updateComment(1L, "New Comment");

        //Assertion
        assertNotNull(updatedComment);
        assertEquals("New Comment", updatedComment.getCommentText());
    }

    @Test
    public void updateComment_notFound(){
        //Setup mocks
        when(commentToBookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            commentToBookService.updateComment(1L, "New Comment");
        });
    }
    
    @Test
    public void deleteComment_success(){
        //Setup data
        CommentToBook comment = new CommentToBook();
        comment.setId(1L);

        //Setup mocks
        when(commentToBookRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentToBookRepository).deleteById(1L);

        commentToBookService.deleteComment(1L);

        //Assertion
        verify(commentToBookRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteComment_notFound(){
        //Setup mocks
        when(commentToBookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            commentToBookService.deleteComment(1L);
        });
    }


    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L}) // Change to provide Author Ids since findByAuthor method in service uses Ids
    public void findByAuthor_success(Long authorId){
        // Create necessary objects for testing
        User author = new User();
        author.setId(authorId);
        author.setUsername("username");
        Book book = new Book();
        book.setName("BookTitle");
        CommentToBook comment = new CommentToBook(author, book, "CommentText");

        //Correct the mocking
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(commentToBookRepository.findByAuthor(author)).thenReturn(Arrays.asList(comment));

        List<CommentToBook> returnedComments = commentToBookService.findByAuthor(authorId);

        assertNotNull(returnedComments);
        assertEquals(1, returnedComments.size());
        assertEquals(authorId, returnedComments.get(0).getAuthor().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    public void findByBook_success(Long bookId){
        User author = new User();
        author.setUsername("username");
        Book book = new Book();
        book.setName("BookTitle");
        book.setId(bookId);
        CommentToBook comment = new CommentToBook(author, book, "CommentText");

        // Mock findById to return a book
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        // Mock findByBook to return a list of comments 
        when(commentToBookRepository.findByBook(book)).thenReturn(Collections.singletonList(comment));

        List<CommentToBook> returnedComments = commentToBookService.findByBook(bookId);

        assertNotNull(returnedComments);
        assertEquals(1, returnedComments.size());
        assertEquals(bookId, returnedComments.get(0).getBook().getId());
    }


    
}



//@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = LibraryApplication.class)
//public class CommentToBookServiceTest {
//
////    @InjectMocks
////    CommentToBookService commentToBookService;
//	
//    @Autowired
//    private CommentToBookService commentToBookService;
//
//    @MockBean
//    CommentToBookRepository commentToBookRepository;
//    @MockBean
//    UserRepository userRepository;
//    @MockBean
//    BookRepository bookRepository;
//
//    @Test
//    void saveCommentValidTest() {
//        String username = "user";
//        User user = new User();
//        user.setUsername(username);
//        Long bookId = 1L;
//        Book book = new Book();
//        book.setId(bookId);
//        String commentText = "Great Book!";
//
//        CommentToBook commentToBook = new CommentToBook();
//        commentToBook.setAuthor(user);
//        commentToBook.setBook(book);
//        commentToBook.setCommentText(commentText);
//
//        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
//        when(commentToBookRepository.save(commentToBook)).thenReturn(commentToBook);
//
//        CommentToBook result = commentToBookService.saveComment(bookId, commentText, username);
//        assertEquals(commentToBook, result);
//    }
//    
//    @Test
//    public void testSaveComment() {
//        // Given
//        Long bookId = 1L;
//        String username = "John";
//        String commentText = "Great book!";
//        User user = new User();
//        user.setUsername(username);
//        Book book = new Book();
//        book.setId(bookId);
//        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
//        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
//
//        // When
//        CommentToBook comment = commentToBookService.saveComment(bookId, commentText, username);
//
//        // Then
//        assertEquals(user, comment.getAuthor());
//        assertEquals(book, comment.getBook());
//        assertEquals(commentText, comment.getCommentText());
//        Mockito.verify(commentToBookRepository).save(comment);
//    }
//
//    // Add similar tests for updateUser, deleteUser, findByAuthor, findByBook
//}
//

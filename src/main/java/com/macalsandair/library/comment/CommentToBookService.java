package com.macalsandair.library.comment;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentToBookService {

    @Autowired
    private CommentToBookRepository commentToBookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    public CommentToBook saveComment(Long bookId, String commentText, String username) {
        CommentToBook comment = new CommentToBook();
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found: " + username));
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Book not found: " + bookId));
        comment.setAuthor(author);
        comment.setBook(book);
        comment.setCommentText(commentText);
        return commentToBookRepository.save(comment);
    }

    public CommentToBook updateComment(Long id, String commentText) {
        return commentToBookRepository.findById(id)
            .map(comment -> {
                comment.setCommentText(commentText);
                return commentToBookRepository.save(comment);
            }).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Comment not found: " + id));
    }

    public void deleteComment(Long id) {
        commentToBookRepository.deleteById(id);
    }

    public List<CommentToBook> findByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No author found with ID: " + authorId));
        return commentToBookRepository.findByAuthor(author);
    }

    public List<CommentToBook> findByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No book found with ID: " + bookId));
        return commentToBookRepository.findByBook(book);
    }
}

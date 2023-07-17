package com.macalsandair.library.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentToBookController {

    @Autowired
    private CommentToBookService commentToBookService;

    @PostMapping("/{bookId}")
    public CommentToBook addComment(@PathVariable Long bookId, @RequestBody Map<String, String> body) {
        String commentText = body.get("commentText");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return commentToBookService.saveComment(bookId, commentText, username);
    }

    @PutMapping("/{id}")
    public CommentToBook updateComment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String commentText = body.get("commentText");
        return commentToBookService.updateComment(id, commentText);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentToBookService.deleteComment(id);
    }

    @GetMapping("/author/{authorId}")
    public List<CommentToBook> findCommentsByAuthor(@PathVariable Long authorId) {
        return commentToBookService.findByAuthor(authorId);
    }

    @GetMapping("/book/{bookId}")
    public List<CommentToBook> findCommentsByBook(@PathVariable Long bookId) {
        return commentToBookService.findByBook(bookId);
    }
}

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

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentToBookController {

	@Autowired
	private CommentToBookService commentToBookService;

	private UserRepository userRepository;
	private BookRepository bookRepository;

	@PostMapping
	public CommentToBook addComment(@RequestBody CommentToBook comment) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		User author = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
		comment.setAuthor(author);
		return commentToBookService.saveComment(comment);
	}

	@PutMapping("/{id}")
	public CommentToBook updateComment(@PathVariable Long id, @RequestBody CommentToBook updatedComment) {
		return commentToBookService.updateComment(id, updatedComment);
	}

	@DeleteMapping("/{id}")
	public void deleteComment(@PathVariable Long id) {
		commentToBookService.deleteComment(id);
	}

	@GetMapping("/author/{authorId}")
	public ResponseEntity<?> findCommentsByAuthor(@PathVariable Long authorId) {
		Optional<User> author = userRepository.findById(authorId);
		if (author.isPresent()) {
			return new ResponseEntity<>(commentToBookService.findByAuthor(author.get()), HttpStatus.OK);
		}
		return new ResponseEntity<>("No author found with ID: " + authorId, HttpStatus.NOT_FOUND);
	}

	@GetMapping("/book/{bookId}")
	public ResponseEntity<?> findCommentsByBook(@PathVariable Long bookId) {
		Optional<Book> book = bookRepository.findById(bookId);
		if (book.isPresent()) {
			return new ResponseEntity<>(commentToBookService.findByBook(book.get()), HttpStatus.OK);
		}
		return new ResponseEntity<>("No book found with ID: " + bookId, HttpStatus.NOT_FOUND);
	}
}
package com.macalsandair.library.recommendation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.user.User;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class BookRecommendationController {

	@Autowired
	private BookRecommendationService bookRecommendationService;

	@PostMapping("/add/{id}")
	public ResponseEntity<String> addFavoriteBook(@PathVariable("id") Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = bookRecommendationService.findUserByUsername(username);
		String response = bookRecommendationService.addFavoriteBook(id, user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/remove/{id}")
	public ResponseEntity<String> deleteFavoriteBook(@PathVariable("id") Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = bookRecommendationService.findUserByUsername(username);
		String response = bookRecommendationService.deleteFavoriteBook(id, user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/checkFavorite/{id}")
	public ResponseEntity<Boolean> checkFavoriteBook(@PathVariable("id") Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = bookRecommendationService.findUserByUsername(username);
		boolean isFavorite = bookRecommendationService.checkFavoriteBook(id, user);
		HttpStatus status = isFavorite ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(isFavorite, status);
	}

	@GetMapping("/personal")
	public Set<Book> sendPersonalRecommendations() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = bookRecommendationService.findUserByUsername(username);
		return bookRecommendationService.recommendBooks(user);
	}
}

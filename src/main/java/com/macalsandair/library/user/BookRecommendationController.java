package com.macalsandair.library.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class BookRecommendationController {
	
  @Autowired
  private BookRecommendationService bookRecommendationService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BookRepository bookRepository;

  @PostMapping("/add/{id}")
  public void addFavoriteBook(@PathVariable("id") Long id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userRepository.findByUsername(username);
    Optional<Book> book = bookRepository.findById(id);

    if (book.isPresent() && !user.get().isFavoriteBook(book.get())) {
      user.get().addFavoriteBook(book.get());
      userRepository.save(user.get());
    }
  }

  @DeleteMapping("/remove/{id}")
  public void deleteFavoriteBook(@PathVariable("id") Long id) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userRepository.findByUsername(username);
    Optional<Book> book = bookRepository.findById(id);

    if (book.isPresent() && user.get().isFavoriteBook(book.get())) {
      user.get().removeFavoriteBook(book.get());
      userRepository.save(user.get());
    }
  }

  @GetMapping("/personal")
  public Set<Book> sendPersonalRecommendations() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Optional<User> user = userRepository.findByUsername(auth.getName());
    return bookRecommendationService.recommendBooks(user.get());
  }
}



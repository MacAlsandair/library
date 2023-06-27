package com.macalsandair.library.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macalsandair.library.book.Book;

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

  @PostMapping("/add")
  public void addFavoriteBook(@RequestBody Book book) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userRepository.findByUsername(username);
    if (!user.get().isFavoriteBook(book)) {
      user.get().addFavoriteBook(book);
      userRepository.save(user.get());
    }
  }

  @DeleteMapping("/remove")
  public void deleteFavoriteBook(@RequestBody Book book) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userRepository.findByUsername(username);
    if (user.get().isFavoriteBook(book)) {
      user.get().removeFavoriteBook(book);
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


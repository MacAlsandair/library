package com.macalsandair.library.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserFavoriteBook;
import com.macalsandair.library.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookRecommendationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
	}

	public Book findBookById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found"));
	}

	public String addFavoriteBook(Long id, User user) {
		Book book = findBookById(id);
		if (user.isFavoriteBook(book)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already in favorites");
		}
		user.addFavoriteBook(book);
		userRepository.save(user);
		return "Book added to favorites successfully";
	}

	public String deleteFavoriteBook(Long id, User user) {
		Book book = findBookById(id);
		if (!user.isFavoriteBook(book)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Book not found in favorites");
		}
		user.removeFavoriteBook(book);
		userRepository.save(user);
		return "Book removed from favorites successfully";
	}

	public Boolean checkFavoriteBook(Long id, User user) {
		Book book = findBookById(id);
		return user.isFavoriteBook(book);
	}

	public Set<Book> recommendBooks(User user) {
		Set<UserFavoriteBook> favoriteBooksLinks = user.getFavoriteBooks();

		Set<Book> favoriteBooks = favoriteBooksLinks.stream().map(UserFavoriteBook::getBook)
				.collect(Collectors.toSet());

		Set<String> favoriteAuthors = favoriteBooks.stream().map(Book::getAuthor).collect(Collectors.toSet());

		Set<String> favoriteGenres = favoriteBooks.stream().map(Book::getGenre).collect(Collectors.toSet());

		List<Short> favoriteYearsOfPublication = favoriteBooks.stream().map(Book::getYearOfPublication)
				.collect(Collectors.toList());

		List<Book> allBooks = bookRepository.findAll();

		return allBooks.stream().filter(book -> !favoriteBooks.contains(book)) // filter out books that are already
																				// favorited
				.filter(book -> favoriteAuthors.contains(book.getAuthor()) || favoriteGenres.contains(book.getGenre())
						|| favoriteYearsOfPublication.contains(book.getYearOfPublication())) // check if the book's
																								// author, genre or
																								// yearOfPublication is
																								// favored by user
				.limit(5) // limit to 5 books
				.collect(Collectors.toSet());
	}
}

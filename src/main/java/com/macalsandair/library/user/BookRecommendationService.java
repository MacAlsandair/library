package com.macalsandair.library.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.book.BookRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookRecommendationService {

	@Autowired
    private BookRepository bookRepository;


    public Set<Book> recommendBooks(User user) {
        Set<UserFavoriteBook> favoriteBooksLinks = user.getFavoriteBooks();

        Set<Book> favoriteBooks = favoriteBooksLinks.stream()
                .map(UserFavoriteBook::getBook)
                .collect(Collectors.toSet());

        Set<String> favoriteAuthors = favoriteBooks.stream()
                .map(Book::getAuthor)
                .collect(Collectors.toSet());

        Set<String> favoriteGenres = favoriteBooks.stream()
                .map(Book::getGenre)
                .collect(Collectors.toSet());

        List<Short> favoriteYearsOfPublication = favoriteBooks.stream()
                .map(Book::getYearOfPublication)
                .collect(Collectors.toList());

        List<Book> allBooks = bookRepository.findAll();

        return allBooks.stream()
                .filter(book -> !favoriteBooks.contains(book)) // filter out books that are already favorited
                .filter(book -> favoriteAuthors.contains(book.getAuthor()) 
                        || favoriteGenres.contains(book.getGenre())
                        || favoriteYearsOfPublication.contains(book.getYearOfPublication())) // check if the book's author, genre or yearOfPublication is favored by user
                .limit(5) // limit to 5 books 
                .collect(Collectors.toSet());
    }
}


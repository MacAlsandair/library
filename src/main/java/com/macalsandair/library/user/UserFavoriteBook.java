package com.macalsandair.library.user;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.macalsandair.library.book.Book;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="user_favorite_books")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFavoriteBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

	public UserFavoriteBook(User user, Book book) {
		super();
		this.user = user;
		this.book = book;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public UserFavoriteBook() {
		super();
	}

	@Override
	public int hashCode() {
		return Objects.hash(book, id, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserFavoriteBook other = (UserFavoriteBook) obj;
		return Objects.equals(book, other.book) && Objects.equals(id, other.id) && Objects.equals(user, other.user);
	}
	
	
    
}

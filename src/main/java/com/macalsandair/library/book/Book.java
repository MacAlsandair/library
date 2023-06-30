package com.macalsandair.library.book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.macalsandair.library.user.User;
import com.macalsandair.library.user.UserFavoriteBook;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "Book")
@Table(name = "book")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;
	
	private String name;
	private String author;
	private short yearOfPublication;
	private String genre;
	

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Set<UserFavoriteBook> userFavoriteBooks = new HashSet<>();

	public Set<UserFavoriteBook> getUserFavoriteBooks() {
		return userFavoriteBooks;
	}


	public void setUserFavoriteBooks(Set<UserFavoriteBook> userFavoriteBooks) {
		this.userFavoriteBooks = userFavoriteBooks;
	}


	public Book() {
		super();
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public short getYearOfPublication() {
		return yearOfPublication;
	}
	public void setYearOfPublication(short yearOfPublication) {
		this.yearOfPublication = yearOfPublication;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public Book(String name, String author, short yearOfPublication, String genre) {
		super();
		this.name = name;
		this.author = author;
		this.yearOfPublication = yearOfPublication;
		this.genre = genre;
	}


	@Override
	public int hashCode() {
		return Objects.hash(author, genre, id, name, yearOfPublication);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(author, other.author) && Objects.equals(genre, other.genre)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& yearOfPublication == other.yearOfPublication;
	}

	
	
}

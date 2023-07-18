package com.macalsandair.library.comment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.macalsandair.library.book.Book;
import com.macalsandair.library.user.User;

@Entity
@Table(name = "comments")
//@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class CommentToBook {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	//@JsonIgnore
	private User author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", nullable = false)
	//@JsonIgnore
	private Book book;

	@Column(name = "comment", nullable = false)
	private String commentText;

	@Column(name = "time_stamp", nullable = false)
	private LocalDateTime timeStamp;

	// constructors, getters, setters
	public CommentToBook() {
	}

	public CommentToBook(User author, Book book, String commentText, LocalDateTime timeStamp) {
		this.author = author;
		this.book = book;
		this.commentText = commentText;
		this.timeStamp = timeStamp;
	}

	public CommentToBook(User author, Book book, String commentText) {
		this.author = author;
		this.book = book;
		this.commentText = commentText;
	}

	public CommentToBook(Book book, String commentText) {
		this.book = book;
		this.commentText = commentText;
	}
	
	public CommentToBook(String commentText) {
		this.commentText = commentText;
	}

	public Long getId() {
		return id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

package com.macalsandair.library.book;

public class BookNotFoundException extends RuntimeException {
	
	public BookNotFoundException(String message) {
		super(message);
	}
}

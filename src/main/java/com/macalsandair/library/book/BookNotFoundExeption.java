package com.macalsandair.library.book;

public class BookNotFoundExeption extends RuntimeException {
	
	public BookNotFoundExeption(String message) {
		super(message);
	}
}

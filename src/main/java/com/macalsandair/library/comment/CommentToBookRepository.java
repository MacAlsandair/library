package com.macalsandair.library.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.user.User;

@Repository
public interface CommentToBookRepository extends JpaRepository<CommentToBook, Long> {

	public List<CommentToBook> findByAuthor(User author);

	public List<CommentToBook> findByBook(Book book);
}

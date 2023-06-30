package com.macalsandair.library.comment;

import com.macalsandair.library.book.Book;
import com.macalsandair.library.comment.CommentToBook;
import com.macalsandair.library.comment.CommentToBookRepository;
import com.macalsandair.library.user.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentToBookService {

  private final CommentToBookRepository commentToBookRepository;

  @Autowired
  public CommentToBookService(CommentToBookRepository commentToBookRepository) {
    this.commentToBookRepository = commentToBookRepository;
  }

  public CommentToBook saveComment(CommentToBook comment) {
    return commentToBookRepository.save(comment);
  }

  public CommentToBook updateComment(Long id, CommentToBook updatedComment) {
    return commentToBookRepository.findById(id).map(comment -> {
      comment.setCommentText(updatedComment.getCommentText());
      return commentToBookRepository.save(comment);
    }).orElseThrow(() -> new RuntimeException("Comment not found: " + id.toString()));
  }

  public void deleteComment(Long id) {
    commentToBookRepository.deleteById(id);
  }
  
  public List<CommentToBook> findByAuthor(User author) {
    return commentToBookRepository.findByAuthor(author);
  }
  
  public List<CommentToBook> findByBook(Book book) {
    return commentToBookRepository.findByBook(book);
  }
}

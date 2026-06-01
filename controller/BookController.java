package library_manage.controller;

import library_manage.Model.*;
import library_manage.services.*;
import library_manage.util.ServiceResult;

public class BookController {
    private final BookServices bookService;
    private final AuthorServices authorServices;

    public BookController(BookServices bookService) {
        this(bookService, new AuthorServices());
    }

    public BookController(BookServices bookService, AuthorServices authorServices) {
        this.bookService = bookService;
        this.authorServices = authorServices;
    }

    public ServiceResult addBook(String isbn, String title, String authorName, int copies) {
        Author author = authorServices.findOrCreateAuthor(authorName);
        Book book = new Book(title, isbn, author, copies);
        return bookService.addBook(book);
    }

    public ServiceResult searchBookbyIsbn(String isbn) {
        return bookService.searchBookByIsbn(isbn);
    }

    public ServiceResult searchBookByTitle(String title) {
        return bookService.searchBookByTitle(title);
    }

    public ServiceResult deleteBook(String isbn) {
        return bookService.deleteBook(isbn);
    }

    public ServiceResult addCopies(String isbn, int count) {
        return bookService.addCopies(isbn, count);
    }

    public ServiceResult reduceCopies(String isbn, int count) {
        return bookService.reduceCopies(isbn, count);
    }

    public ServiceResult getAllBooks() {
        return bookService.getAllBooks();
    }

}

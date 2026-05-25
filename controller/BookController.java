package library_manage.controller;

import library_manage.Model.*;
import library_manage.services.*;
import library_manage.util.ServiceResult;

public class BookController {
    private BookServices bookService;
    private AuthorServices auhtAuthorServices;

    public BookController(BookServices bookService) {
        this.bookService = bookService;
    }

    public String addBook(String isbn, String title, String authorName, int copies) {
        Author author = auhtAuthorServices.findOrCreateAuthor(authorName);
        Book book = new Book(isbn, title, author, copies);
        ServiceResult result = bookService.addBook(book);
        return result.getMessage();
    }

    public String searchBookbyIsbn(String isbn) {
        ServiceResult result = bookService.searchBookByIsbn(isbn);
        if (result.isSuccess()) {
            Book book = (Book) result.getData();
            return "Found: " + book.getTitle() + " by " + book.getAuthor().getName() +
                    " | Copies: " + book.getnumberOfCopies() + " | Borrowed: " + book.getBorrowedCount();
        }
        return result.getMessage();
    }

    public String searchBookByTitle(String title) {
        ServiceResult result = bookService.searchBookByTitle(title);
        if (result.isSuccess()) {
            Book book = (Book) result.getData();
            return "Found: " + book.getTitle() + " by " + book.getAuthor().getName() +
                    " | Copies: " + book.getnumberOfCopies() + " | Borrowed: " + book.getBorrowedCount();
        }
        return result.getMessage();
    }

}

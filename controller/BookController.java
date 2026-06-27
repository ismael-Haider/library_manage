package library_manage.controller;

import library_manage.Model.*;
import library_manage.services.*;
import library_manage.util.ServiceResult;

public class BookController {
    private final BookServices bookService;
    private final AuthorServices authorServices;
    private final BorrowServices borrowService;
// here i use this in every place just to don't assign to it AuthorServices to be this operation autumatic here by using this
    public BookController(BookServices bookService) {
        this(bookService, new AuthorServices(), new BorrowServices(bookService, new UserServices()));
    }

    public BookController(BookServices bookService, AuthorServices authorServices, BorrowServices borrowService) {
        this.bookService = bookService;
        this.authorServices = authorServices;
        this.borrowService = borrowService;
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
        if(borrowService.borrowRecords.stream().anyMatch(record -> record.getBook().getIsbn().equals(isbn) && !record.isReturned())) {
            return new ServiceResult(false, "Cannot delete book. There are unreturned copies.");
        }
        return bookService.deleteBook(isbn);
    }

    public ServiceResult addCopies(String isbn, int count) {
        ServiceResult result = bookService.addCopies(isbn, count);

        if (result.isSuccess() && !borrowService.waitingLists.isEmpty()) {
            result = borrowService.processWaitingList(isbn);
        }

        return result;
    }

    public ServiceResult reduceCopies(String isbn, int count) {
        return bookService.reduceCopies(isbn, count);
    }

    public ServiceResult getAllBooks() { // why this is exits here ? in class BookManagementPanel for [searchBooks() ,
                                         // loadTableData()]
        return bookService.getAllBooks();
    }

}

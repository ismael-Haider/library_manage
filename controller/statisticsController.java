package library_manage.controller;

import java.util.ArrayList;
import library_manage.Model.Author;
import library_manage.Model.Book;
import library_manage.services.AuthorServices;
import library_manage.services.BookServices;
import library_manage.util.ServiceResult;

public class statisticsController {
    private final BookServices bookServices;
    private final AuthorServices authorServices;

    public statisticsController(BookServices bookServices, AuthorServices authorServices) {
        this.bookServices = bookServices;
        this.authorServices = authorServices;
    }

    public ArrayList<Book> getMostBorrowedBooks() {
        ServiceResult result = bookServices.getMostBorrowedBooks();
        return (ArrayList<Book>) result.getData();
    }

    public ArrayList<Author> getMostReadersAuthors() {
        ServiceResult result = authorServices.getMostreadersAuthors();
        return (ArrayList<Author>) result.getData();
    }

    public int getNumberOfBooks() {
        ServiceResult result = bookServices.getAllBooks();
        ArrayList<Book> books = (ArrayList<Book>) result.getData();
        return books.size();
    }
}

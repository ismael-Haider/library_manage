package library_manage;

import library_manage.controller.BookController;
import library_manage.services.AuthorServices;
import library_manage.services.BookServices;
import library_manage.services.BorrowServices;
import library_manage.services.UserServices;

public class Main {
    public static void main(String[] args) {
        AuthorServices authorServices = new AuthorServices();
        BookServices bookServices = new BookServices(authorServices);
        UserServices userServices = new UserServices();
        BorrowServices borrowServices = new BorrowServices(bookServices, userServices);
        BookController bookController = new BookController(bookServices, authorServices);

        System.out.println("Library data loaded from txt files.");
        System.out.println("Authors: " + authorServices.getAllAuthors().size());
        System.out.println("Books: " + bookServices.getBookCount());
        System.out.println("Users: " + userServices.getUserCount());
        System.out.println("Borrow records: " + borrowServices.getBorrowRecordCount());
    }
}

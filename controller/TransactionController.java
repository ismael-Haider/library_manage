package library_manage.controller;

import library_manage.Model.User;
import library_manage.services.AuthorServices;
import library_manage.services.BookServices;
import library_manage.services.BorrowServices;
import library_manage.services.UserServices;
import library_manage.util.ServiceResult;

public class TransactionController {

    private final BookServices bookServices;
    private final AuthorServices authorServices;
    private final UserServices userServices;
    private final BorrowServices borrowServices;

    public TransactionController(BookServices bookServices, AuthorServices authorServices,
            UserServices userServices, BorrowServices borrowServices) {
        this.bookServices = bookServices;
        this.authorServices = authorServices;
        this.userServices = userServices;
        this.borrowServices = borrowServices;
    }

    public ServiceResult borrowBook(String userName, boolean graduate, String isbn) {
        User user = userServices.findOrCreateUser(userName, graduate);
        if (user == null) {
            return new ServiceResult(false, "User name cannot be empty");
        }
        return borrowServices.borrowBook(user, isbn);
    }

    public ServiceResult returnBook(String userName, String isbn) {
        ServiceResult userResult = userServices.searchUserByName(userName);
        if (!userResult.isSuccess()) {
            return userResult;
        }

        User user = (User) userResult.getData();
        return borrowServices.returnBook(user, isbn);
    }

    public ServiceResult getWaitingList(String isbn) {
        return borrowServices.showWaitingList(isbn);
    }

    public ServiceResult getBorrowHistory() {
        return borrowServices.getBorrowHistory();
    }

    public ServiceResult searchBorrowHistoryByUserName(String name) {
        ServiceResult userResult = userServices.searchUserByName(name);
        if (!userResult.isSuccess()) {
            return userResult;
        }
        User user = (User) userResult.getData();
        return borrowServices.searchBorrowRecordByUserId(user.getId());
    }

    public ServiceResult getCurrentBorrowedBooks() {
        return borrowServices.getCurrentBorrowedBooks();
    }

    public int getBorrowRecordCount() {
        return borrowServices.getBorrowRecordCount();
    }

    public int getTotalBooks() { // we using in StatisticsPanel for displaying total books 
        return bookServices.getBookCount();
    }

    public int getAvailableBooks() {
        return BookServices.availableBooks;
    }

    public int getTotalUsers() {
        return userServices.getUserCount();
    }

    public ServiceResult getMostBorrowedBooks() {
        return bookServices.getMostBorrowedBooks();
    }

    public ServiceResult getMostReadAuthors() {
        return authorServices.getMostreadersAuthors();
    }
}
package library_manage.controller;

import java.util.HashMap;
import java.util.PriorityQueue;
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

    public HashMap<String, PriorityQueue<User>> getAllWaitingList(){
        return borrowServices.waitingLists;
    }


    public ServiceResult borrowBook(String userName, boolean graduate, String isbn) { // we using this in class TransactionPanel in borrowBook()  when we want to borrow a book from the transaction panel
        User user = userServices.findOrCreateUser(userName, graduate);
        if (user == null) {
            return new ServiceResult(false, "User name cannot be empty");
        }
        return borrowServices.borrowBook(user, isbn);
    }

    public ServiceResult returnBook(String userName, String isbn) { // we using this in class TransactionPanel in returnBook() when we want to return a book from the transaction panel
        ServiceResult userResult = userServices.searchUserByName(userName);
        if (!userResult.isSuccess()) {
            return userResult;
        }

        User user = (User) userResult.getData();
        return borrowServices.returnBook(user, isbn);

    }

    public ServiceResult getWaitingList(String isbn) { // we using this in class TransactionPanel in loadWaitingList() when we want to load the waiting list  
        return borrowServices.showWaitingList(isbn);
    }

    public ServiceResult getBorrowHistory() { // we using this in class ReportController in getBorrowHistory() when we want to get the borrow history for displaying it in the report panel
        return borrowServices.getBorrowHistory();
    }

    public ServiceResult searchBorrowHistoryByUserName(String name) { // we using this in class ReportController in searchByUserName() when we want to search for a borrow history by user name from the report panel
        ServiceResult userResult = userServices.searchUserByName(name);
        if (!userResult.isSuccess()) {
            return userResult;
        }
        User user = (User) userResult.getData();
        return borrowServices.searchBorrowRecordByUserId(user.getId());
    }

    public ServiceResult getCurrentBorrowedBooks() {  // we using this in class ReportController in getCurrentBorrowedBooks() when we want to  get number the current borrowed books for displaying it in the report panel
        return borrowServices.getCurrentBorrowedBooks();
    }

    public int getBorrowRecordCount() { // we using in class StatisticsPanel in refreshStatistics() for displaying total borrow records
        return borrowServices.getBorrowRecordCount();
    }

    public int getTotalBooks() { // we using in class StatisticsPanel in refreshStatistics() for displaying total books 
        return bookServices.getBookCount();
    }

    public int getAvailableBooks() { // we using in class StatisticsPanel in refreshStatistics() for displaying number  available (الموجودة) books
        return BookServices.availableBooks;
    }

    public int getTotalUsers() { // we using in class StatisticsPanel refreshStatistics() and class TransactionPanel in refreshAll() for displaying total users
        return userServices.getUserCount();
    }

    public ServiceResult getMostBorrowedBooks() { // we using in statistics panel BUT we are calling this method from class ReportController in getMostBorrowedBooks() and in  class StatisticsPanel we caling it  in refreshStatistics() for displaying most borrowed books
        return bookServices.getMostBorrowedBooks();
    }

    public ServiceResult getMostReadAuthors() {  // we using in statistics panel BUT we are calling this method from class ReportController in getMostReadAuthors() and in  class StatisticsPanel we caling it  in refreshStatistics() for displaying most read authors
        return authorServices.getMostreadersAuthors();
    }
}
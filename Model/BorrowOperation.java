package library_manage.Model;

import java.time.LocalDate;

public class BorrowOperation {

    private final User borrower;
    private final Book book;
    private final LocalDate borrowDate;
    private final LocalDate expectedReturnDate;
    private boolean returned;
    private LocalDate actualReturnDate;

    public BorrowOperation(User borrower, Book book,
            LocalDate borrowDate,
            LocalDate expectedReturnDate) {

        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.returned = false;
        this.actualReturnDate = null;
    }

    public BorrowOperation(User borrower, Book book,
            LocalDate borrowDate,
            LocalDate expectedReturnDate,
            boolean returned,
            LocalDate actualReturnDate) {

        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.returned = returned;
        this.actualReturnDate = actualReturnDate;
    }

    public User getBorrower() {
        return borrower;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
}
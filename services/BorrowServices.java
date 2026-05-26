package library_manage.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import library_manage.Model.Book;
import library_manage.Model.BorrowOperation;
import library_manage.Model.User;
import library_manage.util.ServiceResult;

public class BorrowServices {
    private final ArrayList<BorrowOperation> borrowRecords;
    private final HashMap<String, PriorityQueue<User>> waitingLists;
    private final BookServices bookServices;
    private final UserServices userServices;

    public BorrowServices(BookServices bookServices, UserServices userServices) {
        this.bookServices = bookServices;
        this.borrowRecords = new ArrayList<>();
        this.waitingLists = new HashMap<>();
        this.userServices = userServices;
        loadBorrowRecordsFromDisk();
    }

    public ServiceResult borrowBook(User user, String isbn) {
        if (user == null) {
            return new ServiceResult(false, "User cannot be null");
        }

        ServiceResult result = bookServices.searchBookByIsbn(isbn);
        if (!result.isSuccess()) {
            return result;
        }

        Book book = (Book) result.getData();

        if (user.borrowedBooks.contains(book)) {
            return new ServiceResult(false, "User already borrowed this book");
        }

        if (user.borrowedBooks.size() >= 3) {
            return new ServiceResult(false, "User reached maximum borrow limit");
        }

        if (book.getnumberOfCopies() > 0) {
            book.setnumberOfCopies(book.getnumberOfCopies() - 1);
            BookServices.availableBooks--;
            book.setBorrowedCount(book.getBorrowedCount() + 1);
            book.getAuthor().incrementNumberOfReaders();

            BorrowOperation operation = new BorrowOperation(
                    user,
                    book,
                    LocalDate.now(),
                    LocalDate.now().plusDays(14));

            borrowRecords.add(operation);
            user.borrowedBooks.add(book);
            user.borrowedHistoryUser.add(operation);
            saveBorrowRecords();

            return new ServiceResult(
                    true,
                    "Book borrowed successfully. Return date: " + LocalDate.now().plusDays(14),
                    operation);
        }

        // if number of copyies doesn't enough
        if (!waitingLists.containsKey(isbn)) {
            waitingLists.put(
                    isbn,
                    new PriorityQueue<>((first, second) -> Boolean.compare(
                            second.isGraduate(),
                            first.isGraduate())));
        }

        waitingLists.get(isbn).add(user);
        return new ServiceResult(
                false,
                "Book unavailable. Added to waiting list. Position: " + waitingLists.get(isbn).size());
    }

    public ServiceResult returnBook(User user, String isbn) {
        if (user == null) {
            return new ServiceResult(false, "User cannot be null");
        }

        ServiceResult result = bookServices.searchBookByIsbn(isbn);
        if (!result.isSuccess()) {
            return result;
        }

        Book book = (Book) result.getData();

        if (!user.borrowedBooks.contains(book)) {
            return new ServiceResult(false, "User did not borrow this book");
        }

        user.borrowedBooks.remove(book);
        book.setnumberOfCopies(book.getnumberOfCopies() + 1);
        BookServices.availableBooks++;

        for (BorrowOperation operation : borrowRecords) {
            if (operation.getBorrower().getId() == user.getId()
                    && operation.getBook().getIsbn().equals(isbn)
                    && !operation.isReturned()) {
                operation.setReturned(true);
                operation.setActualReturnDate(LocalDate.now());
                break;
            }
        }

        if (waitingLists.containsKey(isbn)
                && !waitingLists.get(isbn).isEmpty()) {
            User nextUser = waitingLists.get(isbn).poll();

            ServiceResult autoBorrowResult = borrowBook(nextUser, isbn);

            if (autoBorrowResult.isSuccess()) {
                saveBorrowRecords();
                return new ServiceResult(
                        true,
                        "Book returned and automatically borrowed to: " + nextUser.getName(),
                        autoBorrowResult.getData());
            }
        }

        saveBorrowRecords();
        return new ServiceResult(true, "Book returned successfully");
    }

    public ServiceResult getBorrowHistory() {
        if (borrowRecords.isEmpty()) {
            return new ServiceResult(false, "No borrow history found");
        }

        return new ServiceResult(true, "Borrow history found", borrowRecords);
    }

    public ServiceResult getCurrentBorrowedBooks() {
        ArrayList<BorrowOperation> current = new ArrayList<>();
        for (BorrowOperation operation : borrowRecords) {
            if (!operation.isReturned()) {
                current.add(operation);
            }
        }
        return new ServiceResult(true, "Current borrowed books retrieved", current);
    }

    public ServiceResult showWaitingList(String isbn) {
        if (!waitingLists.containsKey(isbn)
                || waitingLists.get(isbn).isEmpty()) {
            return new ServiceResult(false, "Waiting list is empty");
        }

        PriorityQueue<User> queue = waitingLists.get(isbn);
        ArrayList<User> waitingUsers = new ArrayList<>(queue);
        return new ServiceResult(true, "Waiting list retrieved", waitingUsers);
    }

    public ServiceResult searchBorrowRecordByUserId(int userId) {
        // ArrayList<BorrowOperation> records = new ArrayList<>();

        // for (BorrowOperation operation : borrowRecords) {
        // if (operation.getBorrower().getId() == userId) {
        // records.add(operation);
        // }
        // }
        User user = userServices.getUserById(userId);
        if (user == null) {
            return new ServiceResult(false, "User not found");
        }

        ArrayList<BorrowOperation> records = user.borrowedHistoryUser;
        if (records.isEmpty()) {
            return new ServiceResult(false, "No records found");
        }

        return new ServiceResult(true, "Records found", records);
    }

    public ArrayList<BorrowOperation> getBorrowRecords() {
        return borrowRecords;
    }

    public int getBorrowRecordCount() {
        return borrowRecords.size();
    }

    public void saveBorrowRecords() {
        library_manage.util.TxtDataStore.saveBorrowRecords(borrowRecords);
        bookServices.saveBooks();
        userServices.saveUsers();
    }

    private void loadBorrowRecordsFromDisk() {
        ArrayList<BorrowOperation> records = library_manage.util.TxtDataStore.loadBorrowRecords(
                userServices::getUserById,
                bookServices::getBookByIsbn);

        for (BorrowOperation operation : records) {
            borrowRecords.add(operation);
            operation.getBorrower().borrowedHistoryUser.add(operation);
            if (!operation.isReturned() && !operation.getBorrower().borrowedBooks.contains(operation.getBook())) {
                operation.getBorrower().borrowedBooks.add(operation.getBook());
            }
        }
    }
}
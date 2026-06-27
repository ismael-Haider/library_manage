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
    // i make it public for use it in the book controller if i need to delete booke it need to be no person had this book or borrow it 
    public final ArrayList<BorrowOperation> borrowRecords;
    // i make it public for use it in the book controller class to check if the waiting list is empty or not when we add copies to a book
    public final HashMap<String, PriorityQueue<User>> waitingLists; // <isbn, waiting list of users>
    private final BookServices bookServices;
    private final UserServices userServices;

    public BorrowServices(BookServices bookServices, UserServices userServices) {
        this.bookServices = bookServices;
        this.borrowRecords = new ArrayList<>();
        this.waitingLists = new HashMap<>();
        this.userServices = userServices;
        loadBorrowRecordsFromDisk();
        loadWaitingListsFromDisk();
    }

    // Borrow a book if available, otherwise add user to waiting list
    public ServiceResult borrowBook(User user, String isbn) { // we using this in class TransactionController when we
                                                              // want to borrow a book from the transaction panel
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
            BookServices.availableBooks--; // = Number of books copies
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
        saveWaitingLists(); 
        return new ServiceResult(
                false,
                "Book unavailable. Added to waiting list. Position: " + waitingLists.get(isbn).size());
    }

    // Return a borrowed book and auto-assign it to next user in waiting list
    public ServiceResult returnBook(User user, String isbn) { // we using this in class Transaction Controller when we
                                                              // want to return a book from the transaction panel
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

        if (waitingLists.containsKey(isbn) &&
                !waitingLists.get(isbn).isEmpty()) {
            User nextUser = waitingLists.get(isbn).poll();
            saveWaitingLists();

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

    public ServiceResult processWaitingList(String isbn) {
        if (waitingLists.containsKey(isbn) &&
                !waitingLists.get(isbn).isEmpty()) {
            return new ServiceResult(true, "Copies added successfully", bookServices.getBookByIsbn(isbn));
        }
        while (waitingLists.containsKey(isbn)
                && !waitingLists.get(isbn).isEmpty()) {
            Book book = bookServices.getBookByIsbn(isbn);

            if (book.getnumberOfCopies() <= 0) {
                break;
            }

            User nextUser = waitingLists.get(isbn).poll();

            ServiceResult result = borrowBook(nextUser, isbn);

            if (!result.isSuccess()) {
                break;
            }
        }

        saveBorrowRecords();
        return new ServiceResult(true, "successfully adding but we completed as much of the waiting list as possible ");
    }

    // Get complete borrow history
    public ServiceResult getBorrowHistory() { // we using this in class Transaction Controller when we want to get the
                                              // borrow history from the reports panel
        pruneArchivedBorrowHistory(); // clear old records before returning the history
        if (borrowRecords.isEmpty()) {
            return new ServiceResult(false, "No borrow history found");
        }

        return new ServiceResult(true, "Borrow history found", borrowRecords);
    }

    // Get current borrowed books
    public ServiceResult getCurrentBorrowedBooks() { // we using this in class Transaction Controller when we want to
                                                     // get the current borrowed books from the transaction panel[
        ArrayList<BorrowOperation> current = new ArrayList<>();
        for (BorrowOperation operation : borrowRecords) {
            if (!operation.isReturned()) {
                current.add(operation);
            }
        }
        if (current.isEmpty()) {
            return new ServiceResult(false, "No current borrowed books found");
        }
        return new ServiceResult(true, "Current borrowed books retrieved", current);
    }

    // Show waiting list for a specific book
    public ServiceResult showWaitingList(String isbn) { // we using this in class Transaction Controller when we want to
                                                        // show the waiting list for a book from the transaction panel
        if (!waitingLists.containsKey(isbn)
                || waitingLists.get(isbn).isEmpty()) {
            return new ServiceResult(false, "Waiting list is empty");
        }

        PriorityQueue<User> queue = waitingLists.get(isbn);
        ArrayList<User> waitingUsers = new ArrayList<>(queue);
        return new ServiceResult(true, "Waiting list retrieved", waitingUsers);
    }

    // Get borrowing history for a specific user
    public ServiceResult searchBorrowRecordByUserId(int userId) { // we using this in class Transaction Controller when
                                                                  // we want to search for the borrow history of a user
                                                                  // by his name from the reports panel
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

    public ArrayList<BorrowOperation> getBorrowRecords() { // we not using this method anywhere, so we can delete it
        return borrowRecords;
    }

    public int getBorrowRecordCount() { // we using this in class Transaction Controller when display the count of
                                        // Borrow Records in statistics and transactions panel
        return borrowRecords.size();
    }

    public void saveBorrowRecords() { // we using this in class BorrowedServices when we want to save the borrow
                                      // records to the disk after we borrowed or returned a book
        pruneArchivedBorrowHistory();
        library_manage.util.TxtDataStore.saveBorrowRecords(borrowRecords);
        bookServices.saveBooks();
        userServices.saveUsers();
    }

    private void loadBorrowRecordsFromDisk() { // we using this in class BorrowedServices when we want to load the
                                               // borrow records from the disk when we start the application
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
        pruneArchivedBorrowHistory();
    }

    private void loadWaitingListsFromDisk() {
        HashMap<String, PriorityQueue<User>> loaded = library_manage.util.TxtDataStore.loadWaitingLists(
            userServices::getUserById
        );
        this.waitingLists.putAll(loaded);
}
    private void saveWaitingLists() {
        library_manage.util.TxtDataStore.saveWaitingLists(waitingLists);
}

    private void pruneArchivedBorrowHistory() { // we using this in class BorrowedServices when we want to prune the
                                                // borrow records that are older than 1 year
        LocalDate cutoffDate = LocalDate.now().minusYears(1);
        boolean removed = borrowRecords.removeIf(operation -> operation.isReturned()
                && operation.getBorrowDate() != null
                && operation.getBorrowDate().isBefore(cutoffDate));

        if (removed) {
            library_manage.util.TxtDataStore.saveBorrowRecords(borrowRecords);
        }
    }
}
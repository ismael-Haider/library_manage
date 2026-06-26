package library_manage.services;

import java.util.ArrayList;
import library_manage.Model.Author;
import library_manage.Model.Avl;
import library_manage.Model.Book;
import library_manage.Model.BookNode;
import library_manage.util.ServiceResult;
import library_manage.util.TxtDataStore;

public class BookServices {
    // for statistics and quick access to available books count
    public static int availableBooks = 0; // =? Number of book copies
    private final Avl avlBooks;
    private final AuthorServices authorServices;

    public BookServices() {
        this(new AuthorServices());
    }

    public BookServices(AuthorServices authorServices) {
        this.avlBooks = new Avl();
        this.authorServices = authorServices;
        loadBooksFromDisk();
    }

    public ServiceResult addBook(Book book) { // where we using this ? in class BookController when we want to add a book from the book management panel
        if (book == null) {
            return new ServiceResult(false, "Book cannot be null");
        }

        if (avlBooks.search(book.getIsbn()) != null) {
            addCopies(book.getIsbn(), book.getnumberOfCopies());
            return new ServiceResult(true, "Book already exists , but we have increased the number of copies");
        }

        avlBooks.insert(book);
        availableBooks += book.getnumberOfCopies();
        TxtDataStore.saveBooks(getAllBookObjects()); // i don't know what and how is doing
        return new ServiceResult(true, "Book added successfully", book);
    }

    public ServiceResult searchBookByIsbn(String isbn) { // where we using this ? in class BookController when we want to search for a book by isbn from the book management panel
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ServiceResult(false, "ISBN cannot be empty");
        }

        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        return new ServiceResult(true, "Book found", book);
    }

    public ServiceResult searchBookByTitle(String title) { // where we using this ? in class BookController when we want to search for a book by title from the book management panel
        if (title == null || title.trim().isEmpty()) {
            return new ServiceResult(false, "Title cannot be empty");
        }

        Book book = avlBooks.searchBytitle(title);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        return new ServiceResult(true, "Book found", book);
    }

    public ServiceResult deleteBook(String isbn) { // where we using this ? in class BookController when we want to delete a book from the book management panel
        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        availableBooks -= book.getnumberOfCopies();
        avlBooks.delete(isbn);
        TxtDataStore.saveBooks(getAllBookObjects());
        return new ServiceResult(true, "Book deleted successfully");
    }

    public ServiceResult addCopies(String isbn, int count) { // where we using this ? in class BookController when we want to add copies to a book in the book management panel
        if (count <= 0) {
            return new ServiceResult(false, "Copies to add must be greater than zero");
        }

        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        book.setnumberOfCopies(book.getnumberOfCopies() + count);
        availableBooks += count;
        TxtDataStore.saveBooks(getAllBookObjects());
        return new ServiceResult(true, "Copies added successfully", book);
    }

    public ServiceResult reduceCopies(String isbn, int count) { // where we using this ? in class BookController when we want to reduce the number of copies of a book in the book management panel
        if (count <= 0) {
            return new ServiceResult(false, "Copies to reduce must be greater than zero");
        }

        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        if (book.getnumberOfCopies() < count) {
            return new ServiceResult(false, "Copies cannot be negative");
        }

        book.setnumberOfCopies(book.getnumberOfCopies() - count);
        availableBooks -= count;
        TxtDataStore.saveBooks(getAllBookObjects());
        return new ServiceResult(true, "Copies reduced successfully", book);
    }

    public ServiceResult getAllBooks() { // where we using this ? in class BookController when we want to show all books in the table in the book management panel
        ArrayList<BookNode> books = avlBooks.getAllBooks();
        return new ServiceResult(true, "Books retrieved successfully", books);
    }

    public ServiceResult getMostBorrowedBooks() {// where we using this ? in class TransactionController when we want to show the most borrowed books in the statistics panel
        ArrayList<BookNode> books = avlBooks.getAllBooks();
        books.sort((left, right) -> Integer.compare(
                right.book.getBorrowedCount(),
                left.book.getBorrowedCount()));

        if (books.size() <= 10) {
            return new ServiceResult(true, "Most borrowed books found", books);
        }

        return new ServiceResult(true, "Most borrowed books found", books.subList(0, 10));
    }

    public Book getBookByIsbn(String isbn) { // where we using this ? in class BorrowServices when we want to get the book object by isbn to check if the user can borrow it or not // حكي ذكاء اصطناعي
        return avlBooks.search(isbn);
    }

    public ArrayList<Book> getAllBookObjects() { // where we using this ? in class BookServices when we want to save the books to the disk 
        ArrayList<Book> books = new ArrayList<>();
        for (BookNode bookNode : avlBooks.getAllBooks()) {
            books.add(bookNode.book);
        }
        return books;
    }

    public int getBookCount() { //  where we using this ? in class TransactionController when we want to get the total number of books in the library , and in class LibraryFrame for if is 0 to Autofill 
        return avlBooks.getAllBooks().size();
    }

    public void saveBooks() { // we using this in class BorrowedServices when we want to save the books to the disk after we borrowed or returned a book we handling this with txtDataStore
        TxtDataStore.saveBooks(getAllBookObjects());
    }

    private void loadBooksFromDisk() { // where we using this ? in class BookServices when we want to load the books from the disk when we start the application
        ArrayList<Book> books = TxtDataStore.loadBooks(authorName -> {
            Author author = authorServices.findAuthorByName(authorName);
            if (author != null) {
                return author;
            }
            return authorServices.findOrCreateAuthor(authorName);
        });

        for (Book book : books) {
            avlBooks.insert(book);
            availableBooks += book.getnumberOfCopies();
        }
    }
}
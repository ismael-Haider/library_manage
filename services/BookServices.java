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
    public static int availableBooks = 0;
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

    public ServiceResult addBook(Book book) {
        if (book == null) {
            return new ServiceResult(false, "Book cannot be null");
        }

        if (avlBooks.search(book.getIsbn()) != null) {
            return new ServiceResult(false, "Book already exists");
        }

        avlBooks.insert(book);
        availableBooks += book.getnumberOfCopies();
        TxtDataStore.saveBooks(getAllBookObjects());
        return new ServiceResult(true, "Book added successfully", book);
    }

    public ServiceResult searchBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ServiceResult(false, "ISBN cannot be empty");
        }

        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        return new ServiceResult(true, "Book found", book);
    }

    public ServiceResult searchBookByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ServiceResult(false, "Title cannot be empty");
        }

        Book book = avlBooks.searchBytitle(title);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        return new ServiceResult(true, "Book found", book);
    }

    public ServiceResult deleteBook(String isbn) {
        Book book = avlBooks.search(isbn);
        if (book == null) {
            return new ServiceResult(false, "Book not found");
        }

        availableBooks -= book.getnumberOfCopies();
        avlBooks.delete(isbn);
        TxtDataStore.saveBooks(getAllBookObjects());
        return new ServiceResult(true, "Book deleted successfully");
    }

    public ServiceResult addCopies(String isbn, int count) {
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

    public ServiceResult reduceCopies(String isbn, int count) {
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

    public ServiceResult getAllBooks() {
        ArrayList<BookNode> books = avlBooks.getAllBooks();
        return new ServiceResult(true, "Books retrieved successfully", books);
    }

    public ServiceResult getMostBorrowedBooks() {
        ArrayList<BookNode> books = avlBooks.getAllBooks();
        books.sort((left, right) -> Integer.compare(
                right.book.getBorrowedCount(),
                left.book.getBorrowedCount()));

        if (books.size() <= 10) {
            return new ServiceResult(true, "Most borrowed books found", books);
        }

        return new ServiceResult(true, "Most borrowed books found", books.subList(0, 10));
    }

    public Book getBookByIsbn(String isbn) {
        return avlBooks.search(isbn);
    }

    public ArrayList<Book> getAllBookObjects() {
        ArrayList<Book> books = new ArrayList<>();
        for (BookNode bookNode : avlBooks.getAllBooks()) {
            books.add(bookNode.book);
        }
        return books;
    }

    public int getBookCount() {
        return avlBooks.getAllBooks().size();
    }

    public void saveBooks() {
        TxtDataStore.saveBooks(getAllBookObjects());
    }

    private void loadBooksFromDisk() {
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
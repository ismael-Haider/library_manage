package library_manage.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import library_manage.Model.Author;
import library_manage.Model.Book;
import library_manage.Model.BorrowOperation;
import library_manage.Model.User;

public final class TxtDataStore {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path AUTHORS_FILE = DATA_DIR.resolve("authors.txt");
    private static final Path BOOKS_FILE = DATA_DIR.resolve("books.txt");
    private static final Path USERS_FILE = DATA_DIR.resolve("users.txt");
    private static final Path BORROWS_FILE = DATA_DIR.resolve("borrows.txt");

    private TxtDataStore() {
    }

    public static ArrayList<Author> loadAuthors() {
        ArrayList<Author> authors = new ArrayList<>();
        for (String line : readAllLines(AUTHORS_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            String name = parts[0].trim();
            int readers = parts.length > 1 ? parseInt(parts[1], 0) : 0;
            if (!name.isEmpty()) {
                authors.add(new Author(name, readers));
            }
        }
        return authors;
    }

    public static void saveAuthors(List<Author> authors) {
        ArrayList<String> lines = new ArrayList<>();
        for (Author author : authors) {
            lines.add(author.getName() + "|" + author.getNumberOfReaders());
        }
        writeAllLines(AUTHORS_FILE, lines);
    }

    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (String line : readAllLines(USERS_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            if (parts.length < 3) {
                continue;
            }

            int id = parseInt(parts[0], -1);
            String name = parts[1].trim();
            boolean graduate = Boolean.parseBoolean(parts[2].trim());
            if (id > 0 && !name.isEmpty()) {
                users.add(new User(name, graduate, id));
            }
        }
        return users;
    }

    public static void saveUsers(List<User> users) {
        ArrayList<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.getId() + "|" + user.getName() + "|" + user.isGraduate());
        }
        writeAllLines(USERS_FILE, lines);
    }

    public static ArrayList<Book> loadBooks(Function<String, Author> authorResolver) {
        ArrayList<Book> books = new ArrayList<>();
        for (String line : readAllLines(BOOKS_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            // the number -1 to not include empty element ex : ismael| haider | => ["ismael","haider"] , without -1 ["ismael","haider",""]
            String[] parts = line.split("\\|", -1);// to move to the 
            if (parts.length < 5) {
                continue;
            }

            String isbn = parts[0].trim();
            String title = parts[1].trim();
            String authorName = parts[2].trim();
            int copies = parseInt(parts[3], 0);
            int borrowedCount = parseInt(parts[4], 0);

            if (isbn.isEmpty() || title.isEmpty() || authorName.isEmpty()) {
                continue;
            }

            Author author = authorResolver.apply(authorName);
            if (author == null) {
                author = new Author(authorName);
            }

            Book book = new Book(title, isbn, author, copies, borrowedCount);
            books.add(book);
        }
        return books;
    }

    public static void saveBooks(List<Book> books) {
        ArrayList<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(book.getIsbn() + "|" + book.getTitle() + "|" + book.getAuthor().getName()
                    + "|" + book.getnumberOfCopies() + "|" + book.getBorrowedCount());
        }
        writeAllLines(BOOKS_FILE, lines);
    }

    public static ArrayList<BorrowOperation> loadBorrowRecords(
            Function<Integer, User> userResolver,
            Function<String, Book> bookResolver) {

        ArrayList<BorrowOperation> borrowOperations = new ArrayList<>();
        for (String line : readAllLines(BORROWS_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) {
                continue;
            }

            int userId = parseInt(parts[0], -1);
            String isbn = parts[1].trim();
            LocalDate borrowDate = parseDate(parts[2]);
            LocalDate expectedReturnDate = parseDate(parts[3]);
            boolean returned = Boolean.parseBoolean(parts[4].trim());
            LocalDate actualReturnDate = parseDate(parts[5]);

            User user = userResolver.apply(userId);
            Book book = bookResolver.apply(isbn);
            if (user == null || book == null || borrowDate == null || expectedReturnDate == null) {
                continue;
            }

            BorrowOperation operation = new BorrowOperation(
                    user,
                    book,
                    borrowDate,
                    expectedReturnDate,
                    returned,
                    actualReturnDate);

            borrowOperations.add(operation);
        }
        return borrowOperations;
    }

    public static void saveBorrowRecords(List<BorrowOperation> borrowOperations) {
        ArrayList<String> lines = new ArrayList<>();
        for (BorrowOperation operation : borrowOperations) {
            String actualReturnDate = operation.getActualReturnDate() == null
                    ? ""
                    : operation.getActualReturnDate().toString();

            lines.add(operation.getBorrower().getId() + "|"
                    + operation.getBook().getIsbn() + "|"
                    + operation.getBorrowDate() + "|"
                    + operation.getExpectedReturnDate() + "|"
                    + operation.isReturned() + "|"
                    + actualReturnDate);
        }
        writeAllLines(BORROWS_FILE, lines);
    }
    public static void saveWaitingLists(HashMap<String, PriorityQueue<User>> waitingLists) {
        ArrayList<String> lines = new ArrayList<>();
        for (String isbn : waitingLists.keySet()) {
            PriorityQueue<User> queue = waitingLists.get(isbn);
            StringBuilder sb = new StringBuilder();
            sb.append(isbn).append("|");
            for (User user : queue) {
                sb.append(user.getId()).append(",");
            }
            if (!queue.isEmpty()) {
                sb.setLength(sb.length() - 1); // Remove the last comma
            }
            lines.add(sb.toString());
        }
        writeAllLines(DATA_DIR.resolve("waiting_lists.txt"), lines);
    }
    
    public static HashMap<String, PriorityQueue<User>> loadWaitingLists(Function<Integer, User> userResolver) {
        HashMap<String, PriorityQueue<User>> waitingLists = new HashMap<>();
        Path waitingFile = DATA_DIR.resolve("waiting_lists.txt");
        
        for (String line : readAllLines(waitingFile)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            
            String[] parts = line.split("\\|", -1);
            if (parts.length < 2) {
                continue;
            }
            
            String isbn = parts[0].trim();
            if (isbn.isEmpty()) {
                continue;
            }
            
            // FIX: Add the comparator here - graduates have priority
            PriorityQueue<User> queue = new PriorityQueue<>((first, second) -> 
                Boolean.compare(second.isGraduate(), first.isGraduate()));
            
            String usersPart = parts[1].trim();
            if (!usersPart.isEmpty()) {
                String[] userIds = usersPart.split(",");
                for (String userIdStr : userIds) {
                    int userId = parseInt(userIdStr.trim(), -1);
                    if (userId > 0) {
                        User user = userResolver.apply(userId);
                        if (user != null) {
                            queue.add(user);
                        }
                    }
                }
            }
            
            waitingLists.put(isbn, queue);
        }
        
        return waitingLists;
    }

    private static List<String> readAllLines(Path file) {
        try {
            ensureParentDirectory(file);
            if (!Files.exists(file)) {
                Files.createFile(file);
                return new ArrayList<>();
            }
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return new ArrayList<>();
        }
    }

    private static void writeAllLines(Path file, List<String> lines) {
        try {
            ensureParentDirectory(file);
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write data file: " + file, exception);
        }
    }

    private static void ensureParentDirectory(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
    
}
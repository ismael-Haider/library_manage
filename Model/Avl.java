package library_manage.Model;

import java.util.ArrayList;

public class Avl {
    BookNode root;

    int height(BookNode node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    int getBalance(BookNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    BookNode rightRotate(BookNode y) {

        BookNode x = y.left;
        BookNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    BookNode leftRotate(BookNode x) {

        BookNode y = x.right;
        BookNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    public BookNode insert(Book book) {
        root = insertHelper(root, book);
        return root;
    }

    private BookNode insertHelper(BookNode node, Book book) {

        if (node == null) {
            return new BookNode(book);
        }

        if (book.getIsbn().equals(node.book.getIsbn())) {
            System.out.println("Book with this ISBN already exists");
            // Library.counterOfBooks--;// لأنه رح يزيد في addBook وبعدين رح ينقصه
            return node;
        }

        if (book.getIsbn().compareTo(node.book.getIsbn()) < 0) {
            node.left = insertHelper(node.left, book);
        } else if (book.getIsbn().compareTo(node.book.getIsbn()) > 0) {
            node.right = insertHelper(node.right, book);
        }
        // دخلت عالمكان المحدد ولكن مو الدقيق والان فقرة التوازن

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && book.getIsbn().compareTo(node.left.book.getIsbn()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && book.getIsbn().compareTo(node.right.book.getIsbn()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && book.getIsbn().compareTo(node.left.book.getIsbn()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && book.getIsbn().compareTo(node.right.book.getIsbn()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public Book search(String isbn) {
        return searchHelper(root, isbn);
    }

    private Book searchHelper(BookNode node, String isbn) {

        if (node == null)
            return null;

        if (isbn.equals(node.book.getIsbn())) {
            return node.book;
        }

        if (isbn.compareTo(node.book.getIsbn()) < 0) {
            return searchHelper(node.left, isbn);
        }

        return searchHelper(node.right, isbn);
    }

    public Book searchBytitle(String title) {
        return searchHelperBytitle(root, title);
    }

    private Book searchHelperBytitle(BookNode node, String title) { // n التعديل مشان نكون مستحدمين نفس الطريقة والبحث 
        if (node == null)
            return null;

        if (title.equalsIgnoreCase(node.book.getTitle())) {
            return node.book;
        }

        if (title.compareTo(node.book.getTitle()) < 0) {
            return searchHelper(node.left, title);
        }
        return searchHelper(node.right, title);
    }

    public void delete(String isbn) {
        root = deleteHelper(root, isbn);
    }

    private BookNode deleteHelper(BookNode node, String isbn) {

        if (node == null)
            return null;

        if (isbn.compareTo(node.book.getIsbn()) < 0) {
            node.left = deleteHelper(node.left, isbn);
        } else if (isbn.compareTo(node.book.getIsbn()) > 0) {
            node.right = deleteHelper(node.right, isbn);
        } else {

            // حالة 1: بدون أبناء
            if (node.left == null && node.right == null)
                return null;

            // حالة 2: ابن واحد
            if (node.left == null)
                return node.right;

            if (node.right == null)
                return node.left;

            // حالة 3: ابنان
            BookNode successor = successor(node.right);
            node.book = successor.book;
            node.right = deleteHelper(node.right, successor.book.getIsbn());
        }
        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && isbn.compareTo(node.left.book.getIsbn()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && isbn.compareTo(node.right.book.getIsbn()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && isbn.compareTo(node.left.book.getIsbn()) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && isbn.compareTo(node.right.book.getIsbn()) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private BookNode successor(BookNode node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    public ArrayList<BookNode> getAllBooks() {  // where is the method getAllBooks it using ? in class BookServices in method getBooksObjects() 

        ArrayList<BookNode> books = new ArrayList<>();

        getBooksInOrder(root, books);

        return books;
    }

    private void getBooksInOrder(BookNode node, ArrayList<BookNode> books) { // we using this in here in getAllBooks())

        if (node == null)
            return;

        getBooksInOrder(node.left, books);

        books.add(node);

        getBooksInOrder(node.right, books);
    }
}
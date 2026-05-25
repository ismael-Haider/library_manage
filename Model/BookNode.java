package library_manage.Model;

public class BookNode {
    public Book book;
    BookNode left;
    BookNode right;
    int height;

    public BookNode(Book book) {
        this.book = book;
        this.left = null;
        this.right = null;
        this.height = 0;
    }
}

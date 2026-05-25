package library_manage.Model;

public class Book {
    private String title;
    private Author author;
    private String isbn;
    private int numberOfCopies;
    private int borrowedCount;

    public Book() {
    }

    public Book(String title, String isbn, Author author, int numberOfCopies) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.numberOfCopies = numberOfCopies;
        this.borrowedCount = 0;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getnumberOfCopies() {
        return numberOfCopies;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    public void setBorrowedCount(int borrowedCount) {
        this.borrowedCount = borrowedCount;
    }

    public void setnumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void displayInfo() {
        System.out.println("Title: " + title);
        System.out.println("Author: " + author.getName());
        System.out.println("ISBN: " + isbn);
        System.out.println("numberOfCopies Available: " + numberOfCopies);
    }

}

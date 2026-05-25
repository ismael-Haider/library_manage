
package library_manage.Model;

import java.util.ArrayList;

public class User extends Person {
    private final boolean graduate;
    private static int counter = 1;
    private final int id;
    public final ArrayList<Book> borrowedBooks;
    public final ArrayList<BorrowOperation> borrowedHistoryUser;
    public User(String name, boolean graduate) {
        super(name);
        this.graduate = graduate;
        this.id = counter++;
        this.borrowedBooks = new ArrayList<>();
        this.borrowedHistoryUser = new ArrayList<>();
    }

    public boolean isGraduate() {
        return graduate;
    }

    public int getId() {
        return id;
    }
}

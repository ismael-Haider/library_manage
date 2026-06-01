package library_manage.view.tables;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import library_manage.Model.Book;

public class BookTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = { "ISBN", "Title", "Author", "Available Copies", "Borrowed Count" };
    private final List<Book> books = new ArrayList<>();

    public void setBooks(List<Book> newBooks) {
        books.clear();
        if (newBooks != null) {
            books.addAll(newBooks);
        }
        fireTableDataChanged();
    }

    public Book getBookAt(int rowIndex) {
        return books.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex >= 3 ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> book.getIsbn();
            case 1 -> book.getTitle();
            case 2 -> book.getAuthor().getName();
            case 3 -> book.getnumberOfCopies();
            case 4 -> book.getBorrowedCount();
            default -> "";
        };
    }
}
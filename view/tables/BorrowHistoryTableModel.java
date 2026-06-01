package library_manage.view.tables;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import library_manage.Model.BorrowOperation;

public class BorrowHistoryTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = { "User", "ISBN", "Title", "Borrowed", "Due", "Returned", "Return Date" };
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final List<BorrowOperation> records = new ArrayList<>();

    public void setRecords(List<BorrowOperation> newRecords) {
        records.clear();
        if (newRecords != null) {
            records.addAll(newRecords);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return records.size();
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
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BorrowOperation record = records.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> record.getBorrower().getName();
            case 1 -> record.getBook().getIsbn();
            case 2 -> record.getBook().getTitle();
            case 3 -> FORMATTER.format(record.getBorrowDate());
            case 4 -> FORMATTER.format(record.getExpectedReturnDate());
            case 5 -> record.isReturned() ? "Yes" : "No";
            case 6 -> record.getActualReturnDate() == null ? "-" : FORMATTER.format(record.getActualReturnDate());
            default -> "";
        };
    }
}
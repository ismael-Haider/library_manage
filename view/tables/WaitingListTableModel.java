package library_manage.view.tables;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import library_manage.Model.User;

public class WaitingListTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = { "Position", "User ID", "User Name", "Graduate" };
    private final List<User> users = new ArrayList<>();

    public void setUsers(List<User> newUsers) {
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return users.size();
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
        return columnIndex == 0 || columnIndex == 1 ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = users.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> user.getId();
            case 2 -> user.getName();
            case 3 -> user.isGraduate() ? "Yes" : "No";
            default -> "";
        };
    }
}
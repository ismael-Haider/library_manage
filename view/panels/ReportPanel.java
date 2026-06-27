package library_manage.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import library_manage.Model.Author;
import library_manage.Model.BookNode;
import library_manage.Model.BorrowOperation;
import library_manage.controller.ReportController;
import library_manage.util.ServiceResult;
import library_manage.view.tables.BorrowHistoryTableModel;

public class ReportPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 242, 243);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color PRIMARY = new Color(49, 92, 170);
    private static final Path REPORT_FILE = Paths.get("data", "borrow_history_report.txt");

    private final ReportController reportController;
    private final BorrowHistoryTableModel historyTableModel = new BorrowHistoryTableModel();
    private final JTable historyTable = new JTable(historyTableModel);
    private final JTextArea insightsArea = new JTextArea();
    private final JTextField searchTitleField = createField("Search by book name");
    private final JTextField searchUserField = createField("Search by user name");

    public ReportPanel(ReportController reportController) {
        this.reportController = reportController;
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        refreshReport();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        JButton searchUser = createButton("Search User");
        searchUser.addActionListener(event -> searchByUserName());
        JButton search = createButton("Search");
        search.addActionListener(event -> searchByTitle());
        JButton refresh = createButton("Refresh");
        refresh.addActionListener(event -> refreshReport());
        JButton save = createButton("Save Report");
        save.addActionListener(event -> saveReport());
        controls.add(searchUserField);
        controls.add(searchUser);
        controls.add(searchTitleField);
        controls.add(search);
        controls.add(refresh);
        controls.add(save);

        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.add(buildHistoryCard(), BorderLayout.CENTER);
        body.add(buildInsightsCard(), BorderLayout.EAST);
        return body;
    }

    private JPanel buildHistoryCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel title = new JLabel("Borrow History");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        historyTable.setRowHeight(34);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(new Color(228, 230, 234));
        historyTable.setFillsViewportHeight(true);
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(PRIMARY);
        historyTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        historyTable.setDefaultRenderer(String.class, centerRenderer);

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildInsightsCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new java.awt.Dimension(360, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel title = new JLabel("Insights");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        insightsArea.setEditable(false);
        insightsArea.setLineWrap(true);
        insightsArea.setWrapStyleWord(true);
        insightsArea.setBackground(Color.WHITE);
        insightsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(insightsArea), BorderLayout.CENTER);
        return card;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    public void refreshReport() {
        ServiceResult historyResult = reportController.getBorrowHistory();
        if (historyResult.isSuccess()) {
            @SuppressWarnings("unchecked")
            ArrayList<BorrowOperation> records = (ArrayList<BorrowOperation>) historyResult.getData();
            historyTableModel.setRecords(records);
        } else {
            historyTableModel.setRecords(new ArrayList<>());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Current borrowed books:\n");
        ServiceResult currentResult = reportController.getCurrentBorrowedBooks();
        if (currentResult.isSuccess()) {
            @SuppressWarnings("unchecked")
            ArrayList<BorrowOperation> current = (ArrayList<BorrowOperation>) currentResult.getData();
            builder.append(current.size()).append('\n');
        } else {
            builder.append("0\n");
        }

        builder.append("\nMost borrowed books:\n");
        appendBooks(builder, reportController.getMostBorrowedBooks());

        builder.append("\nMost read authors:\n");
        appendAuthors(builder, reportController.getMostReadAuthors());

        insightsArea.setText(builder.toString());
    }

    private void appendBooks(StringBuilder builder, ServiceResult result) {
        if (!result.isSuccess()) {
            builder.append(result.getMessage()).append('\n');
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BookNode> books = (ArrayList<BookNode>) result.getData();
        if (books.isEmpty()) {
            builder.append("No data\n");
            return;
        }

        for (BookNode node : books) {
            builder.append("- ").append(node.book.getTitle()).append(" (")
                    .append(node.book.getBorrowedCount()).append(")\n");
        }
    }

    private void appendAuthors(StringBuilder builder, ServiceResult result) {
        if (!result.isSuccess()) {
            builder.append(result.getMessage()).append('\n');
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<Author> authors = (ArrayList<Author>) result.getData();
        if (authors.isEmpty()) {
            builder.append("No data\n");
            return;
        }

        for (Author author : authors) {
            builder.append("- ").append(author.getName()).append(" (")
                    .append(author.getNumberOfReaders()).append(")\n");
        }
    }

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(new Color(145, 152, 161));
        field.setColumns(18);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(208, 212, 219)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if (placeholder.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(145, 152, 161));
                }
            }
        });
        return field;
    }

    private void searchByTitle() { // we using this for search on book by it's title and disply the borrow history in tabel
        String query = searchTitleField.getText().trim();
        if (query.isEmpty() || "Search by book name".equals(query)) {
            refreshReport();
            return;
        }

        ServiceResult searchResult = reportController.searchBorrowHistoryByTitle(query);
        if (!searchResult.isSuccess()) {
            historyTableModel.setRecords(new ArrayList<>());
            insightsArea.setText(searchResult.getMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BorrowOperation> filtered = (ArrayList<BorrowOperation>) searchResult.getData();
        historyTableModel.setRecords(filtered);
    }

    private void searchByUserName() { // we using this for search on user by it's name and disply his borrow books in tabel
        String query = searchUserField.getText().trim();
        if (query.isEmpty() || "Search by user name".equals(query)) {
            refreshReport();
            return;
        }

        ServiceResult searchResult = reportController.searchBorrowHistoryByUserName(query);
        if (!searchResult.isSuccess()) {
            historyTableModel.setRecords(new ArrayList<>());
            insightsArea.setText(searchResult.getMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BorrowOperation> filtered = (ArrayList<BorrowOperation>) searchResult.getData();
        historyTableModel.setRecords(filtered);
    }

    private void saveReport() {
        ServiceResult historyResult = reportController.getBorrowHistory();
        if (!historyResult.isSuccess()) {
            showMessage(historyResult.getMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BorrowOperation> records = (ArrayList<BorrowOperation>) historyResult.getData();
        StringBuilder report = new StringBuilder();
        report.append("Borrow History Report\n");
        report.append("Generated: ").append(java.time.LocalDate.now()).append("\n\n");

        for (BorrowOperation record : records) {
            report.append(record.getBorrower().getName())
                    .append(" | ")
                    .append(record.getBook().getTitle())
                    .append(" | ISBN: ")
                    .append(record.getBook().getIsbn())
                    .append(" | Borrowed: ")
                    .append(record.getBorrowDate())
                    .append(" | Due: ")
                    .append(record.getExpectedReturnDate())
                    .append(" | Returned: ")
                    .append(record.isReturned())
                    .append(" | Return Date: ")
                    .append(record.getActualReturnDate() == null ? "-" : record.getActualReturnDate())
                    .append("\n");
        }

        try {
            Files.createDirectories(REPORT_FILE.getParent());
            Files.writeString(REPORT_FILE, report.toString(), StandardCharsets.UTF_8);
            showMessage("Report saved to " + REPORT_FILE.toString());
        } catch (IOException exception) {
            showMessage("Unable to save report: " + exception.getMessage());
        }
    }

    private void showMessage(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message);
    }
}
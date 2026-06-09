package library_manage.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import library_manage.Model.Book;
import library_manage.Model.BookNode;
import library_manage.controller.BookController;
import library_manage.util.ServiceResult;
import library_manage.view.tables.BookTableModel;

public class BookManagementPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 242, 243);
    private static final Color CARD_BORDER = new Color(225, 228, 232);
    private static final Color PRIMARY = new Color(49, 92, 170);

    private static final String ISBN_PLACEHOLDER = "ISBN e.g. 978-0-13-235088-4";
    private static final String TITLE_PLACEHOLDER = "Book title (letters only)";
    private static final String COPIES_PLACEHOLDER = "Copies 1-100";
    private static final String AUTHOR_PLACEHOLDER = "Author name (letters only)";
    private static final String SEARCH_TITLE_PLACEHOLDER = "Search title";
    private static final String SEARCH_ISBN_PLACEHOLDER = "Search ISBN";
    private static final String UPDATE_ISBN_PLACEHOLDER = "ISBN to update";
    private static final String UPDATE_COUNT_PLACEHOLDER = "Count 1-100";
    private static final String DELETE_ISBN_PLACEHOLDER = "ISBN to delete";
    private static final String WAITING_ISBN_PLACEHOLDER = "ISBN for waiting list";

    private static final Pattern ISBN_PATTERN = Pattern.compile("^\\d+-\\d+-\\d+-\\d+-\\d+$");
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[\\p{L}][\\p{L}\\s'.-]*$");

    private final BookController bookController;
    private final BookTableModel tableModel = new BookTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField isbnField = createField(ISBN_PLACEHOLDER);
    private final JTextField titleField = createField(TITLE_PLACEHOLDER);
    private final JTextField copiesField = createField(COPIES_PLACEHOLDER);
    private final JTextField authorField = createField(AUTHOR_PLACEHOLDER);

    private final JTextField updateIsbnField = createField(UPDATE_ISBN_PLACEHOLDER);
    private final JTextField updateCountField = createField(UPDATE_COUNT_PLACEHOLDER);

    private final JTextField deleteIsbnField = createField(DELETE_ISBN_PLACEHOLDER);
    private final JTextField searchTitleField = createField(SEARCH_TITLE_PLACEHOLDER);
    private final JTextField searchIsbnField = createField(SEARCH_ISBN_PLACEHOLDER);

    public BookManagementPanel(BookController bookController) {
        this.bookController = bookController;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildContent(), BorderLayout.CENTER);
        configureBookTable();
        loadTableData();
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(buildActionArea(), BorderLayout.NORTH);
        content.add(buildTableArea(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildActionArea() {
        JPanel actionArea = new JPanel(new GridBagLayout());
        actionArea.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 16, 16);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        actionArea.add(createCard("Add Book", new JTextField[] { isbnField, titleField, copiesField, authorField },
                new String[] { "Add" }, event -> addBook()), constraints);

        constraints.gridx = 1;
        actionArea.add(createCard("Update Copies", new JTextField[] { updateIsbnField, updateCountField },
                new String[] { "Add Copies", "Reduce Copies" }, event -> updateCopies(event.getActionCommand())),
                constraints);

        constraints.gridx = 2;
        actionArea.add(createCard("Delete Book", new JTextField[] { deleteIsbnField }, new String[] { "Delete" },
                event -> deleteBook()), constraints);

        constraints.gridx = 3;
        actionArea.add(createCard("Search", new JTextField[] { searchTitleField, searchIsbnField },
                new String[] { "Search", "Show All" },
                event -> handleSearchButtons(event.getActionCommand())), constraints);

        return actionArea;
    }

    private JPanel createCard(String title, JTextField[] fields, String[] buttons,
            java.awt.event.ActionListener actionListener) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 12, 0);
        c.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(title);
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        card.add(label, c);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 10, 0);

        int row = 1;
        for (JTextField field : fields) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            card.add(field, c);
            row++;
        }

        JPanel buttonRow = new JPanel(new GridLayout(1, buttons.length, 10, 0));
        buttonRow.setOpaque(false);
        for (String buttonText : buttons) {
            JButton button = createButton(buttonText);
            button.addActionListener(actionListener);
            button.setActionCommand(buttonText);
            buttonRow.add(button);
        }

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.insets = new Insets(6, 0, 0, 0);
        card.add(buttonRow, c);

        return card;
    }

    private JPanel buildTableArea() {
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Books Inventory");
        title.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(event -> refreshTable());
        actions.add(refreshButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));

        tableCard.add(header, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
    }

    private void configureBookTable() {
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(228, 230, 234));
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(true);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setSelectionBackground(new Color(219, 232, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(String.class, centerRenderer);
        table.setDefaultRenderer(Integer.class, centerRenderer);

        table.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control C"), "copySelectedCells");
        table.getActionMap().put("copySelectedCells", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                copySelectedCellsToClipboard();
            }
        });
    }

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(new Color(145, 152, 161));
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

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
        return button;
    }

    private void addBook() {
        String isbn = readValue(isbnField, ISBN_PLACEHOLDER);
        String title = readValue(titleField, TITLE_PLACEHOLDER);
        String author = readValue(authorField, AUTHOR_PLACEHOLDER);
        String copiesText = readValue(copiesField, COPIES_PLACEHOLDER);

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || copiesText.isEmpty()) {
            showMessage("Please fill all book fields.");
            return;
        }

        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            showMessage("ISBN must follow the same format, for example 978-0-13-235088-4.");
            return;
        }

        if (!TEXT_PATTERN.matcher(title).matches()) {
            showMessage("Title must contain letters and spaces only.");
            return;
        }

        if (!TEXT_PATTERN.matcher(author).matches()) {
            showMessage("Author name must contain letters and spaces only.");
            return;
        }

        try {
            int copies = Integer.parseInt(copiesText);
            if (copies < 1 || copies > 100) {
                showMessage("Copies must be between 1 and 100.");
                return;
            }

            ServiceResult result = bookController.addBook(isbn, title, author, copies);
            showMessage(result.getMessage());
            if (result.isSuccess()) {
                clearField(isbnField, ISBN_PLACEHOLDER);
                clearField(titleField, TITLE_PLACEHOLDER);
                clearField(authorField, AUTHOR_PLACEHOLDER);
                clearField(copiesField, COPIES_PLACEHOLDER);
                refreshTable();
            }
        } catch (NumberFormatException exception) {
            showMessage("Copies must be a valid number.");
        }
    }

    private void updateCopies(String command) {
        String isbn = readValue(updateIsbnField, UPDATE_ISBN_PLACEHOLDER);
        String countText = readValue(updateCountField, UPDATE_COUNT_PLACEHOLDER);

        if (isbn.isEmpty() || countText.isEmpty()) {
            showMessage("Please enter ISBN and a copy count.");
            return;
        }

        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            showMessage("ISBN must follow the same format, for example 978-0-13-235088-4.");
            return;
        }

        try {
            int count = Integer.parseInt(countText);
            if (count < 1 || count > 100) {
                showMessage("Count must be between 1 and 100.");
                return;
            }

            ServiceResult result = "Add Copies".equals(command)
                    ? bookController.addCopies(isbn, count)
                    : bookController.reduceCopies(isbn, count);
            showMessage(result.getMessage());
            if (result.isSuccess()) {
                clearField(updateIsbnField, UPDATE_ISBN_PLACEHOLDER);
                clearField(updateCountField, UPDATE_COUNT_PLACEHOLDER);
                refreshTable();
            }
        } catch (NumberFormatException exception) {
            showMessage("Count must be a valid number.");
        }
    }

    private void deleteBook() {
        String isbn = readValue(deleteIsbnField, DELETE_ISBN_PLACEHOLDER);
        if (isbn.isEmpty()) {
            showMessage("Please enter an ISBN.");
            return;
        }

        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            showMessage("ISBN must follow the same format, for example 978-0-13-235088-4.");
            return;
        }

        ServiceResult result = bookController.deleteBook(isbn);
        showMessage(result.getMessage());
        if (result.isSuccess()) {
            clearField(deleteIsbnField, DELETE_ISBN_PLACEHOLDER);
            refreshTable();
        }
    }

    private void handleSearchButtons(String command) {
        if ("Show All".equals(command)) {
            refreshTable();
            return;
        }

        searchBooks();
    }

    private void searchBooks() {
        String titleQuery = readValue(searchTitleField, SEARCH_TITLE_PLACEHOLDER);
        String isbnQuery = readValue(searchIsbnField, SEARCH_ISBN_PLACEHOLDER);

        if (titleQuery.isEmpty() && isbnQuery.isEmpty()) {
            showMessage("Enter a title or ISBN to search.");
            return;
        }

        List<Book> filtered = new ArrayList<>();
        ServiceResult allBooks = bookController.getAllBooks();
        if (!allBooks.isSuccess()) {
            showMessage(allBooks.getMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BookNode> nodes = (ArrayList<BookNode>) allBooks.getData();
        for (BookNode node : nodes) {
            Book book = node.book;
            boolean matchesTitle = !titleQuery.isEmpty()
                    && book.getTitle().toLowerCase().contains(titleQuery.toLowerCase());
            boolean matchesIsbn = !isbnQuery.isEmpty()
                    && book.getIsbn().toLowerCase().contains(isbnQuery.toLowerCase());
            if (matchesTitle || matchesIsbn) {
                filtered.add(book);
            }
        }

        if (filtered.isEmpty()) {
            showMessage("No books matched your search.");
        }
        tableModel.setBooks(filtered);
    }

    private void copySelectedCellsToClipboard() {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();
        if (selectedRows.length == 0 || selectedColumns.length == 0) {
            return;
        }

        StringBuilder clipboardText = new StringBuilder();
        for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
            if (rowIndex > 0) {
                clipboardText.append(System.lineSeparator());
            }

            for (int columnIndex = 0; columnIndex < selectedColumns.length; columnIndex++) {
                if (columnIndex > 0) {
                    clipboardText.append('\t');
                }

                Object value = table.getValueAt(selectedRows[rowIndex], selectedColumns[columnIndex]);
                if (value != null) {
                    clipboardText.append(value);
                }
            }
        }

        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(clipboardText.toString()), null);
    }

    private void loadTableData() {
        ServiceResult result = bookController.getAllBooks();
        if (!result.isSuccess()) {
            tableModel.setBooks(new ArrayList<>());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BookNode> nodes = (ArrayList<BookNode>) result.getData();
        ArrayList<Book> books = new ArrayList<>();
        for (BookNode node : nodes) {
            books.add(node.book);
        }
        books.sort(Comparator.comparing(Book::getIsbn));
        tableModel.setBooks(books);
    }

    public void refreshTable() {
        loadTableData();
    }

    private String readValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        return placeholder.equals(value) ? "" : value;
    }

    private void clearField(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(145, 152, 161));
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
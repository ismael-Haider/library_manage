package library_manage.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import library_manage.Model.User;
import library_manage.controller.TransactionController;
import library_manage.util.ServiceResult;
import library_manage.view.tables.WaitingListTableModel;

public class TransactionPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 242, 243);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color PRIMARY = new Color(49, 92, 170);

    private final TransactionController transactionController;
    private final WaitingListTableModel waitingListTableModel = new WaitingListTableModel();
    private final JTable waitingListTable = new JTable(waitingListTableModel);

    private final JTextField userNameField = createField("User name");
    private final JTextField isbnField = createField("ISBN");
    private final JCheckBox graduateCheckBox = new JCheckBox("Graduate user");
    private final JTextField waitingIsbnField = createField("ISBN for waiting list");

    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel userCountLabel = new JLabel();
    private final JLabel borrowCountLabel = new JLabel();

    public TransactionPanel(TransactionController transactionController) {
        this.transactionController = transactionController;
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    private JPanel buildTopSection() {
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setOpaque(false);
        container.add(buildActionCard(), BorderLayout.NORTH);
        container.add(buildSummaryCard(), BorderLayout.CENTER);
        return container;
    }

    private JPanel buildActionCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Borrow and Return");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        form.setOpaque(false);
        userNameField.setColumns(18);
        isbnField.setColumns(18);
        form.add(userNameField);
        form.add(graduateCheckBox);
        form.add(isbnField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        JButton borrowButton = createButton("Borrow");
        borrowButton.addActionListener(event -> borrowBook());
        JButton returnButton = createButton("Return");
        returnButton.addActionListener(event -> returnBook());
        buttons.add(borrowButton);
        buttons.add(returnButton);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(92, 100, 110));

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSummaryCard() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        card.add(createSummaryChip("Users", userCountLabel));
        card.add(createSummaryChip("Borrow records", borrowCountLabel));
        card.add(statusLabel);
        return card;
    }

    private JPanel createSummaryChip(String title, JLabel valueLabel) {
        JPanel chip = new JPanel(new BorderLayout());
        chip.setBackground(new Color(248, 250, 252));
        chip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 233, 238)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(92, 100, 110));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(PRIMARY);

        chip.add(titleLabel, BorderLayout.NORTH);
        chip.add(valueLabel, BorderLayout.CENTER);
        return chip;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Waiting List");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        waitingIsbnField.setColumns(18);
        JButton searchButton = createButton("search Waiting List");
        searchButton.addActionListener(event -> searchWaitingList());
        controls.add(waitingIsbnField);
        controls.add(searchButton);
        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        waitingListTable.setRowHeight(34);
        waitingListTable.setShowGrid(true);
        waitingListTable.setGridColor(new Color(228, 230, 234));
        waitingListTable.setFillsViewportHeight(true);
        waitingListTable.getTableHeader().setReorderingAllowed(false);
        waitingListTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        waitingListTable.getTableHeader().setBackground(PRIMARY);
        waitingListTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        waitingListTable.setDefaultRenderer(String.class, centerRenderer);
        waitingListTable.setDefaultRenderer(Integer.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(waitingListTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
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
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private void borrowBook() {
        String userName = readValue(userNameField, "User name");
        String isbn = readValue(isbnField, "ISBN");
        if (userName.isEmpty() || isbn.isEmpty()) {
            statusLabel.setText("Enter user name and ISBN.");
            return;
        }

        ServiceResult result = transactionController.borrowBook(userName, graduateCheckBox.isSelected(), isbn);
        System.out.println(graduateCheckBox.isSelected());
        statusLabel.setText(result.getMessage());

        // Update the waiting list search field with the ISBN
        waitingIsbnField.setText(isbn);

        // Refresh counts but don't clear the table yet
        userCountLabel.setText(String.valueOf(transactionController.getTotalUsers()));
        borrowCountLabel.setText(String.valueOf(transactionController.getBorrowRecordCount()));

        // If user was added to waiting list, automatically show the waiting list
        if (!result.isSuccess() && result.getMessage().contains("Added to waiting list")) {
            searchWaitingListAutumaticaly(isbn);
        } else {
            // If not added to waiting list, clear the table
            waitingListTableModel.setUsers(new ArrayList<>());
        }

        // refreshAll();
        // waitingIsbnField.setText(isbn);
        // if (!result.isSuccess() && result.getMessage().contains("Added to waiting
        // list")) {
        // searchWaitingListAutumaticaly(isbn);
        // }
    }

    private void returnBook() {
        String userName = readValue(userNameField, "User name");
        String isbn = readValue(isbnField, "ISBN");
        if (userName.isEmpty() || isbn.isEmpty()) {
            statusLabel.setText("Enter user name and ISBN.");
            return;
        }

        ServiceResult result = transactionController.returnBook(userName, isbn);
        statusLabel.setText(result.getMessage());

        // Update the waiting list search field with the ISBN
        waitingIsbnField.setText(isbn);

        // Refresh counts but don't clear the table yet
        userCountLabel.setText(String.valueOf(transactionController.getTotalUsers()));
        borrowCountLabel.setText(String.valueOf(transactionController.getBorrowRecordCount()));

        // If book was returned, refresh the waiting list to show next user
        if (result.isSuccess()) {
            searchWaitingListAutumaticaly(isbn);
        } else {
            // If return failed, clear the table
            waitingListTableModel.setUsers(new ArrayList<>());
        }

        // refreshAll();
        // // If book was returned, refresh the waiting list to show next user
        // if (result.isSuccess()) {
        // searchWaitingListAutumaticaly(isbn);
        // }
    }

    // public void refreshAll() {
    // userCountLabel.setText(String.valueOf(transactionController.getTotalUsers()));
    // borrowCountLabel.setText(String.valueOf(transactionController.getBorrowRecordCount()));
    // waitingListTableModel.setUsers(new ArrayList<>());
    // }

    private void searchWaitingList() {
        String isbn = readValue(waitingIsbnField, "ISBN for waiting list");
        if (isbn.isEmpty()) {
            statusLabel.setText("Enter an ISBN for the waiting list.");
            return;
        }

        ServiceResult result = transactionController.getWaitingList(isbn);
        if (!result.isSuccess()) {
            waitingListTableModel.setUsers(new ArrayList<>());
            statusLabel.setText(result.getMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<User> users = (ArrayList<User>) result.getData();
        waitingListTableModel.setUsers(users);
        statusLabel.setText("Waiting list searched.");
    }

    private void searchWaitingListAutumaticaly(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            waitingListTableModel.setUsers(new ArrayList<>());
            statusLabel.setText("Enter an ISBN to view waiting list");
            return;
        }

        ServiceResult result = transactionController.getWaitingList(isbn.trim());
        if (!result.isSuccess()) {
            waitingListTableModel.setUsers(new ArrayList<>());
            // Don't change status label if it's just empty
            if (!statusLabel.getText().contains("searched")) {
                statusLabel.setText(result.getMessage());
            }
            return;
        }

        @SuppressWarnings("unchecked")
        ArrayList<User> users = (ArrayList<User>) result.getData();
        waitingListTableModel.setUsers(users);
        statusLabel.setText("Waiting list for ISBN: " + isbn + " (" + users.size() + " users)");
    }

    private String readValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        return placeholder.equals(value) ? "" : value;
    }
}
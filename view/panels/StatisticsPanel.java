package library_manage.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import library_manage.Model.Author;
import library_manage.Model.BookNode;
import library_manage.controller.ReportController;
import library_manage.controller.TransactionController;
import library_manage.util.ServiceResult;

public class StatisticsPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 242, 243);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color PRIMARY = new Color(49, 92, 170);

    private final TransactionController transactionController;
    private final ReportController reportController;

    private final JLabel totalBooksLabel = new JLabel();
    private final JLabel availableBooksLabel = new JLabel();
    private final JLabel totalUsersLabel = new JLabel();
    private final JLabel borrowCountLabel = new JLabel();
    private final JTextArea topBooksArea = new JTextArea();
    private final JTextArea topAuthorsArea = new JTextArea();

    public StatisticsPanel(TransactionController transactionController, ReportController reportController) {
        this.transactionController = transactionController;
        this.reportController = reportController;
        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        refreshStatistics();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Statistics");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JButton refresh = createButton("Refresh Stats");
        refresh.addActionListener(event -> refreshStatistics());

        header.add(title, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel summaryGrid = new JPanel(new java.awt.GridLayout(2, 2, 16, 16));
        summaryGrid.setOpaque(false);
        summaryGrid.add(createStatCard("Total Books", totalBooksLabel));
        summaryGrid.add(createStatCard("Available Books", availableBooksLabel));
        summaryGrid.add(createStatCard("Total Users", totalUsersLabel));
        summaryGrid.add(createStatCard("Borrow Records", borrowCountLabel));

        JPanel lower = new JPanel(new GridLayout(1, 2, 16, 0));
        lower.setOpaque(false);
        lower.add(createTextCard("Most Borrowed Books", topBooksArea, "Top 10 by borrow count"));
        lower.add(createTextCard("Most Read Authors", topAuthorsArea, "Top authors by reader count"));

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.add(summaryGrid, BorderLayout.NORTH);
        wrapper.add(lower, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(92, 100, 110));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(PRIMARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTextCard(String title, JTextArea area, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sublabel = new JLabel(subtitle);
        sublabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sublabel.setForeground(new Color(92, 100, 110));
        header.add(label, BorderLayout.WEST);
        header.add(sublabel, BorderLayout.EAST);

        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(new JScrollPane(area), BorderLayout.CENTER);
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

    public void refreshStatistics() {
        totalBooksLabel.setText(String.valueOf(transactionController.getTotalBooks()));
        availableBooksLabel.setText(String.valueOf(transactionController.getAvailableBooks()));
        totalUsersLabel.setText(String.valueOf(transactionController.getTotalUsers()));
        borrowCountLabel.setText(String.valueOf(transactionController.getBorrowRecordCount()));

        topBooksArea.setText(renderBooks(reportController.getMostBorrowedBooks()));
        topAuthorsArea.setText(renderAuthors(reportController.getMostReadAuthors()));
    }

    private String renderBooks(ServiceResult result) {
        if (!result.isSuccess()) {
            return result.getMessage();
        }

        @SuppressWarnings("unchecked")
        ArrayList<BookNode> books = (ArrayList<BookNode>) result.getData();
        StringBuilder builder = new StringBuilder();
        for (BookNode node : books) {
            builder.append(node.book.getTitle()).append(" | borrowed: ")
                    .append(node.book.getBorrowedCount()).append('\n');
        }
        return builder.length() == 0 ? "No data" : builder.toString();
    }

    private String renderAuthors(ServiceResult result) {
        if (!result.isSuccess()) {
            return result.getMessage();
        }

        @SuppressWarnings("unchecked")
        ArrayList<Author> authors = (ArrayList<Author>) result.getData();
        StringBuilder builder = new StringBuilder();
        for (Author author : authors) {
            builder.append(author.getName()).append(" | readers: ")
                    .append(author.getNumberOfReaders()).append('\n');
        }
        return builder.length() == 0 ? "No data" : builder.toString();
    }
}
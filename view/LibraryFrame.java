package library_manage.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import library_manage.controller.BookController;
import library_manage.controller.ReportController;
import library_manage.controller.TransactionController;
import library_manage.services.AuthorServices;
import library_manage.services.BookServices;
import library_manage.services.BorrowServices;
import library_manage.services.UserServices;
import library_manage.view.components.SidebarPanel;
import library_manage.view.panels.BookManagementPanel;
import library_manage.view.panels.ReportPanel;
import library_manage.view.panels.StatisticsPanel;
import library_manage.view.panels.TransactionPanel;

public class LibraryFrame extends JFrame {

    private static final String CARD_BOOKS = "books";
    private static final String CARD_TRANSACTIONS = "transactions";
    private static final String CARD_REPORTS = "reports";
    private static final String CARD_STATISTICS = "statistics";
    private final JPanel contentPanel = new JPanel(new java.awt.CardLayout());
    private final java.awt.CardLayout contentLayout = (java.awt.CardLayout) contentPanel.getLayout();
    private final AuthorServices authorServices = new AuthorServices();
    private final BookServices bookServices = new BookServices(authorServices);
    private final UserServices userServices = new UserServices();
    private final BorrowServices borrowServices = new BorrowServices(bookServices, userServices);
    private final BookController bookController = new BookController(bookServices, authorServices);
    private final TransactionController transactionController = new TransactionController(
            bookServices, authorServices, userServices, borrowServices);
    private final ReportController reportController = new ReportController(transactionController);

    public LibraryFrame() {
        configureFrame();
        seedDemoBooksIfNeeded();
        buildUi();
    }

    private void configureFrame() {
        setTitle("Library Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 760));
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(Paths.get("util", "icons", "book_icon.png").toString()).getImage().getScaledInstance(
                64, 64, Image.SCALE_SMOOTH));
        setLayout(new BorderLayout());
    }

    private void buildUi() {
        SidebarPanel sidebar = new SidebarPanel(this::showCard);
        sidebar.setPreferredSize(new Dimension(260, 0));

        JPanel header = buildHeader();

        BookManagementPanel bookPanel = new BookManagementPanel(bookController);
        contentPanel.setBackground(new Color(241, 242, 243));
        contentPanel.add(bookPanel, CARD_BOOKS);
        contentPanel.add(new TransactionPanel(transactionController), CARD_TRANSACTIONS);
        contentPanel.add(new ReportPanel(reportController), CARD_REPORTS);
        contentPanel.add(new StatisticsPanel(transactionController, reportController), CARD_STATISTICS);

        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(new Color(241, 242, 243));
        rightSide.add(header, BorderLayout.NORTH);
        rightSide.add(contentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(rightSide, BorderLayout.CENTER);

        sidebar.setActive(CARD_BOOKS);
        showCard(CARD_BOOKS);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 228, 232)));

        JLabel title = new JLabel("Library Dashboard");
        title.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel subtitle = new JLabel("Manage books, inventory, and reporting from one place", SwingConstants.RIGHT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 109, 120));

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private void showCard(String cardName) {
        contentLayout.show(contentPanel, cardName);
    }

    private void seedDemoBooks() {
        bookController.addBook("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 12);
        bookController.addBook("978-0-201-63361-0", "Design Patterns", "Erich Gamma", 8);
        bookController.addBook("978-0-596-00573-1", "Head First Java", "Kathy Sierra", 5);
        bookController.addBook("978-0-321-35668-0", "Effective Java", "Joshua Bloch", 3);
        bookController.addBook("978-0-470-12686-4", "Java For Dummies", "Barry Burd", 1);
    }

    private void seedDemoBooksIfNeeded() {
        if (bookServices.getBookCount() == 0) {
            seedDemoBooks();
        }
    }
}
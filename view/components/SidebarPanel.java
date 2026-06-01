package library_manage.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SidebarPanel extends JPanel {

    private static final Color BASE = new Color(17, 39, 72);
    private static final Color ACCENT = new Color(63, 128, 255);

    private final Map<String, JButton> buttons = new LinkedHashMap<>();

    public SidebarPanel(Consumer<String> navigationHandler) {
        setLayout(new BorderLayout());
        setBackground(BASE);
        setBorder(BorderFactory.createEmptyBorder(24, 18, 24, 18));

        JLabel title = new JLabel("Library Manage");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Control center");
        subtitle.setForeground(new Color(185, 197, 217));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));
        branding.add(title);
        branding.add(Box.createVerticalStrut(6));
        branding.add(subtitle);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));

        addButton(menu, "books", "Books", navigationHandler);
        addButton(menu, "transactions", "Transactions", navigationHandler);
        addButton(menu, "reports", "Reports", navigationHandler);
        addButton(menu, "statistics", "Statistics", navigationHandler);

        add(branding, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
    }

    private void addButton(JPanel container, String key, String label, Consumer<String> navigationHandler) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BASE);
        button.setOpaque(true);
        button.addActionListener(event -> {
            setActive(key);
            navigationHandler.accept(key);
        });

        buttons.put(key, button);
        container.add(button);
        container.add(Box.createVerticalStrut(10));
    }

    public void setActive(String key) {
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            JButton button = entry.getValue();
            if (entry.getKey().equals(key)) {
                button.setBackground(ACCENT);
            } else {
                button.setBackground(BASE);
            }
        }
    }
}
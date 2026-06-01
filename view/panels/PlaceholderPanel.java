package library_manage.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title, String description) {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 242, 243));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                BorderFactory.createEmptyBorder(42, 36, 42, 36)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel descriptionLabel = new JLabel("<html><div style='text-align:center;'>" + description + "</div></html>",
                SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descriptionLabel.setForeground(new Color(96, 106, 118));

        card.add(titleLabel, BorderLayout.CENTER);
        card.add(descriptionLabel, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
    }
}
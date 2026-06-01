package library_manage;

import javax.swing.SwingUtilities;
import library_manage.view.LibraryFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryFrame().setVisible(true));
    }
}

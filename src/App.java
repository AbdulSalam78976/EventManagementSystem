import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.sql.SQLException;

import screens.LoginScreen;

/**
 * Main entry point for the Event Management System
 */
public class App {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error setting system look and feel: " + e.getMessage(),
                "Initialization Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Launch the application with the login screen
        SwingUtilities.invokeLater(() -> {
            try {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing login screen: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

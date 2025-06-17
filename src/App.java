import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import screens.LoginScreen;
import utils.NetworkUtils;

/**
 * Main entry point for the Event Management System
 */
public class App {
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        // Check internet connection
        if (!NetworkUtils.isInternetAvailable()) {
            JOptionPane.showMessageDialog(null,
                "No internet connection detected. Please check your connection and try again.",
                "Network Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

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

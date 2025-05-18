package components;

import javax.swing.*;
import java.awt.*;
import utils.AppColors;
import utils.UIConstants;

/**
 * A reusable loading panel component that shows a loading spinner and message
 */
public class LoadingPanel extends JPanel {
    private final JLabel messageLabel;
    private final JLabel spinnerLabel;
    private Timer spinnerTimer;
    private int spinnerIndex = 0;
    private static final String[] SPINNER_FRAMES = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};

    /**
     * Creates a new loading panel with a default message
     */
    public LoadingPanel() {
        this("Loading...");
    }

    /**
     * Creates a new loading panel with a custom message
     * 
     * @param message The message to display while loading
     */
    public LoadingPanel(String message) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spinner label
        spinnerLabel = new JLabel(SPINNER_FRAMES[0]);
        spinnerLabel.setFont(UIConstants.TITLE_FONT);
        spinnerLabel.setForeground(AppColors.PRIMARY);
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(spinnerLabel);

        // Add some vertical space
        add(Box.createVerticalStrut(10));

        // Message label
        messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.BODY_FONT);
        messageLabel.setForeground(AppColors.TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(messageLabel);

        // Start the spinner animation
        startSpinner();
    }

    /**
     * Starts the spinner animation
     */
    private void startSpinner() {
        spinnerTimer = new Timer(100, e -> {
            spinnerIndex = (spinnerIndex + 1) % SPINNER_FRAMES.length;
            spinnerLabel.setText(SPINNER_FRAMES[spinnerIndex]);
        });
        spinnerTimer.start();
    }

    /**
     * Stops the spinner animation
     */
    public void stopSpinner() {
        if (spinnerTimer != null) {
            spinnerTimer.stop();
        }
    }

    /**
     * Updates the loading message
     * 
     * @param message The new message to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Shows the loading panel in a glass pane
     * 
     * @param parent The parent component to show the loading panel over
     * @return The created loading panel
     */
    public static LoadingPanel showInGlassPane(Component parent) {
        JRootPane rootPane = SwingUtilities.getRootPane(parent);
        if (rootPane == null) return null;

        // Create glass pane if it doesn't exist
        if (!(rootPane.getGlassPane() instanceof JPanel)) {
            rootPane.setGlassPane(new JPanel(new BorderLayout()));
        }

        JPanel glassPane = (JPanel) rootPane.getGlassPane();
        glassPane.removeAll();
        glassPane.setLayout(new BorderLayout());
        glassPane.setOpaque(false);

        // Add semi-transparent background
        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setBackground(new Color(0, 0, 0, 128));
        glassPane.add(overlay, BorderLayout.CENTER);

        // Add loading panel
        LoadingPanel loadingPanel = new LoadingPanel();
        glassPane.add(loadingPanel, BorderLayout.CENTER);

        glassPane.setVisible(true);
        return loadingPanel;
    }

    /**
     * Hides the loading panel from the glass pane
     * 
     * @param parent The parent component
     */
    public static void hideFromGlassPane(Component parent) {
        JRootPane rootPane = SwingUtilities.getRootPane(parent);
        if (rootPane != null) {
            rootPane.getGlassPane().setVisible(false);
        }
    }
} 
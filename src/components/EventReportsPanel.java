package components;

import javax.swing.*;
import java.awt.*;
import utils.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Panel for displaying event reports and analytics
 */
public class EventReportsPanel extends JPanel {
    private final JPanel contentPanel;
    private final JLabel loadingLabel;
    private final JLabel errorLabel;
    
    /**
     * Creates a new EventReportsPanel
     */
    public EventReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Event Reports & Analytics",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        add(titleLabel, BorderLayout.NORTH);

        // Loading label
        loadingLabel = UIUtils.createLabel(
            "Loading reports...",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        loadingLabel.setVisible(false);
        add(loadingLabel, BorderLayout.CENTER);

        // Error label
        errorLabel = UIUtils.createLabel(
            "",
            UIConstants.BODY_FONT,
            AppColors.ERROR
        );
        errorLabel.setVisible(false);
        add(errorLabel, BorderLayout.CENTER);

        // Reports content
        contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(contentPanel, BorderLayout.CENTER);

        // Load initial data
        loadReports();
    }

    /**
     * Loads the event reports data
     */
    private void loadReports() {
        // Show loading state
        setLoading(true);
        
        // Start loading in background
        new Thread(() -> {
            try {
                // TODO: Load actual data from database
                // For now, using dummy data
                final String attendance = "85%";
                final String revenue = formatCurrency(12345);
                final String engagement = formatNumber(1234);
                final String rating = "4.5/5";
                
                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    updateReports(attendance, revenue, engagement, rating);
                    setLoading(false);
                });
            } catch (Exception e) {
                // Show error on EDT
                SwingUtilities.invokeLater(() -> {
                    showError("Error loading reports: " + e.getMessage());
                    setLoading(false);
                });
            }
        }).start();
    }

    /**
     * Updates the reports with new data
     */
    private void updateReports(String attendance, String revenue, String engagement, String rating) {
        contentPanel.removeAll();
        contentPanel.add(createReportCard("Event Attendance", attendance, "📊"));
        contentPanel.add(createReportCard("Revenue", revenue, "💰"));
        contentPanel.add(createReportCard("User Engagement", engagement, "👥"));
        contentPanel.add(createReportCard("Event Rating", rating, "⭐"));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Shows or hides the loading state
     */
    private void setLoading(boolean loading) {
        loadingLabel.setVisible(loading);
        contentPanel.setVisible(!loading);
        errorLabel.setVisible(false);
    }

    /**
     * Shows an error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        contentPanel.setVisible(false);
        loadingLabel.setVisible(false);
    }

    /**
     * Creates a report card with the given title, value, and icon
     */
    private JPanel createReportCard(String title, String value, String icon) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        card.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            20
        ));

        JLabel titleLabel = UIUtils.createLabel(
            icon + " " + title,
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = UIUtils.createLabel(
            value,
            UIConstants.HEADER_FONT,
            AppColors.PRIMARY
        );
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Formats a number with commas
     */
    private String formatNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }

    /**
     * Formats a currency amount
     */
    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    /**
     * Refreshes the reports data
     */
    public void refresh() {
        loadReports();
    }
} 
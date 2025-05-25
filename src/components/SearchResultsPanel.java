package components;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import models.Event;
import utils.*;

/**
 * Panel for displaying search results
 */
public class SearchResultsPanel extends JPanel {
    private final JPanel resultsPanel;
    private final Consumer<String[]> onEventClick;
    private final JLabel loadingLabel;
    private final JLabel errorLabel;

    /**
     * Creates a new SearchResultsPanel
     * 
     * @param onEventClick Callback for when an event is clicked
     */
    public SearchResultsPanel(Consumer<String[]> onEventClick) {
        this.onEventClick = onEventClick;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Search Results",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        add(titleLabel, BorderLayout.NORTH);

        // Loading label
        loadingLabel = UIUtils.createLabel(
            "Searching...",
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

        // Results container
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Sets the search results using Event objects
     * 
     * @param events List of events to display
     */
    public void setResults(List<Event> events) {
        if (events == null) {
            showError("Invalid search results");
            return;
        }

        setLoading(false);
        resultsPanel.removeAll();
        
        if (events.isEmpty()) {
            showNoResults();
        } else {
            for (Event event : events) {
                if (event == null) continue;
                java.util.List<JButton> buttons = new java.util.ArrayList<>();
                JButton viewBtn = UIUtils.createButton(
                    "View Details",
                    null,
                    UIUtils.ButtonType.SECONDARY,
                    UIUtils.ButtonSize.SMALL
                );
                viewBtn.addActionListener(e -> {
                    try {
                        Window owner = SwingUtilities.getWindowAncestor(this);
                        JDialog dialog;
                        if (owner instanceof Dialog) {
                            dialog = new JDialog((Dialog) owner, "Event Details", true);
                        } else {
                            dialog = new JDialog((Frame) owner, "Event Details", true);
                        }
                        dialog.setSize(700, 600);
                        dialog.setLocationRelativeTo(this);
                        dialog.setLayout(new BorderLayout());
                        dialog.add(new EventDetailsPanel(event), BorderLayout.CENTER);
                        JButton closeBtn = UIUtils.createButton("Close", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
                        closeBtn.addActionListener(ev -> dialog.dispose());
                        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        btnPanel.add(closeBtn);
                        dialog.add(btnPanel, BorderLayout.SOUTH);
                        dialog.setVisible(true);
                    } catch (Exception ex) {
                        showError("Error loading event details: " + ex.getMessage());
                    }
                });
                buttons.add(viewBtn);
                EventCard card = new EventCard(event, buttons);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultsPanel.add(card);
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /**
     * Sets the search results using string arrays
     * 
     * @param events Array of event data to display
     */
    public void setEvents(String[][] events) {
        if (events == null) {
            showError("Invalid search results");
            return;
        }

        setLoading(false);
        resultsPanel.removeAll();
        
        if (events.length == 0) {
            showNoResults();
        } else {
            for (String[] event : events) {
                if (event == null || event.length < 4) {
                    continue;
                }
                
                try {
                    RoundedPanel card = createEventCard(event);
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultsPanel.add(card);
                    resultsPanel.add(Box.createVerticalStrut(10));
                } catch (Exception e) {
                    // Skip invalid event data
                    continue;
                }
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /**
     * Creates an event card from event data
     */
    private RoundedPanel createEventCard(String[] event) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        card.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Event image placeholder
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(200, 200, 200));
        imagePanel.setPreferredSize(new Dimension(150, 150));
        card.add(imagePanel, BorderLayout.WEST);

        // Event details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        // Event title
        JLabel titleLabel = new JLabel(event[0]);
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Event date
        JLabel dateLabel = new JLabel("📅 " + event[1]);
        dateLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        // Event venue
        JLabel venueLabel = new JLabel("📍 " + event[2]);
        venueLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(venueLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Register button
        GradientButton actionButton = GradientButton.createPrimaryButton(event[3]);
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionButton.setPreferredSize(new Dimension(120, 35));
        actionButton.addActionListener(e -> {
            try {
                onEventClick.accept(event);
            } catch (Exception ex) {
                showError("Error handling event click: " + ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(actionButton);
        detailsPanel.add(buttonPanel);

        card.add(detailsPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Shows the no results message
     */
    private void showNoResults() {
        JLabel noResultsLabel = UIUtils.createLabel(
            "No events found matching your search criteria.",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(noResultsLabel);
    }

    /**
     * Shows or hides the loading state
     */
    private void setLoading(boolean loading) {
        loadingLabel.setVisible(loading);
        resultsPanel.setVisible(!loading);
        errorLabel.setVisible(false);
    }

    /**
     * Shows an error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        resultsPanel.setVisible(false);
        loadingLabel.setVisible(false);
    }

    /**
     * Clears all search results
     */
    public void clearResults() {
        setLoading(false);
        resultsPanel.removeAll();
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /**
     * Shows the loading state
     */
    public void showLoading() {
        setLoading(true);
    }
} 
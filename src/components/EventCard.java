package components;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import models.Event;
import utils.*;

public class EventCard extends RoundedPanel {
    private final Event event;
    private JLabel titleLabel;
    private JLabel dateLabel;
    private JLabel venueLabel;
    private JLabel slotsLabel;
    private JLabel imageLabel;
    private JButton viewButton;

    public EventCard(Event event) {
        super(new BorderLayout(15, 0), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        this.event = event;
        setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Date panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setOpaque(false);
        dateLabel = new JLabel(event.getEventDate().toLocalDate().toString());
        dateLabel.setIcon(new ImageIcon(getClass().getResource("/icons/calendar.png")));
        datePanel.add(dateLabel);

        // Venue panel
        JPanel venuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        venuePanel.setOpaque(false);
        venueLabel = new JLabel(event.getVenueName());
        venueLabel.setIcon(new ImageIcon(getClass().getResource("/icons/location.png")));
        venuePanel.add(venueLabel);

        // Slots panel
        JPanel slotsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        slotsPanel.setOpaque(false);
        slotsLabel = new JLabel("Available Slots: " + event.getTotalSlots());
        slotsLabel.setIcon(new ImageIcon(getClass().getResource("/icons/users.png")));
        slotsPanel.add(slotsLabel);

        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        if (event.getMainImage() != null) {
            ImageIcon icon = new ImageIcon(event.getMainImage());
            Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(image));
        } else {
            imageLabel = new JLabel("No image available");
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.add(titlePanel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(datePanel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(venuePanel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(slotsPanel);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(imagePanel, BorderLayout.WEST);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> onViewDetails());
        buttonPanel.add(viewButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleRegistration() {
        // TODO: Implement registration logic
        JOptionPane.showMessageDialog(this,
            "Registration functionality will be implemented soon.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void onViewDetails() {
        // TODO: Implement view details logic
        JOptionPane.showMessageDialog(this,
            "View details functionality will be implemented soon.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 
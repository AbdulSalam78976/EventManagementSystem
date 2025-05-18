package components;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import models.Event;
import utils.*;

public class EventCard extends RoundedPanel {
    private final Event event;

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

        // Event image placeholder
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(200, 200, 200));
        imagePanel.setPreferredSize(new Dimension(150, 150));
        add(imagePanel, BorderLayout.WEST);

        // Event details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        // Event title
        JLabel titleLabel = new JLabel(event.getName());
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Event date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        JLabel dateLabel = new JLabel("📅 " + event.getStartDateTime().format(dateFormatter));
        dateLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        JLabel timeLabel = new JLabel("🕒 " + event.getStartDateTime().format(timeFormatter) + " - " + event.getEndDateTime().format(timeFormatter));
        timeLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(timeLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        JLabel venueLabel = new JLabel("📍 " + event.getVenueName());
        venueLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(venueLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        int availableSlots = event.getCapacity() - event.getRegisteredCount();
        JLabel slotsLabel = new JLabel("👥 " + availableSlots + "/" + event.getCapacity() + " Slots Available");
        slotsLabel.setFont(UIConstants.BODY_FONT);
        detailsPanel.add(slotsLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Register button
        JButton registerButton = UIUtils.createButton("REGISTER", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> handleRegistration());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        detailsPanel.add(buttonPanel);

        add(detailsPanel, BorderLayout.CENTER);
    }

    private void handleRegistration() {
        // TODO: Implement registration logic
        JOptionPane.showMessageDialog(this,
            "Registration functionality will be implemented soon.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 
package components;

import models.Event;
import models.Event.EventStatus;
import models.User;
import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;
import controllers.AuthController;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class RecentEventItem extends RoundedPanel {
    private final JLabel titleLabel;
    private final JLabel dateLabel;
    private final JLabel venueLabel;
    private final JLabel statusLabel;
    private final JLabel participantsLabel;
    private final JButton viewDetailsButton;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    private Event event;

    public RecentEventItem(Event event, ActionListener onViewDetails) {
        super(new BorderLayout(10, 0), AppColors.BACKGROUND_LIGHT, UIConstants.CORNER_RADIUS_SMALL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.event = event;

        // Title label
        titleLabel = UIUtils.createLabel(
            event.getTitle(),
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        add(titleLabel, BorderLayout.NORTH);

        // Info panel (date, venue, status, participants)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);

        dateLabel = UIUtils.createLabel(
            "Date: " + (event.getEventDate() != null ?
                event.getEventDate().format(DATE_TIME_FORMATTER) : "N/A"),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );

        venueLabel = UIUtils.createLabel(
            "Venue: " + (event.getVenueName() != null ? event.getVenueName() : "N/A"),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );

        statusLabel = UIUtils.createLabel(
            "Status: " + event.getStatus().toString(),
            UIConstants.SMALL_FONT,
            getStatusColor(event.getStatus())
        );

        participantsLabel = UIUtils.createLabel(
            "Participants: " + (event.getTotalSlots() - event.getAvailableSlots()) + " / " + event.getTotalSlots(),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );

        infoPanel.add(dateLabel);
        infoPanel.add(venueLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(participantsLabel);
        add(infoPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        viewDetailsButton = UIUtils.createButton(
            "View Details",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.SMALL
        );
        if (onViewDetails != null) {
            viewDetailsButton.addActionListener(onViewDetails);
        }

        buttonPanel.add(viewDetailsButton);
        add(buttonPanel, BorderLayout.EAST);

        // Make the entire panel clickable
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof RecentEventItem) {
                    // Trigger the same action as the button
                    if (onViewDetails != null) {
                        onViewDetails.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "view_details_click"));
                    }
                }
            }
        });

        // Add cursor change on hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private Color getStatusColor(EventStatus status) {
        switch (status) {
            case APPROVED:
                return AppColors.SUCCESS;
            case PENDING:
                return AppColors.WARNING;
            case REJECTED:
                return AppColors.ERROR;
            case CANCELLED:
                return AppColors.TEXT_SECONDARY;
            case COMPLETED:
                return AppColors.PRIMARY;
            default:
                return AppColors.TEXT_PRIMARY;
        }
    }

    public void updateEvent(Event event) {
        this.event = event;
        titleLabel.setText(event.getTitle());
        dateLabel.setText("Date: " + (event.getEventDate() != null ?
            event.getEventDate().format(DATE_TIME_FORMATTER) : "N/A"));
        venueLabel.setText("Venue: " + (event.getVenueName() != null ? event.getVenueName() : "N/A"));
        statusLabel.setText("Status: " + event.getStatus().toString());
        statusLabel.setForeground(getStatusColor(event.getStatus()));
        participantsLabel.setText("Participants: " + (event.getTotalSlots() - event.getAvailableSlots()) + " / " + event.getTotalSlots());
    }
} 
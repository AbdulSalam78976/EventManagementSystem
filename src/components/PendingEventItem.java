package components;

import models.Event;
import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionListener;

public class PendingEventItem extends RoundedPanel {
    private final JLabel titleLabel;
    private final JLabel dateLabel;
    private final JLabel organizerLabel;
    private final JButton approveButton;
    private final JButton rejectButton;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public PendingEventItem(Event event, ActionListener onApprove, ActionListener onReject) {
        super(new BorderLayout(10, 0), AppColors.BACKGROUND_LIGHT, UIConstants.CORNER_RADIUS_SMALL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title label
        titleLabel = UIUtils.createLabel(
            event.getTitle(),
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        add(titleLabel, BorderLayout.NORTH);
        
        // Info panel (date and organizer)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        infoPanel.setOpaque(false);
        
        dateLabel = UIUtils.createLabel(
            "Date: " + (event.getEventDate() != null ? 
                event.getEventDate().format(DATE_FORMATTER) : "N/A"),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        
        organizerLabel = UIUtils.createLabel(
            "Organizer: " + (event.getOrganizer() != null ? 
                event.getOrganizer().getName() : "Unknown"),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        
        infoPanel.add(dateLabel);
        infoPanel.add(organizerLabel);
        add(infoPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        approveButton = UIUtils.createButton(
            "Approve",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.SMALL
        );
        if (onApprove != null) {
            approveButton.addActionListener(onApprove);
        }
        
        rejectButton = UIUtils.createButton(
            "Reject",
            null,
            UIUtils.ButtonType.ERROR,
            UIUtils.ButtonSize.SMALL
        );
        if (onReject != null) {
            rejectButton.addActionListener(onReject);
        }
        
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    public void updateEvent(Event event) {
        titleLabel.setText(event.getTitle());
        dateLabel.setText("Date: " + (event.getEventDate() != null ? 
            event.getEventDate().format(DATE_FORMATTER) : "N/A"));
        organizerLabel.setText("Organizer: " + (event.getOrganizer() != null ? 
            event.getOrganizer().getName() : "Unknown"));
    }

    public void setApproveEnabled(boolean enabled) {
        approveButton.setEnabled(enabled);
    }

    public void setRejectEnabled(boolean enabled) {
        rejectButton.setEnabled(enabled);
    }
} 
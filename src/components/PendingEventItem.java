package components;

import models.Event;
import screens.AdminDashboardNew;
import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PendingEventItem extends RoundedPanel {
    private final JLabel titleLabel;
    private final JLabel dateLabel;
    private final JLabel organizerLabel;
    private final JButton approveButton;
    private final JButton rejectButton;
    private final JButton viewDetailsButton;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private Event event;

    public PendingEventItem(Event event, ActionListener onApprove, ActionListener onReject) {
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
        
        viewDetailsButton = UIUtils.createButton(
            "View Details",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.SMALL
        );
        viewDetailsButton.addActionListener(e -> showEventDetails());
        
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
        
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.EAST);

        // Make the entire panel clickable
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof PendingEventItem) {
                    showEventDetails();
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

    private void showEventDetails() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof AdminDashboardNew) {
            ((AdminDashboardNew) parentWindow).showEventDetails(event);
        }
    }

    public void updateEvent(Event event) {
        this.event = event;
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
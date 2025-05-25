package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import models.Event;
import utils.AppColors;
import utils.EmojiUtils;
import utils.UIConstants;
import utils.UIUtils;

public class EventCard extends RoundedPanel {
    private final Event event;
    private JLabel titleLabel;
    private JLabel categoryLabel;
    private JLabel descriptionLabel;
    private JLabel imageLabel;
    private JPanel buttonPanel;

    public EventCard(Event event, java.util.List<JButton> actionButtons) {
        super(new BorderLayout(15, 0), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        this.event = event;
        setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        initializeComponents(actionButtons);
    }

    private void initializeComponents(java.util.List<JButton> actionButtons) {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);

        // --- Image/Icon ---
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setOpaque(false);
        if (event.getMainImage() != null) {
            ImageIcon icon = new ImageIcon(event.getMainImage());
            Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(image));
        } else {
            // Use emoji placeholder based on event category
            String categoryEmoji = EmojiUtils.getEventCategoryEmoji(event.getCategory());
            imageLabel = new JLabel(categoryEmoji);
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(100, 100));
            imageLabel.setBackground(new Color(248, 249, 250));
            imageLabel.setOpaque(true);
            imageLabel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // --- Details ---
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        // Title
        titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        detailsPanel.add(titleLabel);

        // Category badge
        String category = event.getCategory() != null ? event.getCategory() : "Uncategorized";
        categoryLabel = new JLabel(category.toUpperCase());
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryLabel.setOpaque(true);
        categoryLabel.setBackground(new Color(220, 235, 255));
        categoryLabel.setForeground(AppColors.PRIMARY_DARK);
        categoryLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(categoryLabel);

        // Description (short preview)
        String desc = event.getDescription() != null ? event.getDescription() : "";
        if (desc.length() > 100) desc = desc.substring(0, 100) + "...";
        descriptionLabel = new JLabel("<html><div style='width:220px;'>" + desc + "</div></html>");
        descriptionLabel.setFont(UIConstants.BODY_FONT);
        descriptionLabel.setForeground(AppColors.TEXT_SECONDARY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(descriptionLabel);

        // --- Button Panel ---
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        if (actionButtons != null) {
            for (JButton btn : actionButtons) {
                buttonPanel.add(btn);
            }
        }

        // --- Layout ---
        add(imagePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
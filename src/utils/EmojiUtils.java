package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Utility class for creating emoji-based icons and labels throughout the application
 * Replaces traditional ImageIcon usage with emoji text for a modern, lightweight approach
 */
public class EmojiUtils {

    // Prevent instantiation
    private EmojiUtils() {}

    // Standard emoji font for consistent rendering
    public static final Font EMOJI_FONT_SMALL = new Font("Segoe UI Emoji", Font.PLAIN, 12);
    public static final Font EMOJI_FONT_NORMAL = new Font("Segoe UI Emoji", Font.PLAIN, 16);
    public static final Font EMOJI_FONT_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 24);
    public static final Font EMOJI_FONT_EXTRA_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 32);

    /**
     * Creates a JLabel with emoji icon and text
     * @param emoji The emoji character(s)
     * @param text The text to display (can be null)
     * @param fontSize The font size for the emoji
     * @return JLabel with emoji and text
     */
    public static JLabel createEmojiLabel(String emoji, String text, int fontSize) {
        String displayText = text != null ? emoji + " " + text : emoji;
        JLabel label = new JLabel(displayText);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        return label;
    }

    /**
     * Creates a JButton with emoji icon and text
     * @param emoji The emoji character(s)
     * @param text The button text
     * @param fontSize The font size
     * @return JButton with emoji and text
     */
    public static JButton createEmojiButton(String emoji, String text, int fontSize) {
        String displayText = text != null ? emoji + " " + text : emoji;
        JButton button = new JButton(displayText);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        return button;
    }

    /**
     * Creates a JLabel with just emoji (no text)
     * @param emoji The emoji character(s)
     * @param fontSize The font size
     * @return JLabel with emoji only
     */
    public static JLabel createEmojiIcon(String emoji, int fontSize) {
        JLabel label = new JLabel(emoji);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Sets emoji font on an existing component
     * @param component The component to update
     * @param fontSize The font size
     */
    public static void setEmojiFont(JComponent component, int fontSize) {
        component.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
    }

    /**
     * Creates a panel with emoji icon and content
     * @param emoji The emoji character(s)
     * @param content The content panel
     * @param iconSize The size of the emoji icon
     * @return Panel with emoji icon and content
     */
    public static JPanel createEmojiPanel(String emoji, JComponent content, int iconSize) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel iconLabel = createEmojiIcon(emoji, iconSize);
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a status indicator with emoji and text
     * @param emoji The status emoji
     * @param status The status text
     * @param color The text color
     * @return JLabel with status indicator
     */
    public static JLabel createStatusLabel(String emoji, String status, Color color) {
        JLabel label = new JLabel(emoji + " " + status);
        label.setFont(EMOJI_FONT_NORMAL);
        label.setForeground(color);
        return label;
    }

    /**
     * Gets the appropriate emoji for event status
     * @param status The event status
     * @return Corresponding emoji
     */
    public static String getEventStatusEmoji(String status) {
        switch (status.toUpperCase()) {
            case "APPROVED": return "✅";
            case "PENDING": return "⏳";
            case "REJECTED": return "❌";
            case "CANCELLED": return "🚫";
            case "COMPLETED": return "🏁";
            case "ONGOING": return "🔄";
            case "DRAFT": return "📝";
            default: return "❓";
        }
    }

    /**
     * Gets the appropriate emoji for registration status
     * @param status The registration status
     * @return Corresponding emoji
     */
    public static String getRegistrationStatusEmoji(String status) {
        switch (status.toUpperCase()) {
            case "APPROVED": return "✅";
            case "PENDING": return "⏳";
            case "CANCELLED": return "❌";
            case "REJECTED": return "❌";
            case "CONFIRMED": return "✅";
            case "WAITLISTED": return "⏰";
            default: return "❓";
        }
    }

    /**
     * Gets the appropriate emoji for user role
     * @param role The user role
     * @return Corresponding emoji
     */
    public static String getUserRoleEmoji(String role) {
        switch (role.toUpperCase()) {
            case "ADMIN": return "👑";
            case "ORGANIZER": return "🎯";
            case "ATTENDEE": return "👤";
            case "STUDENT": return "🎓";
            case "FACULTY": return "👨‍🏫";
            case "STAFF": return "👔";
            default: return "👤";
        }
    }

    /**
     * Gets the appropriate emoji for event category
     * @param category The event category
     * @return Corresponding emoji
     */
    public static String getEventCategoryEmoji(String category) {
        if (category == null) return "📅";

        switch (category.toLowerCase()) {
            case "academic": return "🎓";
            case "sports": return "⚽";
            case "cultural": return "🎭";
            case "technical": return "💻";
            case "workshop": return "🔧";
            case "seminar": return "💼";
            case "conference": return "🏢";
            case "social": return "🎉";
            case "competition": return "🏆";
            case "exhibition": return "🖼️";
            case "meeting": return "👥";
            case "training": return "📚";
            default: return "📅";
        }
    }

    /**
     * Gets the appropriate emoji for file type
     * @param fileName The file name or extension
     * @return Corresponding emoji
     */
    public static String getFileTypeEmoji(String fileName) {
        if (fileName == null) return "📄";

        String extension = fileName.toLowerCase();
        if (extension.contains(".")) {
            extension = extension.substring(extension.lastIndexOf("."));
        }

        switch (extension) {
            case ".pdf": return "📑";
            case ".doc":
            case ".docx": return "📝";
            case ".xls":
            case ".xlsx": return "📊";
            case ".ppt":
            case ".pptx": return "📈";
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".gif":
            case ".bmp": return "🖼️";
            case ".mp4":
            case ".avi":
            case ".mov":
            case ".wmv": return "🎥";
            case ".mp3":
            case ".wav":
            case ".flac": return "🎵";
            case ".zip":
            case ".rar":
            case ".7z": return "🗜️";
            case ".txt": return "📄";
            case ".csv": return "📋";
            default: return "📄";
        }
    }

    /**
     * Creates a modern card with emoji header
     * @param emoji The header emoji
     * @param title The card title
     * @param content The card content
     * @return Styled card panel
     */
    public static JPanel createEmojiCard(String emoji, String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header with emoji and title
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);

        JLabel emojiLabel = createEmojiIcon(emoji, 20);
        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);

        header.add(emojiLabel);
        header.add(titleLabel);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    /**
     * Creates a text field with emoji icon
     */
    public static JPanel createEmojiTextField(String emoji, JTextField textField) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setOpaque(true);
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Create emoji label with background
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setVerticalAlignment(SwingConstants.CENTER);
        emojiLabel.setPreferredSize(new Dimension(35, textField.getPreferredSize().height));
        emojiLabel.setOpaque(true);
        emojiLabel.setBackground(new Color(248, 249, 250));
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        // Remove border from text field since panel has border
        textField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        fieldPanel.add(emojiLabel, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Creates a password field with emoji icon
     */
    public static JPanel createEmojiPasswordField(String emoji, JPasswordField passwordField) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setOpaque(true);
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Create emoji label with background
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setVerticalAlignment(SwingConstants.CENTER);
        emojiLabel.setPreferredSize(new Dimension(35, passwordField.getPreferredSize().height));
        emojiLabel.setOpaque(true);
        emojiLabel.setBackground(new Color(248, 249, 250));
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        // Remove border from password field since panel has border
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        fieldPanel.add(emojiLabel, BorderLayout.WEST);
        fieldPanel.add(passwordField, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Creates a text area with emoji icon
     */
    public static JPanel createEmojiTextArea(String emoji, JTextArea textArea) {
        JPanel fieldPanel = new JPanel(new BorderLayout(8, 0));
        fieldPanel.setOpaque(false);

        // Create emoji label
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(EMOJI_FONT_NORMAL);
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setVerticalAlignment(SwingConstants.TOP);
        emojiLabel.setPreferredSize(new Dimension(30, 30));
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        fieldPanel.add(emojiLabel, BorderLayout.WEST);
        fieldPanel.add(textArea, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Creates a combo box with emoji icon
     */
    public static JPanel createEmojiComboBox(String emoji, JComboBox<?> comboBox) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setOpaque(true);
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Create emoji label with background
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emojiLabel.setVerticalAlignment(SwingConstants.CENTER);
        emojiLabel.setPreferredSize(new Dimension(35, comboBox.getPreferredSize().height));
        emojiLabel.setOpaque(true);
        emojiLabel.setBackground(new Color(248, 249, 250));
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        fieldPanel.add(emojiLabel, BorderLayout.WEST);
        fieldPanel.add(comboBox, BorderLayout.CENTER);

        return fieldPanel;
    }
}

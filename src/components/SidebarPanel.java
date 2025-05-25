package components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import utils.AppColors;

/**
 * Reusable sidebar panel component with consistent styling and improved visuals
 */
public class SidebarPanel extends JPanel {
    private final List<JButton> navButtons = new ArrayList<>();
    private final Map<String, JButton> buttonMap = new HashMap<>();
    private final CardLayout contentCardLayout;
    private final JPanel contentPanel;
    private final String username;
    private final String userRole;
    private JButton selectedButton;

    // Colors for the sidebar
    private static final Color SIDEBAR_BG = new Color(33, 37, 43);
    private static final Color SELECTED_BG = new Color(44, 49, 58);
    private static final Color HOVER_BG = new Color(39, 44, 52);
    private static final Color AVATAR_BG = AppColors.PRIMARY;

    // Icon size constants
    private static final int ICON_WIDTH = 20;
    private static final int ICON_HEIGHT = 20;

    /**
     * Creates a sidebar panel with navigation buttons
     *
     * @param contentCardLayout The card layout for the content panel
     * @param contentPanel The content panel that will be controlled by this sidebar
     * @param username The username to display
     * @param userRole The user role to display
     */
    public SidebarPanel(CardLayout contentCardLayout, JPanel contentPanel, String username, String userRole) {
        this.contentCardLayout = contentCardLayout;
        this.contentPanel = contentPanel;
        this.username = username;
        this.userRole = userRole;

        // Set up the panel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(SIDEBAR_BG);
        setPreferredSize(new Dimension(250, getHeight()));
        setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Add user profile section
        addProfileSection();

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(70, 70, 70));
        separator.setBackground(SIDEBAR_BG);
        separator.setMaximumSize(new Dimension(220, 1));
        add(separator);
        add(Box.createVerticalStrut(20));

        // Add navigation section label
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navLabel.setForeground(new Color(150, 150, 150));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        add(navLabel);
    }

    /**
     * Loads and resizes an icon from the resources directory
     *
     * @param iconName The name of the icon file (without path)
     * @return The resized ImageIcon, or null if the icon couldn't be loaded
     */
    private ImageIcon loadIcon(String iconName) {
        try {
            String iconPath = "/icons/" + iconName;
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl == null) {
                System.err.println("Error loading icon: " + iconName + " (not found at " + iconPath + ")");
                return null;
            }
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconName + " (" + e.getMessage() + ")");
            return null;
        }
    }

    /**
     * Adds the user profile section to the sidebar
     */
    private void addProfileSection() {
        JPanel profilePanel = new JPanel(new BorderLayout(15, 0));
        profilePanel.setOpaque(false);
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(220, 60));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 20, 5));

        // Create avatar panel with emoji user icon
        JPanel avatarPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(45, 45);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        avatarPanel.setBackground(AVATAR_BG);

        // Add emoji avatar
        JLabel avatarEmoji = new JLabel("👤");
        avatarEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatarEmoji.setHorizontalAlignment(SwingConstants.CENTER);
        avatarEmoji.setVerticalAlignment(SwingConstants.CENTER);
        avatarEmoji.setForeground(Color.WHITE);
        avatarPanel.add(avatarEmoji, BorderLayout.CENTER);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(userRole);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(200, 200, 200));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfo.add(nameLabel);
        userInfo.add(Box.createVerticalStrut(3));
        userInfo.add(roleLabel);

        profilePanel.add(avatarPanel, BorderLayout.WEST);
        profilePanel.add(userInfo, BorderLayout.CENTER);

        add(profilePanel);
    }

    /**
     * Adds a navigation button to the sidebar
     *
     * @param text The button text
     * @param iconName The name of the icon file (without path)
     * @param cardName The name of the card to show when this button is clicked
     * @param isDefault Whether this button should be selected by default
     * @return The created button
     */
    public JButton addNavButton(String text, String iconName, String cardName, boolean isDefault) {
        // Create a custom button with hover effect
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background with rounded corners
                if (getModel().isPressed()) {
                    g2.setColor(SELECTED_BG);
                } else if (getModel().isRollover() || isSelected()) {
                    g2.setColor(HOVER_BG);
                } else {
                    g2.setColor(SIDEBAR_BG);
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                // Draw indicator for selected button
                if (isSelected()) {
                    g2.setColor(AppColors.PRIMARY);
                    g2.fillRoundRect(0, 0, 4, getHeight(), 2, 2);
                }

                super.paintComponent(g);
                g2.dispose();
            }

            public boolean isSelected() {
                return SidebarPanel.this.selectedButton == this;
            }
        };

        // Load and set the icon (only if iconName is provided)
        if (iconName != null && !iconName.trim().isEmpty()) {
            ImageIcon icon = loadIcon(iconName);
            if (icon != null) {
                button.setIcon(icon);
                button.setIconTextGap(10); // Add space between icon and text
            }
        }

        // Set button properties
        button.setText(text);
        // Use a font that supports emoji rendering
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(220, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Make the button fill the width
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add basic action listener
        button.addActionListener(e -> {
            if (contentCardLayout != null && contentPanel != null) {
                contentCardLayout.show(contentPanel, cardName);
                System.out.println("Showing card: " + cardName + " (from basic listener)");
            }
            updateButtonStyles(button);
        });

        // Add to the panel and lists
        add(button);
        add(Box.createVerticalStrut(5));
        navButtons.add(button);
        buttonMap.put(text, button);
        buttonMap.put(cardName, button);

        // Set as selected if default
        if (isDefault) {
            selectedButton = button;
        }

        return button;
    }

    /**
     * Adds a section label to the sidebar
     *
     * @param text The label text
     */
    public void addSectionLabel(String text) {
        // Add some space before the section
        add(Box.createVerticalStrut(15));

        // Add section label
        JLabel sectionLabel = new JLabel(text);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sectionLabel.setForeground(new Color(150, 150, 150));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

        add(sectionLabel);
    }

    /**
     * Adds a logout button to the sidebar
     *
     * @param logoutAction The action to perform when the logout button is clicked
     */
    public void addLogoutButton(ActionListener logoutAction) {
        // Add spacer to push logout to bottom
        add(Box.createVerticalGlue());

        // Add section label
        addSectionLabel("ACCOUNT");

        // Create logout button with custom styling
        JButton logoutButton = new JButton("🚪 Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background with rounded corners
                if (getModel().isPressed()) {
                    g2.setColor(new Color(180, 60, 60));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(150, 50, 50));
                } else {
                    g2.setColor(new Color(120, 40, 40));
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                super.paintComponent(g);
                g2.dispose();
            }
        };

        // Set font to support emoji rendering
        logoutButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setMaximumSize(new Dimension(220, 40));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.addActionListener(logoutAction);

        add(logoutButton);
        add(Box.createVerticalStrut(10));
    }

    /**
     * Updates the styles of all buttons when a button is selected
     *
     * @param selectedButton The button that was selected
     */
    private void updateButtonStyles(JButton selectedButton) {
        // Update the selected button
        this.selectedButton = selectedButton;

        // Repaint all buttons to update their appearance
        for (JButton button : navButtons) {
            button.repaint();
        }
    }

    /**
     * Gets the currently selected button
     *
     * @return The selected button
     */
    public JButton getSelectedButton() {
        return selectedButton;
    }

    /**
     * Selects a button by its text or card name
     *
     * @param buttonTextOrCardName The text of the button or the name of the card
     */
    public void selectButton(String buttonTextOrCardName) {
        JButton button = buttonMap.get(buttonTextOrCardName);
        if (button != null) {
            updateButtonStyles(button);
            if (contentCardLayout != null && contentPanel != null) {
                contentCardLayout.show(contentPanel, buttonTextOrCardName);
                System.out.println("Selecting button and showing card: " + buttonTextOrCardName);
            }
        } else {
            System.err.println("Button not found for selection: " + buttonTextOrCardName);
            System.err.println("Available buttons: " + String.join(", ", buttonMap.keySet()));
        }
    }

    /**
     * Adds an action listener to a navigation button
     *
     * @param buttonText The text of the button to add the listener to
     * @param listener The action listener to add
     */
    public void addNavButtonActionListener(String buttonText, ActionListener listener) {
        // Try to find the button by text first
        JButton button = buttonMap.get(buttonText);

        if (button != null) {
            // Remove all existing listeners
            ActionListener[] actionListeners = button.getActionListeners();
            for (ActionListener al : actionListeners) {
                button.removeActionListener(al);
            }

            // Add a new combined action listener
            button.addActionListener(e -> {
                // First show the card - use the buttonText as the card name
                if (contentCardLayout != null && contentPanel != null) {
                    contentCardLayout.show(contentPanel, buttonText);
                    System.out.println("Showing card from action listener: " + buttonText);
                }

                // Update button styles
                updateButtonStyles(button);

                // Then execute the custom listener
                listener.actionPerformed(e);
            });

            System.out.println("Added action listener to button: " + buttonText);
        } else {
            System.err.println("Button not found for: " + buttonText);
            // Print all keys in the buttonMap for debugging
            System.err.println("Available buttons: " + String.join(", ", buttonMap.keySet()));
        }
    }
}

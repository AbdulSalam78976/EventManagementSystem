package utils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import components.RoundedPanel;
import java.util.Objects;
import java.net.URL;

/**
 * Utility class for UI components with consistent styling
 */
public class UIUtils {
    // Default corner radius for rounded components
    public static final int CORNER_RADIUS = 10;
    
    // Default padding for components
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 15;
    
    // Prevent instantiation
    private UIUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Creates a panel with rounded corners
     * 
     * @param backgroundColor The background color of the panel
     * @param radius The corner radius
     * @return A JPanel with rounded corners
     */
    public static JPanel createRoundedPanel(Color backgroundColor, int radius) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background
                g2.setColor(backgroundColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                
                g2.dispose();
            }
        };
    }
    
    /**
     * Creates a panel with rounded corners and a layout manager
     * 
     * @param backgroundColor The background color of the panel
     * @param radius The corner radius
     * @param layout The layout manager to use
     * @return A JPanel with rounded corners and the specified layout
     */
    public static JPanel createRoundedPanel(Color backgroundColor, int radius, LayoutManager layout) {
        JPanel panel = createRoundedPanel(backgroundColor, radius);
        panel.setLayout(layout);
        return panel;
    }
    
    /**
     * Creates a rounded border
     * 
     * @param color The border color
     * @param radius The corner radius
     * @param thickness The border thickness
     * @return A Border with rounded corners
     */
    public static Border createRoundedBorder(Color color, int radius, int thickness) {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(color);
                g2.setStroke(new BasicStroke(thickness));
                g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
                
                g2.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(thickness, thickness, thickness, thickness);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
    
    /**
     * Creates a rounded border with padding
     * 
     * @param color The border color
     * @param radius The corner radius
     * @param thickness The border thickness
     * @param padding The padding inside the border
     * @return A Border with rounded corners and padding
     */
    public static Border createRoundedBorderWithPadding(Color color, int radius, int thickness, int padding) {
        return BorderFactory.createCompoundBorder(
            createRoundedBorder(color, radius, thickness),
            BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }
    
    /**
     * Creates a sidebar button with consistent styling
     * 
     * @param text The button text
     * @param icon The button icon (can be null)
     * @param isSelected Whether the button is selected
     * @return A styled JButton for the sidebar
     */
    public static JButton createSidebarButton(String text, Icon icon, boolean isSelected) {
        JButton button = new JButton(text);
        if (icon != null) {
            button.setIcon(icon);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setIconTextGap(10);
        }
        
        // Set button properties
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(isSelected ? new Color(70, 70, 70) : AppColors.BACKGROUND_DARK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        
        // Add rounded corners
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        return button;
    }
    
    /**
     * Creates a sidebar navigation button with consistent styling and an ActionListener.
     * This method handles loading the icon from resources.
     *
     * @param text The button text
     * @param iconName The name of the icon file (without extension), can be null
     * @param listener The ActionListener for the button
     * @return A styled JButton for sidebar navigation
     */
    public static JButton createSidebarNavButton(String text, String iconName, ActionListener listener) {
        JButton button = new JButton(text);
        
        // Load and set icon if provided
        if (iconName != null) {
            ImageIcon icon = IconUtils.loadIcon(iconName, IconUtils.ICON_SIZE_NORMAL); // Assuming default icon size for sidebar
            if (icon != null) {
                button.setIcon(icon);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setIconTextGap(10);
            }
        }
        
        // Set button properties
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(AppColors.TEXT_LIGHT);
        button.setBackground(AppColors.BACKGROUND_DARK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40)); // Ensure consistent size

        // Add padding
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect (optional, based on design)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(AppColors.PRIMARY_DARK); // Darker shade on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(AppColors.BACKGROUND_DARK); // Revert on exit
            }
        });
        
        if (listener != null) {
            button.addActionListener(listener);
        }
        
        return button;
    }
    
    /**
     * Creates a text field with rounded corners
     * 
     * @return A JTextField with rounded corners
     */
    public static JTextField createRoundedTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(createRoundedBorderWithPadding(AppColors.BORDER_LIGHT, CORNER_RADIUS, 1, PADDING_MEDIUM));
        return textField;
    }
    
    /**
     * Creates a password field with rounded corners
     * 
     * @return A JPasswordField with rounded corners
     */
    public static JPasswordField createRoundedPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(createRoundedBorderWithPadding(AppColors.BORDER_LIGHT, CORNER_RADIUS, 1, PADDING_MEDIUM));
        return passwordField;
    }
    
    /**
     * Creates a text area with rounded corners
     * 
     * @return A JTextArea with rounded corners
     */
    public static JTextArea createRoundedTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(createRoundedBorderWithPadding(AppColors.BORDER_LIGHT, CORNER_RADIUS, 1, PADDING_MEDIUM));
        return textArea;
    }
    
    /**
     * Creates a scroll pane with rounded corners
     * 
     * @param component The component to scroll
     * @return A JScrollPane with rounded corners
     */
    public static JScrollPane createRoundedScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(createRoundedBorder(AppColors.BORDER_LIGHT, CORNER_RADIUS, 1));
        return scrollPane;
    }
    
    /**
     * Creates a combo box with rounded corners
     * 
     * @param items The items to display in the combo box
     * @return A JComboBox with rounded corners
     */
    public static <T> JComboBox<T> createRoundedComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(createRoundedBorder(AppColors.BORDER_LIGHT, CORNER_RADIUS, 1));
        return comboBox;
    }

    /**
     * Creates a standardized button with consistent styling.
     *
     * @param text The button text.
     * @param iconName The name of the icon file (without extension), can be null.
     * @param type The button type (PRIMARY, SECONDARY, ERROR).
     * @param size The button size (NORMAL, SMALL, LARGE, LARGE_RECTANGULAR).
     * @return A styled JButton.
     */
    public static JButton createButton(String text, String iconName, ButtonType type, ButtonSize size) {
        JButton button = new JButton(text);
        
        // Load and set icon if provided
        if (iconName != null) {
            ImageIcon icon = IconUtils.loadIcon(iconName, IconUtils.ICON_SIZE_NORMAL);
            if (icon != null) {
                button.setIcon(icon);
                button.setIconTextGap(10);
            }
        }
        
        // Set button properties based on type
        switch (type) {
            case PRIMARY:
                button.setBackground(AppColors.PRIMARY);
                button.setForeground(Color.WHITE);
                break;
            case SECONDARY:
                button.setBackground(AppColors.SECONDARY);
                button.setForeground(Color.WHITE);
                break;
            case ERROR:
                button.setBackground(AppColors.ERROR);
                button.setForeground(Color.WHITE);
                break;
        }
        
        // Set button size
        switch (size) {
            case SMALL:
                button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                button.setPreferredSize(new Dimension(100, 30));
                break;
            case NORMAL:
                button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                button.setPreferredSize(new Dimension(120, 35));
                break;
            case LARGE:
                button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                button.setPreferredSize(new Dimension(140, 40));
                break;
            case LARGE_RECTANGULAR:
                button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                button.setPreferredSize(new Dimension(200, 45));
                break;
        }
        
        // Common button properties
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                switch (type) {
                    case PRIMARY:
                        button.setBackground(AppColors.PRIMARY_DARK);
                        break;
                    case SECONDARY:
                        button.setBackground(AppColors.SECONDARY_DARK);
                        break;
                    case ERROR:
                        button.setBackground(AppColors.ERROR);
                        break;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                switch (type) {
                    case PRIMARY:
                        button.setBackground(AppColors.PRIMARY);
                        break;
                    case SECONDARY:
                        button.setBackground(AppColors.SECONDARY);
                        break;
                    case ERROR:
                        button.setBackground(AppColors.ERROR);
                        break;
                }
            }
        });

        return button;
    }

    /**
     * Enumeration for button types
     */
    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        ERROR
    }

    /**
     * Enumeration for button sizes
     */
    public enum ButtonSize {
        SMALL,
        NORMAL,
        LARGE,
        LARGE_RECTANGULAR
    }
    
    /**
     * Creates a generic JPanel with specified layout and opacity.
     *
     * @param layout The LayoutManager for the panel.
     * @param isOpaque Whether the panel should be opaque.
     * @return A configured JPanel.
     */
    public static JPanel createPanel(LayoutManager layout, boolean isOpaque) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(isOpaque);
        return panel;
    }

    /**
     * Creates a JLabel with specified text, font, and color.
     *
     * @param text The text for the label.
     * @param font The Font for the label.
     * @param color The Color for the label.
     * @return A configured JLabel.
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Shows an error message dialog.
     *
     * @param parent The parent component for the dialog.
     * @param message The error message to display.
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message dialog.
     *
     * @param parent The parent component for the dialog.
     * @param message The success message to display.
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param parent The parent component for the dialog.
     * @param message The confirmation message to display.
     * @return true if the user confirms, false otherwise.
     */
    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Creates a simple loading panel with a spinner.
     *
     * @return A JPanel representing a loading indicator.
     */
    public static JPanel createLoadingPanel() {
        // This is a basic placeholder. A real loading panel might have animation.
        JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.add(new JLabel("Loading...")); // Consider adding a loading icon/spinner
        loadingPanel.setOpaque(false); // Make it transparent
        return loadingPanel;
    }

    /**
     * Adds a standard border to a JComponent.
     *
     * @param component The component to add the border to.
     */
    public static void addStandardBorder(JComponent component) {
        component.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
    }

    /**
     * Creates a standard stat card panel.
     *
     * @param title The title of the stat card.
     * @param value The value to display.
     * @param iconName The name of the icon (can be null).
     * @return A JPanel representing a stat card.
     */
    public static JPanel createStatCard(String title, String value, String iconName) {
         RoundedPanel card = new RoundedPanel(new BorderLayout(0, 10), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        card.setBorder(createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));

        JLabel titleLabel = createLabel(
             title,
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

         JLabel valueLabel = createLabel(
             value,
            UIConstants.HEADER_FONT,
            AppColors.PRIMARY
        );
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

         JPanel contentPanel = createPanel(new BorderLayout(), false);
         contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Inner padding
         contentPanel.add(titleLabel, BorderLayout.NORTH);
         contentPanel.add(valueLabel, BorderLayout.CENTER);

         // Add icon if provided
        if (iconName != null) {
            ImageIcon icon = IconUtils.loadIcon(iconName, IconUtils.ICON_SIZE_NORMAL); // Load icon
            if (icon != null) {
                 JLabel iconLabel = new JLabel(icon);
                 iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                 // Add icon above the title or adjust layout as needed
                 // For simplicity, adding it to the north of a wrapper panel
                 JPanel iconWrapper = createPanel(new FlowLayout(FlowLayout.CENTER), false);
                 iconWrapper.add(iconLabel);
                 contentPanel.add(iconWrapper, BorderLayout.WEST); // Or another suitable position
            }
        }

        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }

    /**
     * Creates a header label.
     */
    public static JLabel createHeaderLabel(String text) {
        return createLabel(text, UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
    }

    /**
     * Creates a title label.
     */
    public static JLabel createTitleLabel(String text) {
        return createLabel(text, UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
    }

    /**
     * Creates a subtitle label.
     */
    public static JLabel createSubtitleLabel(String text) {
        return createLabel(text, UIConstants.SUBTITLE_FONT, AppColors.TEXT_SECONDARY);
    }

    /**
     * Creates a body label.
     */
    public static JLabel createBodyLabel(String text) {
        return createLabel(text, UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
    }

    /**
     * Creates a small label.
     */
    public static JLabel createSmallLabel(String text) {
        return createLabel(text, UIConstants.SMALL_FONT, AppColors.TEXT_SECONDARY);
    }

    /**
     * Creates a card panel with standard styling.
     */
    public static JPanel createCardPanel() {
        JPanel panel = createPanel(new BorderLayout(), true);
        panel.setBackground(AppColors.CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER), // Outer border
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Inner padding
        ));
        return panel;
    }

    // Helper method to create ImageIcons from resources
    public static ImageIcon createImageIcon(String path, int size) {
        URL imgURL = UIUtils.class.getResource("/resources/images/" + path + ".png");
        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}

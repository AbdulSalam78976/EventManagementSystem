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

/**
 * Utility class for UI components with consistent styling
 */
public class UIUtils {
    // Default corner radius for rounded components - use UIConstants instead
    public static final int CORNER_RADIUS = UIConstants.CORNER_RADIUS_MEDIUM;

    // Default padding for components - use UIConstants instead
    public static final int PADDING_SMALL = UIConstants.PADDING_SMALL;
    public static final int PADDING_MEDIUM = UIConstants.PADDING_MEDIUM;
    public static final int PADDING_LARGE = UIConstants.PADDING_LARGE;

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
     * Creates a rounded border with enhanced visibility
     *
     * @param color The border color
     * @param radius The corner radius
     * @param thickness The border thickness
     * @return A Border with rounded corners and improved visibility
     */
    public static Border createRoundedBorder(Color color, int radius, int thickness) {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                // Use a slightly thicker stroke for better visibility
                g2.setColor(color);
                g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));

                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(thickness + 1, thickness + 1, thickness + 1, thickness + 1);
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

        // Use emoji instead of icon files
        if (iconName != null) {
            // If iconName is actually an emoji, use it directly
            String displayText = iconName + " " + text;
            button.setText(displayText);
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            button.setHorizontalAlignment(SwingConstants.LEFT);
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
     * Creates a text field with more rounded corners, shadow effect, and enhanced visibility
     *
     * @return A JTextField with stylish rounded corners and subtle shadow
     */
    public static JTextField createRoundedTextField() {
        // Create a custom text field with shadow effect
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                super.paintComponent(g);
            }
        };

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setOpaque(false); // Make transparent to show our custom background

        // Create more rounded borders with larger radius
        Border normalBorder = createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_LARGE, 1, PADDING_MEDIUM);
        Border focusBorder = createRoundedBorderWithPadding(AppColors.BORDER_FOCUS, UIConstants.CORNER_RADIUS_LARGE, 2, PADDING_MEDIUM);

        // Set initial border
        textField.setBorder(normalBorder);

        // Add focus listener to change border color when focused
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(focusBorder);
                textField.setBackground(new Color(245, 250, 255)); // Very light blue background on focus
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(normalBorder);
                textField.setBackground(Color.WHITE);
            }
        });

        return textField;
    }

    /**
     * Creates a password field with more rounded corners, shadow effect, and enhanced visibility
     *
     * @return A JPasswordField with stylish rounded corners and subtle shadow
     */
    public static JPasswordField createRoundedPasswordField() {
        // Create a custom password field with shadow effect
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                super.paintComponent(g);
            }
        };

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setOpaque(false); // Make transparent to show our custom background

        // Create more rounded borders with larger radius
        Border normalBorder = createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_LARGE, 1, PADDING_MEDIUM);
        Border focusBorder = createRoundedBorderWithPadding(AppColors.BORDER_FOCUS, UIConstants.CORNER_RADIUS_LARGE, 2, PADDING_MEDIUM);

        // Set initial border
        passwordField.setBorder(normalBorder);

        // Add focus listener to change border color when focused
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(focusBorder);
                passwordField.setBackground(new Color(245, 250, 255)); // Very light blue background on focus
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(normalBorder);
                passwordField.setBackground(Color.WHITE);
            }
        });

        return passwordField;
    }

    /**
     * Creates a text area with more rounded corners, shadow effect, and enhanced visibility
     *
     * @return A JTextArea with stylish rounded corners and subtle shadow
     */
    public static JTextArea createRoundedTextArea() {
        // Create a custom text area with shadow effect
        JTextArea textArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                super.paintComponent(g);
            }
        };

        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false); // Make transparent to show our custom background

        // Create more rounded borders with larger radius
        Border normalBorder = createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_LARGE, 1, PADDING_MEDIUM);
        Border focusBorder = createRoundedBorderWithPadding(AppColors.BORDER_FOCUS, UIConstants.CORNER_RADIUS_LARGE, 2, PADDING_MEDIUM);

        // Set initial border
        textArea.setBorder(normalBorder);

        // Add focus listener to change border color when focused
        textArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                textArea.setBorder(focusBorder);
                textArea.setBackground(new Color(245, 250, 255)); // Very light blue background on focus
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                textArea.setBorder(normalBorder);
                textArea.setBackground(Color.WHITE);
            }
        });

        return textArea;
    }

    /**
     * Creates a scroll pane with more rounded corners and subtle shadow
     *
     * @param component The component to scroll
     * @return A JScrollPane with stylish rounded corners and subtle shadow
     */
    public static JScrollPane createRoundedScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                super.paintComponent(g);
            }
        };

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_LARGE, 1));

        return scrollPane;
    }

    /**
     * Creates a combo box with more rounded corners, shadow effect, and enhanced visibility
     *
     * @param items The items to display in the combo box
     * @return A JComboBox with stylish rounded corners and subtle shadow
     */
    public static <T> JComboBox<T> createRoundedComboBox(T[] items) {
        // Create a custom combo box with shadow effect
        JComboBox<T> comboBox = new JComboBox<T>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConstants.CORNER_RADIUS_LARGE, UIConstants.CORNER_RADIUS_LARGE);

                super.paintComponent(g);
            }
        };

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setOpaque(false); // Make transparent to show our custom background

        // Create more rounded borders with larger radius
        Border normalBorder = createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_LARGE, 1);
        Border focusBorder = createRoundedBorder(AppColors.BORDER_FOCUS, UIConstants.CORNER_RADIUS_LARGE, 2);

        // Set initial border
        comboBox.setBorder(normalBorder);

        // Add focus listener to change border color when focused
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(focusBorder);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(normalBorder);
            }
        });

        // Customize the combo box UI
        comboBox.setBackground(Color.WHITE);
        if (comboBox.isEditable()) {
            ((JComponent) comboBox.getEditor().getEditorComponent()).setBorder(
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
            );
        }

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

        // Use emoji instead of icon files
        if (iconName != null) {
            // If iconName is actually an emoji, use it directly
            String displayText = iconName + " " + text;
            button.setText(displayText);
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
         RoundedPanel card = new RoundedPanel(new BorderLayout(0, 10), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM, true);
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

         // Add emoji icon if provided
        if (iconName != null) {
            JLabel iconLabel = new JLabel(iconName);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            // Add icon above the title or adjust layout as needed
            // For simplicity, adding it to the north of a wrapper panel
            JPanel iconWrapper = createPanel(new FlowLayout(FlowLayout.CENTER), false);
            iconWrapper.add(iconLabel);
            contentPanel.add(iconWrapper, BorderLayout.WEST); // Or another suitable position
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
     * Creates a card panel with standard styling and shadow effect.
     */
    public static JPanel createCardPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), AppColors.CARD_BACKGROUND, UIConstants.CORNER_RADIUS_MEDIUM, true);
        panel.setBorder(createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            UIConstants.PADDING_MEDIUM
        ));
        return panel;
    }

    // Helper method to create emoji labels for consistent styling
    public static JLabel createEmojiLabel(String emoji, String text, int fontSize) {
        return EmojiUtils.createEmojiLabel(emoji, text, fontSize);
    }

    // Helper method to create emoji buttons for consistent styling
    public static JButton createEmojiButton(String emoji, String text, int fontSize) {
        return EmojiUtils.createEmojiButton(emoji, text, fontSize);
    }
}

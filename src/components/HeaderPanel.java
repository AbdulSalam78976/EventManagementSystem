package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;
import screens.LoginScreen;
import controllers.*;
import screens.*;
import utils.IconUtils;

/**
 * Reusable header panel component with consistent styling for all screens
 */
public class HeaderPanel extends JPanel {
    private final String username;
    private final String userRole;
    private final boolean showCreateEventButton;
    private final Consumer<Void> onCreateEventClick;

    /**
     * Creates a header panel with user information and optional create event button
     *
     * @param username The username to display
     * @param userRole The user role to display
     * @param showCreateEventButton Whether to show the create event button
     * @param onCreateEventClick Callback for when the create event button is clicked
     */
    public HeaderPanel(String username, String userRole, boolean showCreateEventButton, Consumer<Void> onCreateEventClick) {
        this.username = username;
        this.userRole = userRole;
        this.showCreateEventButton = showCreateEventButton;
        this.onCreateEventClick = onCreateEventClick;

        setupUI();
    }

    /**
     * Creates a header panel without create event button
     *
     * @param username The username to display
     * @param userRole The user role to display
     */
    public HeaderPanel(String username, String userRole) {
        this(username, userRole, false, null);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.PRIMARY_DARK);
        setPreferredSize(new Dimension(getWidth(), 60));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // System name on the left
        JLabel systemName = UIUtils.createLabel(
            "EVENT MANAGEMENT SYSTEM",
            UIConstants.HEADER_FONT,
            Color.WHITE
        );
        add(systemName, BorderLayout.WEST);

        // User info and actions on the right
        JPanel userPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0), false);

        // User dropdown
        JPanel userDropdown = createUserDropdown();
        userPanel.add(userDropdown);

        // Create Event Button (if enabled)
        if (showCreateEventButton) {
            JButton createEventButton = UIUtils.createButton(
                "Create Event",
                null,
                UIUtils.ButtonType.PRIMARY,
                UIUtils.ButtonSize.SMALL
            );
            createEventButton.addActionListener(e -> {
                if (onCreateEventClick != null) {
                    onCreateEventClick.accept(null);
                }
            });
            userPanel.add(createEventButton);
        }

        // Notifications icon
        JPanel notificationPanel = createNotificationPanel();
        userPanel.add(notificationPanel);
        add(userPanel, BorderLayout.EAST);
    }

    private JPanel createNotificationPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Use an ImageIcon from IconUtils
        ImageIcon notificationIcon = IconUtils.loadIcon("notification", IconUtils.ICON_SIZE_NORMAL);
        JLabel notificationLabel = new JLabel(notificationIcon);
        notificationLabel.setToolTipText("Notifications");
        panel.add(notificationLabel, BorderLayout.CENTER);

        // Add a red dot for unread notifications
        JLabel badgeLabel = UIUtils.createLabel(
            "•",
            UIConstants.TITLE_FONT,
            AppColors.ERROR
        );
        badgeLabel.setVisible(false); // Hide by default
        panel.add(badgeLabel, BorderLayout.NORTH);

        // Add click handler
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNotifications();
            }
        });

        return panel;
    }

    private void showNotifications() {
        JPopupMenu notificationMenu = new JPopupMenu();
        
        // Add notification items
        JMenuItem noNotifications = new JMenuItem("No new notifications");
        noNotifications.setEnabled(false);
        notificationMenu.add(noNotifications);

        // Show the menu
        Component source = (Component) notificationMenu.getInvoker();
        notificationMenu.show(source, source.getWidth() - notificationMenu.getPreferredSize().width, source.getHeight());
    }

    private JPanel createUserDropdown() {
        JPanel userDropdown = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0), false);
        userDropdown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // User avatar (circle with first letter of username)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circle
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Draw first letter
                g2.setColor(AppColors.PRIMARY_DARK);
                g2.setFont(UIConstants.BODY_FONT);

                String firstLetter = username.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(firstLetter)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(firstLetter, x, y);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }
        };

        JLabel userLabel = UIUtils.createLabel(
            username,
            UIConstants.BODY_FONT,
            Color.WHITE
        );

        JLabel roleLabel = UIUtils.createLabel(
            "(" + userRole + ")",
            UIConstants.SMALL_FONT,
            new Color(220, 220, 220)
        );

        JLabel dropdownIcon = UIUtils.createLabel(
            "▼",
            UIConstants.SMALL_FONT,
            Color.WHITE
        );

        // Create user info panel with proper BoxLayout
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        userInfo.add(userLabel);
        userInfo.add(roleLabel);

        userDropdown.add(avatarPanel);
        userDropdown.add(Box.createHorizontalStrut(5));
        userDropdown.add(userInfo);
        userDropdown.add(dropdownIcon);

        // Add popup menu to user dropdown
        JPopupMenu userMenu = createUserMenu();
        userDropdown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.show(userDropdown, 0, userDropdown.getHeight());
            }
        });

        return userDropdown;
    }

    private JPopupMenu createUserMenu() {
        JPopupMenu userMenu = new JPopupMenu();

        // Profile menu item
        JMenuItem profileItem = new JMenuItem("Profile");
        profileItem.addActionListener(e -> showProfile());

        // Settings menu item
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettings());

        // Logout menu item
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());

        userMenu.add(profileItem);
        userMenu.add(settingsItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        return userMenu;
    }

    private void showProfile() {
        JOptionPane.showMessageDialog(
            this,
            "Profile view will be implemented here",
            "Profile",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(
            this,
            "Settings view will be implemented here",
            "Settings",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleLogout() {
        try {
            AuthController.getInstance().logout();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            new LoginScreen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during logout: " + e.getMessage(),
                "Logout Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

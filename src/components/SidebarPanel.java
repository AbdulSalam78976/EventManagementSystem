package components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;
import screens.LoginScreen;
import java.util.function.Consumer;
import controllers.*;
import screens.*;

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
    private String currentView;
    private Consumer<String> onNavigation;

    /**
     * Creates a sidebar panel with navigation buttons
     *
     * @param contentCardLayout The card layout for the content panel
     * @param contentPanel The content panel that will be controlled by this sidebar
     * @param username The username to display
     * @param userRole The user role to display
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public SidebarPanel(CardLayout contentCardLayout, JPanel contentPanel, String username, String userRole) {
        if (contentCardLayout == null) {
            throw new IllegalArgumentException("Content card layout cannot be null");
        }
        if (contentPanel == null) {
            throw new IllegalArgumentException("Content panel cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (userRole == null || userRole.trim().isEmpty()) {
            throw new IllegalArgumentException("User role cannot be null or empty");
        }

        this.contentCardLayout = contentCardLayout;
        this.contentPanel = contentPanel;
        this.username = username;
        this.userRole = userRole;

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(AppColors.PRIMARY_DARK);
        setPreferredSize(new Dimension(250, getHeight()));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Logo and system name
        JPanel logoPanel = createLogoPanel();
        add(logoPanel, BorderLayout.NORTH);

        // Navigation menu
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);

        // Logout button at bottom
        JPanel logoutPanel = createLogoutPanel();
        add(logoutPanel, BorderLayout.SOUTH);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.CENTER), false);
        logoPanel.setBackground(AppColors.PRIMARY_DARK);

        JLabel logoLabel = UIUtils.createLabel(
            "🎉",
            UIConstants.TITLE_FONT,
            Color.WHITE
        );

        JLabel systemName = UIUtils.createLabel(
            "Event Management",
            UIConstants.HEADER_FONT,
            Color.WHITE
        );

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createHorizontalStrut(10));
        logoPanel.add(systemName);

        return logoPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), false);
        menuPanel.setBackground(AppColors.PRIMARY_DARK);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Add menu items
        addMenuItem(menuPanel, "Home", "🏠", "home");
        addMenuItem(menuPanel, "Search", "🔍", "search");
        addMenuItem(menuPanel, "Calendar", "📅", "calendar");
        addMenuItem(menuPanel, "My Events", "📋", "my-events");
        addMenuItem(menuPanel, "Profile", "👤", "profile");

        return menuPanel;
    }

    private void addMenuItem(JPanel parent, String text, String icon, String action) {
        JPanel menuItem = UIUtils.createPanel(new FlowLayout(FlowLayout.LEFT, 20, 10), false);
        menuItem.setBackground(AppColors.PRIMARY_DARK);
        menuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = UIUtils.createLabel(
            icon,
            UIConstants.TITLE_FONT,
            Color.WHITE
        );

        JLabel textLabel = UIUtils.createLabel(
            text,
            UIConstants.BODY_FONT,
            Color.WHITE
        );

        menuItem.add(iconLabel);
        menuItem.add(textLabel);

        // Add click handler
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleNavigation(action);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive(action)) {
                    menuItem.setBackground(AppColors.PRIMARY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive(action)) {
                    menuItem.setBackground(AppColors.PRIMARY_DARK);
                }
            }
        });

        parent.add(menuItem);
        parent.add(Box.createVerticalStrut(5));
    }

    private JPanel createLogoutPanel() {
        JPanel logoutPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.CENTER), false);
        logoutPanel.setBackground(AppColors.PRIMARY_DARK);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton logoutButton = UIUtils.createButton(
            "Logout",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.SMALL
        );

        logoutButton.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutButton);

        return logoutPanel;
    }

    private void handleNavigation(String action) {
        if (onNavigation != null) {
            onNavigation.accept(action);
        }
    }

    private void handleLogout() {
        try {
            controllers.AuthController.getInstance().logout();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            new screens.LoginScreen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during logout: " + e.getMessage(),
                "Logout Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isActive(String action) {
        return currentView != null && currentView.equals(action);
    }

    public void setCurrentView(String view) {
        this.currentView = view;
        repaint();
    }

    public void setOnNavigation(Consumer<String> onNavigation) {
        this.onNavigation = onNavigation;
    }
}

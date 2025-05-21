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
    private JPanel rightSection;

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
        rightSection = createRightSection();
        add(rightSection, BorderLayout.EAST);
    }

    private JPanel createRightSection() {
        JPanel userPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0), false);

        // User info panel
        JPanel userInfo = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0), false);
        
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

        // User name and role labels
        JPanel labelPanel = UIUtils.createPanel(new GridLayout(2, 1), false);
        JLabel userLabel = UIUtils.createLabel(username, UIConstants.BODY_FONT, Color.WHITE);
        JLabel roleLabel = UIUtils.createLabel("(" + userRole + ")", UIConstants.SMALL_FONT, new Color(220, 220, 220));
        labelPanel.add(userLabel);
        labelPanel.add(roleLabel);

        userInfo.add(avatarPanel);
        userInfo.add(Box.createHorizontalStrut(10));
        userInfo.add(labelPanel);

        userPanel.add(userInfo);
        return userPanel;
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

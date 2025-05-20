package screens;

import javax.swing.*;
import java.awt.*;
import components.SidebarPanel;

public class SampleScreen extends JFrame {
    public SampleScreen(String username, String userRole) {
        setTitle("Sample Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Add content panel
        CardLayout contentCardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(new Color(245, 246, 250));
        add(contentPanel, BorderLayout.CENTER);

        // Add sidebar
        SidebarPanel sidebar = new SidebarPanel(contentCardLayout, contentPanel, username, userRole);
        add(sidebar, BorderLayout.WEST);

        // Add navigation buttons
        sidebar.addNavButton("Dashboard", "", "Dashboard", true);
        sidebar.addNavButton("Pending Approvals", "", "Pending Approvals", false);
        sidebar.addNavButton("All Events", "", "All Events", false);
        sidebar.addNavButton("Registered Users", "", "Registered Users", false);
        sidebar.addNavButton("Event Reports", "", "Event Reports", false);
        sidebar.addNavButton("Activity Log", "", "Activity Log", false);
        sidebar.addSectionLabel("SYSTEM");
        sidebar.addNavButton("System Settings", "", "System Settings", false);
        sidebar.addLogoutButton(e -> System.exit(0));

        // Create styled content panels for each view
        contentPanel.add(createContentPanel("Dashboard"), "Dashboard");
        contentPanel.add(createContentPanel("Pending Approvals"), "Pending Approvals");
        contentPanel.add(createContentPanel("All Events"), "All Events");
        contentPanel.add(createContentPanel("Registered Users"), "Registered Users");
        contentPanel.add(createContentPanel("Event Reports"), "Event Reports");
        contentPanel.add(createContentPanel("Activity Log"), "Activity Log");
        contentPanel.add(createContentPanel("System Settings"), "System Settings");

        setVisible(true);
    }

    private JPanel createContentPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 246, 250));
        JLabel label = new JLabel(title + " View");
        label.setFont(new Font("Segoe UI", Font.BOLD, 32));
        label.setForeground(new Color(40, 43, 48));
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SampleScreen("Admin", "Administrator");
        });
    }
} 
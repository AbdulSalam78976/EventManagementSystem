package screens;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import controllers.AuthController;
import models.User;
import utils.*;
import components.*;
import dao.UserDAO;
import dao.SQLUserDAO;

public class AdminUserManagementScreen extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;
    private AuthController authController;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel userListPanel;
    private JPanel editUserPanel;
    private User selectedUser;

    public AdminUserManagementScreen() throws SQLException {
        authController = AuthController.getInstance();
        setTitle("Event Management System - User Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with card layout
        mainPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) mainPanel.getLayout();

        // Create panels
        userListPanel = createUserListPanel();
        editUserPanel = createEditUserPanel();

        // Add panels to card layout
        mainPanel.add(userListPanel, "USER_LIST");
        mainPanel.add(editUserPanel, "EDIT_USER");

        // Show user list panel by default
        cardLayout.show(mainPanel, "USER_LIST");

        setContentPane(mainPanel);
        loadUsers();
    }

    private JPanel createUserListPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and subtitle
        JPanel headerPanel = UIUtils.createPanel(new BorderLayout(), false);
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createLabel("Users Management", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        JLabel subtitleLabel = UIUtils.createLabel("Manage all system users", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main panel with search, filters, and table
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(0, 15), false);
        mainContent.setOpaque(false);

        // Search and filters panel
        RoundedPanel filtersPanel = new RoundedPanel(new FlowLayout(FlowLayout.LEFT, 15, 10), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        filtersPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));
        
        // Search field
        searchField = UIUtils.createRoundedTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.putClientProperty("JTextField.placeholderText", "Search users...");
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterUsers();
            }
        });
        
        JLabel searchLabel = UIUtils.createLabel("Search:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        filtersPanel.add(searchLabel);
        filtersPanel.add(searchField);

        // Role filter
        roleFilter = UIUtils.createRoundedComboBox(new String[]{"All Roles", "Admin", "Organizer", "Attendee"});
        roleFilter.setPreferredSize(new Dimension(150, 30));
        roleFilter.addActionListener(e -> filterUsers());
        
        JLabel roleLabel = UIUtils.createLabel("Role:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        filtersPanel.add(roleLabel);
        filtersPanel.add(roleFilter);

        // Status filter
        statusFilter = UIUtils.createRoundedComboBox(new String[]{"All Status", "Active", "Inactive"});
        statusFilter.setPreferredSize(new Dimension(150, 30));
        statusFilter.addActionListener(e -> filterUsers());
        
        JLabel statusLabel = UIUtils.createLabel("Status:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        filtersPanel.add(statusLabel);
        filtersPanel.add(statusFilter);
        
        mainContent.add(filtersPanel, BorderLayout.NORTH);

        // Table Panel
        RoundedPanel tablePanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        tablePanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));
        
        // Table
        String[] columns = {"ID", "Name", "Email", "Role", "Status", "Registration Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column is editable
            }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(40);
        userTable.setFont(UIConstants.BODY_FONT);
        userTable.getTableHeader().setFont(UIConstants.SMALL_FONT_BOLD);
        userTable.getTableHeader().setBackground(AppColors.BACKGROUND_LIGHT);
        userTable.getTableHeader().setForeground(AppColors.TEXT_SECONDARY);
        userTable.setSelectionBackground(AppColors.PRIMARY_LIGHT);
        userTable.setSelectionForeground(AppColors.TEXT_PRIMARY);
        userTable.setGridColor(AppColors.BORDER_LIGHT);
        
        userTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainContent.add(tablePanel, BorderLayout.CENTER);
        panel.add(mainContent, BorderLayout.CENTER);

        // Back button
        JButton backButton = UIUtils.createButton("Back to Dashboard", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        backButton.addActionListener(e -> {
            dispose();
            try {
                new AdminDashboardNew().setVisible(true);
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error opening dashboard: " + ex.getMessage());
            }
        });
        
        JPanel buttonPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT), false);
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEditUserPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = UIUtils.createPanel(new BorderLayout(), false);
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createLabel("Edit User", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        JLabel subtitleLabel = UIUtils.createLabel("Update user information", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Form in a rounded panel
        RoundedPanel formContainer = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        formContainer.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER, 
            UIConstants.CORNER_RADIUS_MEDIUM, 
            1, 
            UIConstants.PADDING_LARGE
        ));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        formPanel.add(UIUtils.createLabel("Name:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField nameField = UIUtils.createRoundedTextField();
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(UIUtils.createLabel("Email:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField emailField = UIUtils.createRoundedTextField();
        formPanel.add(emailField, gbc);

        // Role field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(UIUtils.createLabel("Role:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> roleCombo = UIUtils.createRoundedComboBox(new String[]{"Admin", "Organizer", "Attendee"});
        formPanel.add(roleCombo, gbc);

        // Status field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(UIUtils.createLabel("Status:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> statusCombo = UIUtils.createRoundedComboBox(new String[]{"Active", "Inactive"});
        formPanel.add(statusCombo, gbc);

        // Reset password button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JButton resetPasswordButton = UIUtils.createButton("Reset Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        resetPasswordButton.addActionListener(e -> {
            String newPassword = generateTemporaryPassword();
            try {
                AuthController.ResetResult result = authController.resetPassword(emailField.getText(), newPassword);
                if (result.isSuccess()) {
                    UIUtils.showSuccess(this, "Password reset successful. New temporary password: " + newPassword);
                } else {
                    UIUtils.showError(this, "Failed to reset password: " + result.getMessage());
                }
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error resetting password: " + ex.getMessage());
            }
        });
        formPanel.add(resetPasswordButton, gbc);
        
        formContainer.add(formPanel, BorderLayout.CENTER);
        panel.add(formContainer, BorderLayout.CENTER);

        // Bottom button panel
        JPanel buttonPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0), false);
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Back button
        JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        cancelButton.addActionListener(e -> cardLayout.show(mainPanel, "USER_LIST"));
        buttonPanel.add(cancelButton);
        
        // Save button
        JButton saveButton = UIUtils.createButton("Save Changes", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        saveButton.addActionListener(e -> {
            try {
                selectedUser.setName(nameField.getText());
                selectedUser.setEmail(emailField.getText());
                selectedUser.setRole(User.UserRole.fromString((String)roleCombo.getSelectedItem()));
                selectedUser.setActive(statusCombo.getSelectedItem().equals("Active"));

                // Update user in database
                UserDAO userDAO = new SQLUserDAO();
                userDAO.update(selectedUser);

                // Refresh user list and show success message
                loadUsers();
                cardLayout.show(mainPanel, "USER_LIST");
                UIUtils.showSuccess(this, "User updated successfully");
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error updating user: " + ex.getMessage());
            }
        });
        buttonPanel.add(saveButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        // Show loading indicator
        LoadingPanel loadingPanel = LoadingPanel.showInGlassPane(this);
        loadingPanel.setMessage("Loading users...");

        // Disable the table
        userTable.setEnabled(false);

        // Load users in background
        new Thread(() -> {
            try {
                List<User> users = authController.getAllUsers();
                SwingUtilities.invokeLater(() -> {
                    // Update table model
                    DefaultTableModel model = (DefaultTableModel) userTable.getModel();
                    model.setRowCount(0);
                    
                    for (User user : users) {
                        model.addRow(new Object[]{
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole().getDisplayName(),
                            user.isActive() ? "Active" : "Inactive",
                            user.getRegistrationDate(),
                            "Edit"
                        });
                    }
                    
                    // Re-enable the table
                    userTable.setEnabled(true);
                    
                    // Hide loading indicator
                    LoadingPanel.hideFromGlassPane(this);
                });
            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> {
                    UIUtils.showError(this, "Error loading users: " + e.getMessage());
                    userTable.setEnabled(true);
                    LoadingPanel.hideFromGlassPane(this);
                });
            }
        }).start();
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = (String)roleFilter.getSelectedItem();
        String selectedStatus = (String)statusFilter.getSelectedItem();

        try {
            List<User> users = authController.getAllUsers();
            tableModel.setRowCount(0);
            for (User user : users) {
                if (matchesFilter(user, searchText, selectedRole, selectedStatus)) {
                    Object[] row = {
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().getDisplayName(),
                        user.isActive() ? "Active" : "Inactive",
                        user.getRegistrationDate(),
                        "Edit"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering users: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean matchesFilter(User user, String searchText, String selectedRole, String selectedStatus) {
        boolean matchesSearch = user.getName().toLowerCase().contains(searchText) ||
                              user.getEmail().toLowerCase().contains(searchText);
        
        boolean matchesRole = selectedRole.equals("All Roles") ||
                            user.getRole().getDisplayName().equals(selectedRole);
        
        boolean matchesStatus = selectedStatus.equals("All Status") ||
                              (selectedStatus.equals("Active") && user.isActive()) ||
                              (selectedStatus.equals("Inactive") && !user.isActive());
        
        return matchesSearch && matchesRole && matchesStatus;
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // Ensure at least one of each required character type
        password.append(chars.charAt(random.nextInt(26))); // Uppercase
        password.append(chars.charAt(26 + random.nextInt(26))); // Lowercase
        password.append(chars.charAt(52 + random.nextInt(10))); // Number
        password.append(chars.charAt(62 + random.nextInt(8))); // Special
        
        // Add remaining characters
        for (int i = 4; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }

    // Custom cell renderer for buttons
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Edit");
            return this;
        }
    }

    // Custom cell editor for buttons
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = "Edit";
            isPushed = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    selectedUser = authController.getUserById((int)tableModel.getValueAt(row, 0));
                    if (selectedUser != null) {
                        cardLayout.show(mainPanel, "EDIT_USER");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(button,
                        "Error loading user: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            isPushed = false;
            return label;
        }
    }
} 
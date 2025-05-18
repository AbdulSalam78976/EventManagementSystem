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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for search and filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search users...");
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterUsers();
            }
        });
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);

        // Role filter
        roleFilter = new JComboBox<>(new String[]{"All Roles", "Admin", "Organizer", "Attendee"});
        roleFilter.addActionListener(e -> filterUsers());
        topPanel.add(new JLabel("Role:"));
        topPanel.add(roleFilter);

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "Active", "Inactive"});
        statusFilter.addActionListener(e -> filterUsers());
        topPanel.add(new JLabel("Status:"));
        topPanel.add(statusFilter);

        panel.add(topPanel, BorderLayout.NORTH);

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
        userTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backButton = UIUtils.createButton("Back to Dashboard", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        backButton.addActionListener(e -> {
            dispose();
            try {
                new AdminDashboardNew().setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error opening dashboard: " + ex.getMessage());
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEditUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Role field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Organizer", "Attendee"});
        formPanel.add(roleCombo, gbc);

        // Status field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        formPanel.add(statusCombo, gbc);

        // Reset password button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton resetPasswordButton = UIUtils.createButton("Reset Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        resetPasswordButton.addActionListener(e -> {
            String newPassword = generateTemporaryPassword();
            try {
                AuthController.ResetResult result = authController.resetPassword(emailField.getText(), newPassword);
                if (result.isSuccess()) {
                    JOptionPane.showMessageDialog(this, 
                        "Password reset successful. New temporary password: " + newPassword,
                        "Password Reset",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to reset password: " + result.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error resetting password: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(resetPasswordButton, gbc);

        // Save button
        gbc.gridy = 5;
        JButton saveButton = UIUtils.createButton("Save Changes", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        saveButton.addActionListener(e -> {
            try {
                selectedUser.setName(nameField.getText());
                selectedUser.setEmail(emailField.getText());
                selectedUser.setRole(User.UserRole.fromString((String)roleCombo.getSelectedItem()));
                selectedUser.setActive(statusCombo.getSelectedItem().equals("Active"));
                
                authController.updateUser(selectedUser);
                JOptionPane.showMessageDialog(this, "User updated successfully");
                cardLayout.show(mainPanel, "USER_LIST");
                loadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error updating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(saveButton, gbc);

        // Back button
        gbc.gridy = 6;
        JButton backButton = UIUtils.createButton("Back to List", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "USER_LIST"));
        formPanel.add(backButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
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
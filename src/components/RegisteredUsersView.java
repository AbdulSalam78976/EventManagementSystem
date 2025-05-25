package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import controllers.AuthController;
import models.User;
import utils.AppColors;
import utils.SimpleDocumentListener;
import utils.UIConstants;
import utils.UIUtils;

public class RegisteredUsersView extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;
    private AuthController authController;

    public RegisteredUsersView() {
        try {
            authController = AuthController.getInstance();
            setLayout(new BorderLayout(10, 10));
            setBackground(AppColors.BACKGROUND);

            // Title Panel
            RoundedPanel titlePanel = new RoundedPanel(new BorderLayout(), AppColors.PRIMARY_LIGHT, UIConstants.CORNER_RADIUS_MEDIUM);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel titleLabel = UIUtils.createLabel("Registered Users", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
            titlePanel.add(titleLabel, BorderLayout.WEST);

            add(titlePanel, BorderLayout.NORTH);

            // Main Content Panel
            JPanel mainContent = new JPanel(new BorderLayout(10, 10));
            mainContent.setBackground(AppColors.BACKGROUND);
            mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Filters Panel
            RoundedPanel filtersPanel = new RoundedPanel(new FlowLayout(FlowLayout.LEFT, 10, 5), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
            filtersPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

            // Search Field
            JLabel searchLabel = UIUtils.createLabel("Search:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
            filtersPanel.add(searchLabel);

            searchField = UIUtils.createRoundedTextField();
            searchField.setPreferredSize(new Dimension(200, 30));
            searchField.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override
                public void update() {
                    filterUsers();
                }
            });
            filtersPanel.add(searchField);

            // Role Filter
            roleFilter = UIUtils.createRoundedComboBox(new String[]{"All Roles", "Administrator", "Event Organizer", "Attendee"});
            roleFilter.setPreferredSize(new Dimension(150, 30));
            roleFilter.addActionListener(e -> filterUsers());

            JLabel roleLabel = UIUtils.createLabel("Role:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
            filtersPanel.add(roleLabel);
            filtersPanel.add(roleFilter);

            // Status Filter
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
            userTable.setShowGrid(false);
            userTable.setIntercellSpacing(new Dimension(0, 0));

            // Custom renderers for each column
            userTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
            userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
            userTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
            userTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Role
            userTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Status
            userTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Registration Date
            userTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Actions

            // Status column renderer
            userTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setHorizontalAlignment(JLabel.CENTER);

                    if ("Active".equals(value)) {
                        label.setForeground(AppColors.SUCCESS);
                    } else {
                        label.setForeground(AppColors.ERROR);
                    }

                    return label;
                }
            });

            // Actions column renderer and editor
            TableColumn actionColumn = userTable.getColumnModel().getColumn(6);
            actionColumn.setCellRenderer(new ButtonRenderer());
            actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

            // Add table to scroll pane
            JScrollPane scrollPane = new JScrollPane(userTable);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(Color.WHITE);
            tablePanel.add(scrollPane);

            mainContent.add(tablePanel, BorderLayout.CENTER);
            add(mainContent, BorderLayout.CENTER);

            // Load initial data
            loadUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error initializing user management: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        try {
            List<User> users = authController.getAllUsers();
            updateTableData(users);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading users: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = (String) roleFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        try {
            List<User> users = authController.getAllUsers();
            List<User> filteredUsers = users.stream()
                .filter(user -> matchesFilter(user, searchText, selectedRole, selectedStatus))
                .toList();
            updateTableData(filteredUsers);
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

    private void updateTableData(List<User> users) {
        tableModel.setRowCount(0);
        for (User user : users) {
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

    // Custom button renderer for the actions column
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12)); // Support emoji rendering
            setForeground(AppColors.PRIMARY);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    // Custom button editor for the actions column
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12)); // Support emoji rendering
            button.setForeground(AppColors.PRIMARY);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click
                int row = userTable.getSelectedRow();
                if (row != -1) {
                    int userId = (int) userTable.getValueAt(row, 0);
                    try {
                        User user = authController.getUserById(userId);
                        if (user != null) {
                            editUser(user);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null,
                            "Error loading user details: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void editUser(User user) {
        SwingUtilities.invokeLater(() -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UserEditDialog dialog = new UserEditDialog(
                parentFrame,
                user,
                false,
                e -> refreshTable()
            );
            dialog.setVisible(true);
        });
    }

    private void refreshTable() {
        try {
            List<User> users = authController.getAllUsers();
            updateTableData(users);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error refreshing user data: " + e.getMessage());
        }
    }
}
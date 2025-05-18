package components;

import models.Registration;
import utils.AppColors;
import utils.UIUtils;
import controllers.RegistrationController;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import models.Event;
import controllers.EventController;

/**
 * Panel for displaying and managing event participants for an organizer
 */
public class ParticipantsPanel extends JPanel {
    private final int organizerId;
    private final JTable participantsTable;
    private final DefaultTableModel tableModel;
    private final RegistrationController registrationController;
    private final EventController eventController;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    /**
     * Creates a new ParticipantsPanel for the specified organizer
     * 
     * @param organizerId The ID of the organizer whose event participants to display
     * @throws SQLException if a database error occurs during initialization
     */
    public ParticipantsPanel(int organizerId) throws SQLException {
        if (organizerId <= 0) {
            throw new IllegalArgumentException("Invalid organizer ID");
        }
        
        this.organizerId = organizerId;
        this.registrationController = RegistrationController.getInstance();
        this.eventController = EventController.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"ID", "Name", "Email", "Event", "Registration Date", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Actions column index changed
            }
        };

        participantsTable = new JTable(tableModel);
        participantsTable.setRowHeight(40);
        participantsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        participantsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        participantsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        participantsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        participantsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Event Name
        participantsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Registration Date
        participantsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        participantsTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Actions

        // Hide ID column
        participantsTable.getColumnModel().getColumn(0).setMinWidth(0);
        participantsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        participantsTable.getColumnModel().getColumn(0).setWidth(0);

        // Custom renderer for status column
        participantsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        // Custom renderer and editor for actions column
        participantsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        participantsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(participantsTable));

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        refreshParticipants();
    }

    /**
     * Creates the header panel with title and search functionality
     * 
     * @return The created header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Event Participants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.putClientProperty("JTextField.placeholderText", "Search participants...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterParticipants(searchField.getText());
            }
        });

        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Refreshes the participants table with current data for the organizer's events
     * 
     * @throws SQLException if a database error occurs
     */
    public void refreshParticipants() throws SQLException {
        try {
            // Clear existing data
            tableModel.setRowCount(0);

            // Get all events for the organizer
            List<Event> events = eventController.getEventsByOrganizer(organizerId);

            // Get registrations for each event and add to table
            for (Event event : events) {
                List<Registration> registrations = registrationController.getEventRegistrations(event.getId());
                for (Registration reg : registrations) {
                    Object[] row = {
                        reg.getId(),
                        reg.getAttendee().getName(),
                        reg.getAttendee().getEmail(),
                        event.getName(),
                        dateFormat.format(reg.getRegistrationDate()),
                        reg.getStatus(),
                        "Actions"
                    };
                    tableModel.addRow(row);
                }
            }

            // Update status column header with count
            participantsTable.getColumnModel().getColumn(5).setHeaderValue(
                "Status (" + tableModel.getRowCount() + " participants)"
            );
            participantsTable.getTableHeader().repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading participants: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    /**
     * Filters the participants table based on search text
     * 
     * @param searchText The text to search for
     */
    private void filterParticipants(String searchText) {
        try {
            // Clear existing data
            tableModel.setRowCount(0);

            // Get all events for the organizer
            List<Event> events = eventController.getEventsByOrganizer(organizerId);

            // Filter and add to table
            for (Event event : events) {
                List<Registration> registrations = registrationController.getEventRegistrations(event.getId());
                for (Registration reg : registrations) {
                    if (searchText.isEmpty() ||
                        reg.getAttendee().getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        reg.getAttendee().getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                        event.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        
                        Object[] row = {
                            reg.getId(),
                            reg.getAttendee().getName(),
                            reg.getAttendee().getEmail(),
                            event.getName(),
                            dateFormat.format(reg.getRegistrationDate()),
                            reg.getStatus(),
                            "Actions"
                        };
                        tableModel.addRow(row);
                    }
                }
            }

            // Update status column header with count
            participantsTable.getColumnModel().getColumn(5).setHeaderValue(
                "Status (" + tableModel.getRowCount() + " participants)"
            );
            participantsTable.getTableHeader().repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error filtering participants: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Custom renderer for the status column
     */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component renderer = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Registration.Status) {
                Registration.Status status = (Registration.Status) value;
                switch (status) {
                    case PENDING:
                        renderer.setForeground(AppColors.ACCENT_YELLOW);
                        break;
                    case APPROVED:
                        renderer.setForeground(AppColors.ACCENT_GREEN);
                        break;
                    case REJECTED:
                        renderer.setForeground(AppColors.ACCENT_RED);
                        break;
                    case CANCELLED:
                        renderer.setForeground(AppColors.TEXT_SECONDARY);
                        break;
                    default:
                        renderer.setForeground(AppColors.TEXT_PRIMARY);
                        break;
                }
            } else {
                renderer.setForeground(AppColors.TEXT_PRIMARY);
            }
            
            return renderer;
        }
    }

    /**
     * Custom renderer for the actions column buttons
     */
    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton approveButton;
        private final JButton rejectButton;
        private final JButton cancelButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);

            approveButton = UIUtils.createButton("Approve", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
            rejectButton = UIUtils.createButton("Reject", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);
            cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);

            add(approveButton);
            add(rejectButton);
            add(cancelButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Registration.Status status = (Registration.Status) table.getValueAt(row, 5);
            
            approveButton.setVisible(status == Registration.Status.PENDING);
            rejectButton.setVisible(status == Registration.Status.REJECTED);
            cancelButton.setVisible(status == Registration.Status.CANCELLED);
            
            if (isSelected) {
                // Optional: change background for selected row
            }
            
            return this;
        }
    }

    /**
     * Custom editor for the actions column buttons
     */
    private class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton approveButton;
        private final JButton rejectButton;
        private final JButton cancelButton;
        private int clickedRow;
        private JTable table;

        public ButtonEditor(JTable table) {
            super(new JCheckBox()); // Pass a dummy checkbox
            this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            approveButton = UIUtils.createButton("Approve", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
            rejectButton = UIUtils.createButton("Reject", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);
            cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);

            approveButton.addActionListener(e -> fireEditingStopped());
            rejectButton.addActionListener(e -> fireEditingStopped());
            cancelButton.addActionListener(e -> fireEditingStopped());

            panel.add(approveButton);
            panel.add(rejectButton);
            panel.add(cancelButton);

            // Make checkbox invisible
            ((JCheckBox)getComponent()).setVisible(false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            clickedRow = row;
            Registration.Status status = (Registration.Status) table.getValueAt(row, 5);
            
            approveButton.setVisible(status == Registration.Status.PENDING);
            rejectButton.setVisible(status == Registration.Status.REJECTED);
            cancelButton.setVisible(status == Registration.Status.CANCELLED);
            
            if (isSelected) {
                // Optional: change background for selected row
            }
            
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            // Handle button click actions here based on which button was clicked
            // This part needs more logic to determine which button was pressed
            // For now, just stopping editing
            System.out.println("Action button clicked on row: " + clickedRow);

            // You would typically add logic here to get the Registration ID from clickedRow
            // and call the appropriate controller method (approve/reject/cancel registration)

            // Example (assuming Registration ID is in the first hidden column):
            // int registrationId = (Integer) table.getModel().getValueAt(clickedRow, 0);
            // try {
            //     if (/* approve button was clicked */) {
            //         registrationController.approveRegistration(registrationId);
            //     } else if (/* reject button was clicked */) {
            //         registrationController.rejectRegistration(registrationId);
            //     } else if (/* cancel button was clicked */) {
            //         registrationController.cancelRegistration(registrationId);
            //     }
            //     // Refresh participants table
            //     ((ParticipantsPanel) SwingUtilities.getAncestorOfClass(ParticipantsPanel.class, table)).refreshParticipants();
            // } catch (SQLException e) {
            //      JOptionPane.showMessageDialog(table,
            //          "Error updating registration: " + e.getMessage(),
            //          "Database Error",
            //          JOptionPane.ERROR_MESSAGE);
            // }

            return "Actions"; // Return a dummy value
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
        }
    }
} 
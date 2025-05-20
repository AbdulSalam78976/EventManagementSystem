package components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import models.Event;
import models.User;
import controllers.EventController;
import controllers.AuthController;
import utils.UIUtils;
import utils.AppColors;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * Panel for managing events (update, delete) for Admin and Organizer roles.
 */
public class ManageEventsPanel extends JPanel {
    private final String userRole;
    private final int organizerId;
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private EventController eventController;

    /**
     * Creates a new ManageEventsPanel.
     *
     * @param userRole The role of the user ("Admin" or "Organizer").
     * @param organizerId The ID of the organizer (only relevant for Organizer role).
     * @throws SQLException if a database error occurs during initialization.
     */
    public ManageEventsPanel(String userRole, int organizerId) throws SQLException {
        if (!userRole.equals("Admin") && !userRole.equals("Organizer")) {
            throw new IllegalArgumentException("Invalid user role specified.");
        }
        if (userRole.equals("Organizer") && organizerId <= 0) {
             throw new IllegalArgumentException("Invalid organizer ID for Organizer role.");
        }

        this.userRole = userRole;
        this.organizerId = organizerId;
        this.eventController = EventController.getInstance();

        setupUI();
        loadEventsData();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Events");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Add Create New Event button
        JButton createEventButton = UIUtils.createButton("Create New Event", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        createEventButton.addActionListener(e -> showCreateEventForm());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(createEventButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table for events
        String[] columns = {"ID", "Event Name", "Date", "Venue", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Actions column
            }
        };

        eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(40);
        eventsTable.getTableHeader().setReorderingAllowed(false);

        // Hide ID column
        eventsTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventsTable.getColumnModel().getColumn(0).setWidth(0);

        // Add custom renderer and editor for Actions column
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionsRenderer());
        eventsTable.getColumnModel().getColumn(6).setCellEditor(new ActionsEditor(eventsTable));

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // TODO: Add search/filter functionality
    }

    private void loadEventsData() {
        try {
            List<Event> events = eventController.getAllEvents();
            tableModel.setRowCount(0);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Event event : events) {
                Object[] row = {
                    event.getId(),
                    event.getTitle(),
                    event.getEventDate().format(dateFormatter),
                    event.getVenueName(),
                    event.getStatus().toString(),
                    "Actions"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading events: " + e.getMessage());
        }
    }

    // TODO: Implement methods for Update and Delete actions

    /**
     * Custom renderer for the Actions column.
     */
    private class ActionsRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton updateButton;
        private final JButton deleteButton;

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true); // Or false, depending on desired look

            updateButton = UIUtils.createButton("Update", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
            deleteButton = UIUtils.createButton("Delete", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);

            add(updateButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            // You can customize button appearance based on row data if needed
            return this;
        }
    }

    /**
     * Custom editor for the Actions column.
     */
    private class ActionsEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton updateButton;
        private final JButton deleteButton;
        private int clickedRow;
        private JTable table;

        public ActionsEditor(JTable table) {
            super(new JCheckBox()); // Pass a dummy component
            this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true); // Or false

            updateButton = UIUtils.createButton("Update", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
            deleteButton = UIUtils.createButton("Delete", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);

            panel.add(updateButton);
            panel.add(deleteButton);

            // Add action listeners to the buttons
            updateButton.addActionListener(e -> {
                // TODO: Implement update action
                System.out.println("Update clicked on row: " + clickedRow);
                // Call the method in the parent panel to handle the update logic
                ((ManageEventsPanel) SwingUtilities.getAncestorOfClass(ManageEventsPanel.class, table)).handleEditEvent(clickedRow);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                // TODO: Implement delete action
                System.out.println("Delete clicked on row: " + clickedRow);
                 fireEditingStopped();
            });
             // Make checkbox invisible
            ((JCheckBox)getComponent()).setVisible(false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                       boolean isSelected, int row, int column) {
            this.clickedRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            // This method should return the value that goes into the model after editing.
            // Since our buttons don't change the cell value, we can return a dummy value.
            return "Actions";
        }

         // Method to get the event ID for the clicked row
        private int getEventId(int row) {
             return (Integer) table.getModel().getValueAt(row, 0);
        }

         @Override
        public boolean stopCellEditing() {
            // Handle button actions here before stopping editing
            // This logic needs to be here to determine which button was clicked
            // before the editor is stopped.

             // Determine which button was clicked (this is a simplification; a real implementation
             // would need a way to know which button triggered the stopEditing)
             // For now, we'll assume a delete confirmation if delete was the last clicked.

             Component editor = getComponent();
             if (editor instanceof JPanel) {
                 JPanel editorPanel = (JPanel) editor;
                 Component[] components = editorPanel.getComponents();
                 for (Component comp : components) {
                     if (comp instanceof JButton) {
                         JButton button = (JButton) comp;
                         // This is a simplified check - ideally, you'd use the ActionEvent source
                         // but it's not directly available here easily. A robust solution might
                         // involve storing the last clicked button or using a custom TableCellEditor.
                         if (button.getText().equals("Delete") && button.getModel().isPressed()) {
                             // Delete button was likely pressed
                             int confirm = JOptionPane.showConfirmDialog(
                                 table,
                                 "Are you sure you want to delete this event?",
                                 "Confirm Delete",
                                 JOptionPane.YES_NO_OPTION
                             );
                             if (confirm == JOptionPane.YES_OPTION) {
                                 try {
                                     int eventIdToDelete = getEventId(clickedRow);
                                     boolean deleted = eventController.deleteEvent(eventIdToDelete);
                                     if (deleted) {
                                         UIUtils.showSuccess(SwingUtilities.getWindowAncestor(table), "Event deleted successfully.");
                                         // Refresh the table after deletion
                                         ((ManageEventsPanel) SwingUtilities.getAncestorOfClass(ManageEventsPanel.class, table)).loadEventsData();
                                     } else {
                                         UIUtils.showError(SwingUtilities.getWindowAncestor(table), "Failed to delete event.");
                                     }
                                 } catch (SQLException ex) {
                                     UIUtils.showError(SwingUtilities.getWindowAncestor(table), "Database error deleting event: " + ex.getMessage());
                                     ex.printStackTrace();
                                 } catch (Exception ex) {
                                      UIUtils.showError(SwingUtilities.getWindowAncestor(table), "An unexpected error occurred: " + ex.getMessage());
                                      ex.printStackTrace();
                                 }
                             }
                              // Always stop editing after handling the click
                             return super.stopCellEditing();
                         } else if (button.getText().equals("Update") && button.getModel().isPressed()) {
                             // Update button was likely pressed
                             // TODO: Implement update dialog logic here
                             System.out.println("Update action triggered.");
                             // Always stop editing after handling the click
                             return super.stopCellEditing();
                         }
                     }
                 }
             }

             // If no specific button action was handled, stop editing anyway
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
        }
    }

    /**
     * Starts the update action for the event at the given row.
     */
    private void handleEditEvent(int row) {
        try {
            int eventId = (Integer) eventsTable.getModel().getValueAt(row, 0);
            Event event = eventController.getEvent(eventId);
            if (event != null) {
                showEditEventDialog(event);
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading event: " + e.getMessage());
        }
    }

    private void showEditEventDialog(Event event) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Event", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);

        CreateEventForm form = new CreateEventForm(userRole, success -> {
            if (success) {
                loadEventsData(); // Refresh the table
            }
            dialog.dispose();
        });

        // Set the form data
        form.setEventData(event);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Shows the Create Event form in a dialog.
     */
    private void showCreateEventForm() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Create New Event", true);
        dialog.setLayout(new BorderLayout());
        
        CreateEventForm form = new CreateEventForm(userRole, success -> {
            dialog.dispose();
            if (success) {
                loadEventsData(); // Refresh the table after successful creation
            }
        });
        
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 
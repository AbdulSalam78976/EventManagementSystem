package components;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;

/**
 * A reusable calendar panel component that displays events on their respective dates
 * with enhanced features like event categories, time display, and view options.
 */
public class CalendarPanel extends JPanel {
    private Calendar currentCalendar;
    private JLabel monthYearLabel;
    private JPanel daysPanel;
    private List<CalendarEvent> events;
    private CalendarEventClickListener eventClickListener;
    private boolean isWeekView = false;
    private JPopupMenu eventPreviewPopup;
    private Timer popupTimer;
    private CalendarEventDragListener dragListener;

    /**
     * Represents an event to be displayed on the calendar
     */
    public static class CalendarEvent {
        private String id;
        private String title;
        private Date date;
        private String location;
        private boolean isRegistered;
        private String category;
        private String description;
        private Date startTime;
        private Date endTime;
        private Color categoryColor;

        public CalendarEvent(String id, String title, Date date, Date startTime, Date endTime, 
                           String location, String category, String description, boolean isRegistered) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.location = location;
            this.isRegistered = isRegistered;
            this.category = category;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.categoryColor = getCategoryColor(category);
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public Date getDate() { return date; }
        public String getLocation() { return location; }
        public boolean isRegistered() { return isRegistered; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public Date getStartTime() { return startTime; }
        public Date getEndTime() { return endTime; }
        public Color getCategoryColor() { return categoryColor; }

        private Color getCategoryColor(String category) {
            switch (category.toLowerCase()) {
                case "workshop": return new Color(41, 128, 185); // Blue
                case "conference": return new Color(142, 68, 173); // Purple
                case "meeting": return new Color(39, 174, 96); // Green
                case "social": return new Color(230, 126, 34); // Orange
                case "deadline": return new Color(231, 76, 60); // Red
                default: return AppColors.PRIMARY;
            }
        }
    }

    /**
     * Interface for handling calendar event clicks
     */
    public interface CalendarEventClickListener {
        void onEventClick(CalendarEvent event);
    }

    /**
     * Interface for handling calendar event drag and drop
     */
    public interface CalendarEventDragListener {
        void onEventDragged(CalendarEvent event, Date newDate);
    }

    public CalendarPanel() {
        this(new ArrayList<>(), null);
    }

    public CalendarPanel(List<CalendarEvent> events, CalendarEventClickListener listener) {
        this.events = events;
        this.eventClickListener = listener;
        this.currentCalendar = Calendar.getInstance();
        
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize event preview popup
        initializeEventPreview();

        // Create calendar header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create days of week header
        JPanel weekdaysPanel = createWeekdaysPanel();
        add(weekdaysPanel, BorderLayout.CENTER);

        // Create days grid
        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysPanel.setBackground(Color.WHITE);
        add(daysPanel, BorderLayout.SOUTH);

        // Initial calendar render
        updateCalendar();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Month and year label
        monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setFont(UIConstants.HEADER_FONT);
        monthYearLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(monthYearLabel, BorderLayout.CENTER);

        // Navigation and view toggle panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setBackground(Color.WHITE);

        // View toggle buttons
        JPanel viewTogglePanel = new JPanel();
        viewTogglePanel.setBackground(Color.WHITE);
        ButtonGroup viewGroup = new ButtonGroup();
        
        JToggleButton monthViewBtn = new JToggleButton("Month");
        monthViewBtn.setSelected(!isWeekView);
        monthViewBtn.setFocusPainted(false);
        monthViewBtn.addActionListener(e -> {
            isWeekView = false;
            updateCalendar();
        });
        
        JToggleButton weekViewBtn = new JToggleButton("Week");
        weekViewBtn.setSelected(isWeekView);
        weekViewBtn.setFocusPainted(false);
        weekViewBtn.addActionListener(e -> {
            isWeekView = true;
            updateCalendar();
        });
        
        viewGroup.add(monthViewBtn);
        viewGroup.add(weekViewBtn);
        viewTogglePanel.add(monthViewBtn);
        viewTogglePanel.add(weekViewBtn);
        
        controlPanel.add(viewTogglePanel, BorderLayout.WEST);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = UIUtils.createButton("◀", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        prevButton.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        JButton nextButton = UIUtils.createButton("▶", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        nextButton.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        JButton todayButton = UIUtils.createButton("Today", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.SMALL);
        todayButton.addActionListener(e -> {
            currentCalendar = Calendar.getInstance();
            updateCalendar();
        });

        navPanel.add(prevButton);
        navPanel.add(todayButton);
        navPanel.add(nextButton);

        controlPanel.add(navPanel, BorderLayout.EAST);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createWeekdaysPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 7));
        panel.setBackground(Color.WHITE);

        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(AppColors.TEXT_SECONDARY);
            panel.add(label);
        }

        return panel;
    }

    private void updateCalendar() {
        // Update month and year label
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthYearLabel.setText(sdf.format(currentCalendar.getTime()));

        // Clear previous days
        daysPanel.removeAll();

        // Get current month's first day and number of days
        Calendar cal = (Calendar) currentCalendar.clone();
        
        if (isWeekView) {
            // Set to the first day of the week
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            
            // Add weekday headers
            String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String weekday : weekdays) {
                JLabel label = new JLabel(weekday, JLabel.CENTER);
                label.setFont(UIConstants.BODY_FONT_BOLD);
                label.setForeground(AppColors.TEXT_SECONDARY);
                daysPanel.add(label);
            }
            
            // Add the days of the week
            for (int i = 0; i < 7; i++) {
                boolean isToday = isToday(cal);
                List<CalendarEvent> dayEvents = getEventsForDay(cal.getTime());
                daysPanel.add(createDayPanel(cal.get(Calendar.DAY_OF_MONTH), isToday, dayEvents));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            // Month view
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Add weekday headers
            String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String weekday : weekdays) {
                JLabel label = new JLabel(weekday, JLabel.CENTER);
                label.setFont(UIConstants.BODY_FONT_BOLD);
                label.setForeground(AppColors.TEXT_SECONDARY);
                daysPanel.add(label);
            }

            // Add empty cells for days before the first day of month
            for (int i = 0; i < firstDayOfWeek; i++) {
                daysPanel.add(createEmptyDayPanel());
            }

            // Add day cells
            for (int day = 1; day <= daysInMonth; day++) {
                cal.set(Calendar.DAY_OF_MONTH, day);
                boolean isToday = isToday(cal);
                List<CalendarEvent> dayEvents = getEventsForDay(cal.getTime());
                daysPanel.add(createDayPanel(day, isToday, dayEvents));
            }

            // Add empty cells for remaining grid
            int remainingCells = 42 - (firstDayOfWeek + daysInMonth); // 6 rows * 7 days = 42
            for (int i = 0; i < remainingCells; i++) {
                daysPanel.add(createEmptyDayPanel());
            }
        }

        // Update the layout
        if (isWeekView) {
            daysPanel.setLayout(new GridLayout(2, 7, 5, 5)); // 2 rows: headers and days
        } else {
            daysPanel.setLayout(new GridLayout(0, 7, 5, 5)); // 7 columns, variable rows
        }

        // Refresh the panel
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private boolean isToday(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
               today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
               today.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH);
    }

    private JPanel createEmptyDayPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        return panel;
    }

    private JPanel createDayPanel(int day, boolean isToday, List<CalendarEvent> dayEvents) {
        // Create a panel with BoxLayout to stack day number and events
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Set background color based on whether it's today
        if (isToday) {
            panel.setBackground(new Color(230, 240, 255)); // Light blue for today
            panel.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY, 2));
        } else {
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        }

        // Day number label
        JLabel dayLabel = new JLabel(String.valueOf(day), JLabel.CENTER);
        dayLabel.setFont(new Font("Segoe UI", isToday ? Font.BOLD : Font.PLAIN, 14));
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dayLabel);
        
        // Add events for this day
        if (!dayEvents.isEmpty()) {
            panel.add(Box.createVerticalStrut(5));
            
            // Create a panel for events with limited height
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(panel.getBackground());
            
            // Add up to 3 events (to avoid overcrowding)
            int count = 0;
            for (CalendarEvent event : dayEvents) {
                if (count >= 3) {
                    // If more than 3 events, show a "more" indicator
                    JLabel moreLabel = new JLabel("+" + (dayEvents.size() - 3) + " more", JLabel.CENTER);
                    moreLabel.setFont(UIConstants.SMALL_FONT);
                    moreLabel.setForeground(AppColors.TEXT_SECONDARY);
                    moreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    eventsPanel.add(moreLabel);
                    break;
                }
                
                // Create event indicator
                JPanel eventIndicator = createEventIndicator(event);
                eventsPanel.add(eventIndicator);
                eventsPanel.add(Box.createVerticalStrut(2));
                count++;
            }
            
            panel.add(eventsPanel);
        }

        // Add drop target for drag and drop
        panel.setTransferHandler(new EventTransferHandler(currentCalendar, day));
        
        return panel;
    }

    private JPanel createEventIndicator(CalendarEvent event) {
        JPanel indicator = new JPanel(new BorderLayout());
        indicator.setBackground(event.getCategoryColor());
        indicator.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        indicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        // Event title with time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String displayText = String.format("%s - %s", 
            timeFormat.format(event.getStartTime()),
            event.getTitle()
        );
        
        JLabel eventLabel = new JLabel(displayText);
        eventLabel.setFont(UIConstants.SMALL_FONT);
        eventLabel.setForeground(Color.WHITE);
        indicator.add(eventLabel, BorderLayout.CENTER);

        // Add mouse listeners for preview popup
        indicator.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                eventPreviewPopup.removeAll();
                eventPreviewPopup.add(createEventPreviewPanel(event));
                popupTimer.restart();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                popupTimer.stop();
                eventPreviewPopup.setVisible(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (eventClickListener != null) {
                    eventClickListener.onEventClick(event);
                }
            }
        });

        // Add drag source
        indicator.setTransferHandler(new EventTransferHandler(event));
        
        // Enable dragging
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent)e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                handler.exportAsDrag(comp, e, TransferHandler.MOVE);
            }
        };
        indicator.addMouseListener(dragAdapter);
        
        return indicator;
    }

    // Inner class for handling event drag and drop
    private class EventTransferHandler extends TransferHandler {
        private CalendarEvent event;
        private Calendar calendar;
        private int day;

        public EventTransferHandler(CalendarEvent event) {
            this.event = event;
        }

        public EventTransferHandler(Calendar calendar, int day) {
            this.calendar = (Calendar) calendar.clone();
            this.day = day;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor) && calendar != null;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(event.getId());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String eventId = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                CalendarEvent draggedEvent = events.stream()
                    .filter(e -> e.getId().equals(eventId))
                    .findFirst()
                    .orElse(null);

                if (draggedEvent != null && dragListener != null) {
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    dragListener.onEventDragged(draggedEvent, calendar.getTime());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
        updateCalendar();
    }

    public void setEventClickListener(CalendarEventClickListener listener) {
        this.eventClickListener = listener;
    }

    private void initializeEventPreview() {
        eventPreviewPopup = new JPopupMenu();
        eventPreviewPopup.setBackground(Color.WHITE);
        eventPreviewPopup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create a timer for showing the popup with a delay
        popupTimer = new Timer(500, e -> {
            Point location = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(location, this);
            eventPreviewPopup.show(this, location.x + 15, location.y + 15);
        });
        popupTimer.setRepeats(false);
    }

    private JPanel createEventPreviewPanel(CalendarEvent event) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 150));

        // Event title
        JLabel titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(UIConstants.BODY_FONT_BOLD);
        titleLabel.setForeground(event.getCategoryColor());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));

        // Event time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        JLabel timeLabel = new JLabel(String.format("%s - %s", 
            timeFormat.format(event.getStartTime()),
            timeFormat.format(event.getEndTime())
        ));
        timeLabel.setFont(UIConstants.SMALL_FONT);
        timeLabel.setForeground(AppColors.TEXT_SECONDARY);
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(5));

        // Event location
        JLabel locationLabel = new JLabel(event.getLocation());
        locationLabel.setFont(UIConstants.SMALL_FONT);
        locationLabel.setForeground(AppColors.TEXT_SECONDARY);
        panel.add(locationLabel);
        panel.add(Box.createVerticalStrut(10));

        // Event description
        JTextArea descriptionArea = new JTextArea(event.getDescription());
        descriptionArea.setFont(UIConstants.SMALL_FONT);
        descriptionArea.setForeground(AppColors.TEXT_SECONDARY);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setPreferredSize(new Dimension(230, 60));
        panel.add(descriptionArea);

        // Category badge
        JLabel categoryLabel = new JLabel(event.getCategory().toUpperCase());
        categoryLabel.setFont(UIConstants.SMALL_FONT);
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setOpaque(true);
        categoryLabel.setBackground(event.getCategoryColor());
        categoryLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        panel.add(Box.createVerticalStrut(10));
        panel.add(categoryLabel);

        return panel;
    }

    public void setDragListener(CalendarEventDragListener listener) {
        this.dragListener = listener;
    }

    private List<CalendarEvent> getEventsForDay(Date date) {
        List<CalendarEvent> dayEvents = new ArrayList<>();
        
        // Simple date comparison (ignoring time)
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        
        for (CalendarEvent event : events) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(event.getDate());
            cal2.set(Calendar.HOUR_OF_DAY, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MILLISECOND, 0);
            
            if (cal1.getTimeInMillis() == cal2.getTimeInMillis()) {
                dayEvents.add(event);
            }
        }
        
        return dayEvents;
    }
}

package components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import utils.AppColors;
import utils.UIUtils;

/**
 * A reusable calendar panel component that displays events on their respective dates
 */
public class CalendarPanel extends JPanel {
    private Calendar currentCalendar;
    private JLabel monthYearLabel;
    private JPanel daysPanel;
    private List<CalendarEvent> events;
    private CalendarEventClickListener eventClickListener;

    /**
     * Represents an event to be displayed on the calendar
     */
    public static class CalendarEvent {
        private String id;
        private String title;
        private Date date;
        private String location;
        private boolean isRegistered;

        public CalendarEvent(String id, String title, Date date, String location, boolean isRegistered) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.location = location;
            this.isRegistered = isRegistered;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public Date getDate() { return date; }
        public String getLocation() { return location; }
        public boolean isRegistered() { return isRegistered; }
    }

    /**
     * Interface for handling calendar event clicks
     */
    public interface CalendarEventClickListener {
        void onEventClick(CalendarEvent event);
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
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        monthYearLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(monthYearLabel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("◀");
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        JButton nextButton = new JButton("▶");
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        JButton todayButton = new JButton("Today");
        todayButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        todayButton.setFocusPainted(false);
        todayButton.addActionListener(e -> {
            currentCalendar = Calendar.getInstance();
            updateCalendar();
        });

        navPanel.add(prevButton);
        navPanel.add(todayButton);
        navPanel.add(nextButton);

        panel.add(navPanel, BorderLayout.SOUTH);
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
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before the first day of month
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(createEmptyDayPanel());
        }

        // Add day cells
        Calendar today = Calendar.getInstance();
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            boolean isToday = (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                              today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                              today.get(Calendar.DAY_OF_MONTH) == day);
            
            // Get events for this day
            List<CalendarEvent> dayEvents = getEventsForDay(cal.getTime());
            
            daysPanel.add(createDayPanel(day, isToday, dayEvents));
        }

        // Add empty cells for remaining grid
        int remainingCells = 42 - (firstDayOfWeek + daysInMonth); // 6 rows * 7 days = 42
        for (int i = 0; i < remainingCells; i++) {
            daysPanel.add(createEmptyDayPanel());
        }

        // Refresh the panel
        daysPanel.revalidate();
        daysPanel.repaint();
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
                    moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                    moreLabel.setForeground(AppColors.TEXT_SECONDARY);
                    moreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    eventsPanel.add(moreLabel);
                    break;
                }
                
                // Create event indicator
                JLabel eventLabel = new JLabel(event.getTitle());
                eventLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                eventLabel.setForeground(Color.WHITE);
                eventLabel.setOpaque(true);
                eventLabel.setBackground(event.isRegistered() ? AppColors.SUCCESS : AppColors.PRIMARY);
                eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                eventLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                eventLabel.setToolTipText(event.getTitle() + " at " + event.getLocation());
                
                // Add click listener
                if (eventClickListener != null) {
                    eventLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    eventLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            eventClickListener.onEventClick(event);
                        }
                    });
                }
                
                eventsPanel.add(eventLabel);
                eventsPanel.add(Box.createVerticalStrut(2));
                count++;
            }
            
            panel.add(eventsPanel);
        }
        
        return panel;
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

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
        updateCalendar();
    }

    public void setEventClickListener(CalendarEventClickListener listener) {
        this.eventClickListener = listener;
    }
}

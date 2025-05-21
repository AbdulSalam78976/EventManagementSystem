package utils;

import java.awt.Color;

/**
 * Modern color palette for the Event Management System
 * Includes gradient color pairs for buttons and UI elements
 */
public class AppColors {
    // Primary Colors
    public static final Color PRIMARY = new Color(41, 128, 185);
    public static final Color PRIMARY_DARK = new Color(44, 62, 80);
    public static final Color PRIMARY_LIGHT = new Color(52, 152, 219);
    public static final Color PRIMARY_ACCENT = new Color(41, 128, 185, 20);

    // Secondary Colors
    public static final Color SECONDARY = new Color(46, 204, 113);
    public static final Color SECONDARY_DARK = new Color(39, 174, 96);
    public static final Color SECONDARY_LIGHT = new Color(46, 204, 113, 20);

    // Accent Colors
    public static final Color ACCENT = new Color(155, 89, 182);
    public static final Color ACCENT_DARK = new Color(142, 68, 173);
    public static final Color ACCENT_LIGHT = new Color(155, 89, 182, 20);

    // Specific Accent Colors for Dashboard Stats
    public static final Color ACCENT_YELLOW = new Color(241, 196, 15); // Corresponds to WARNING
    public static final Color ACCENT_GREEN = new Color(46, 204, 113);  // Corresponds to SUCCESS
    public static final Color ACCENT_RED = new Color(231, 76, 60);    // Corresponds to ERROR

    // Background Colors
    public static final Color BACKGROUND = new Color(236, 240, 241);
    public static final Color BACKGROUND_LIGHT = new Color(245, 246, 250);
    public static final Color BACKGROUND_DARK = new Color(30, 30, 30); // Darker shade for sidebar
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color TEXT_LIGHT = new Color(236, 240, 241); // Lighter text for dark backgrounds
    public static final Color TEXT_LIGHT_SECONDARY = new Color(189, 195, 199); // Slightly darker light text
    public static final Color TEXT_DARK = new Color(44, 62, 80);

    // Border Colors
    public static final Color BORDER = new Color(189, 195, 199); // Lighter border for normal state
    public static final Color BORDER_LIGHT = new Color(236, 240, 241);
    public static final Color BORDER_FOCUS = new Color(52, 152, 219); // Blue focus border (matches PRIMARY_LIGHT)

    // Status Colors
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color WARNING = new Color(241, 196, 15);
    public static final Color ERROR = new Color(231, 76, 60);
    public static final Color INFO = new Color(52, 152, 219);

    // Hover Colors
    public static final Color HOVER_PRIMARY = new Color(41, 128, 185, 20);
    public static final Color HOVER_SECONDARY = new Color(46, 204, 113, 20);
    public static final Color HOVER_ACCENT = new Color(155, 89, 182, 20);
    public static final Color HOVER_ERROR = new Color(231, 76, 60, 20);
    public static final Color HOVER_WARNING = new Color(241, 196, 15, 20);
    public static final Color HOVER_SUCCESS = new Color(46, 204, 113, 20);
    public static final Color HOVER_INFO = new Color(52, 152, 219, 20);

    // Shadow Colors
    public static final Color SHADOW = new Color(0, 0, 0, 10);
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 5);
    public static final Color SHADOW_DARK = new Color(0, 0, 0, 20);

    // Overlay Colors
    public static final Color OVERLAY = new Color(0, 0, 0, 50);
    public static final Color OVERLAY_LIGHT = new Color(0, 0, 0, 25);
    public static final Color OVERLAY_DARK = new Color(0, 0, 0, 75);

    // Chart Colors
    public static final Color[] CHART_COLORS = {
        new Color(41, 128, 185),
        new Color(46, 204, 113),
        new Color(155, 89, 182),
        new Color(241, 196, 15),
        new Color(231, 76, 60),
        new Color(52, 152, 219),
        new Color(230, 126, 34),
        new Color(149, 165, 166)
    };

    // Calendar Colors
    public static final Color CALENDAR_TODAY = new Color(46, 204, 113, 20);
    public static final Color CALENDAR_SELECTED = new Color(41, 128, 185, 20);
    public static final Color CALENDAR_HOVER = new Color(52, 152, 219, 20);
    public static final Color CALENDAR_DISABLED = new Color(189, 195, 199, 20);
    public static final Color CALENDAR_WEEKEND = new Color(231, 76, 60, 10);
    public static final Color CALENDAR_HOLIDAY = new Color(241, 196, 15, 10);
    public static final Color CALENDAR_EVENT = new Color(155, 89, 182, 20);
    public static final Color CALENDAR_EVENT_HOVER = new Color(155, 89, 182, 30);
    public static final Color CALENDAR_EVENT_SELECTED = new Color(155, 89, 182, 40);
    public static final Color CALENDAR_EVENT_DISABLED = new Color(155, 89, 182, 10);
    public static final Color CALENDAR_EVENT_TODAY = new Color(46, 204, 113, 30);
    public static final Color CALENDAR_EVENT_WEEKEND = new Color(231, 76, 60, 20);
    public static final Color CALENDAR_EVENT_HOLIDAY = new Color(241, 196, 15, 20);

    private AppColors() {
        // Prevent instantiation
    }
}

package utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Utility class for handling icons in the application
 */
public class IconUtils {
    // Icon sizes
    public static final int ICON_SIZE_SMALL = 16;
    public static final int ICON_SIZE_NORMAL = 24;
    public static final int ICON_SIZE_LARGE = 32;
    
    // Prevent instantiation
    private IconUtils() {}

    /**
     * Loads an icon from the resources directory
     * 
     * @param name The name of the icon file (without extension)
     * @param size The desired size of the icon
     * @return The loaded icon, or null if the icon could not be loaded
     */
    public static ImageIcon loadIcon(String name, int size) {
        try {
            // Try to load from resources directory
            URL resourceUrl = IconUtils.class.getClassLoader().getResource("icons/" + name + ".png");
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                return resizeIcon(icon, size);
            }

            // Fallback to file system
            File iconFile = new File("resources/icons/" + name + ".png");
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                return resizeIcon(icon, size);
            }

            System.err.println("Icon not found: " + name);
            return null;
        } catch (Exception e) {
            System.err.println("Error loading icon: " + name);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resizes an icon to the specified size
     * 
     * @param icon The icon to resize
     * @param size The desired size
     * @return The resized icon
     */
    private static ImageIcon resizeIcon(ImageIcon icon, int size) {
        Image image = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
} 
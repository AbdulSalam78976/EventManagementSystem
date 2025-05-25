package components;

import utils.AppColors;
import utils.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern, reusable statistical card component with customizable styling
 * Features gradient backgrounds, hover effects, and flexible content layout
 */
public class ModernStatCard extends JPanel {
    
    // Card styling options
    public enum CardStyle {
        DEFAULT,        // Standard white background with colored accent
        GRADIENT,       // Gradient background with white text
        OUTLINED,       // Outlined card with transparent background
        ELEVATED        // Card with shadow effect
    }
    
    // Card size presets
    public enum CardSize {
        SMALL(150, 100),
        MEDIUM(200, 120),
        LARGE(250, 140),
        EXTRA_LARGE(300, 160);
        
        private final int width, height;
        
        CardSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public Dimension getDimension() {
            return new Dimension(width, height);
        }
    }
    
    // Component properties
    private String icon;
    private String title;
    private String value;
    private String subtitle;
    private Color primaryColor;
    private Color secondaryColor;
    private CardStyle style;
    private CardSize size;
    private boolean isHovered = false;
    private boolean isClickable = false;
    private Runnable clickAction;
    
    // UI Components
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel subtitleLabel;
    
    /**
     * Creates a basic stat card with default styling
     */
    public ModernStatCard(String icon, String title, String value, Color primaryColor) {
        this(icon, title, value, null, primaryColor, null, CardStyle.DEFAULT, CardSize.MEDIUM);
    }
    
    /**
     * Creates a stat card with custom styling
     */
    public ModernStatCard(String icon, String title, String value, String subtitle, 
                         Color primaryColor, Color secondaryColor, CardStyle style, CardSize size) {
        this.icon = icon;
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.primaryColor = primaryColor != null ? primaryColor : AppColors.PRIMARY;
        this.secondaryColor = secondaryColor != null ? secondaryColor : this.primaryColor.darker();
        this.style = style != null ? style : CardStyle.DEFAULT;
        this.size = size != null ? size : CardSize.MEDIUM;
        
        initializeComponent();
        setupLayout();
        setupStyling();
        setupInteractions();
    }
    
    private void initializeComponent() {
        setOpaque(false);
        setPreferredSize(size.getDimension());
        setMinimumSize(size.getDimension());
        setMaximumSize(size.getDimension());
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    private void setupLayout() {
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Top section with icon and value
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        
        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, getIconSize()));
        iconLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, getValueSize()));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(valueLabel, BorderLayout.EAST);
        
        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, getTitleSize()));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Subtitle (optional)
        if (subtitle != null && !subtitle.trim().isEmpty()) {
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, getSubtitleSize()));
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        
        // Add components to content panel
        contentPanel.add(topSection);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(titleLabel);
        
        if (subtitleLabel != null) {
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(subtitleLabel);
        }
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupStyling() {
        switch (style) {
            case DEFAULT:
                setupDefaultStyling();
                break;
            case GRADIENT:
                setupGradientStyling();
                break;
            case OUTLINED:
                setupOutlinedStyling();
                break;
            case ELEVATED:
                setupElevatedStyling();
                break;
        }
    }
    
    private void setupDefaultStyling() {
        valueLabel.setForeground(primaryColor);
        titleLabel.setForeground(new Color(100, 100, 100));
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(new Color(150, 150, 150));
        }
    }
    
    private void setupGradientStyling() {
        valueLabel.setForeground(Color.WHITE);
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(new Color(255, 255, 255, 150));
        }
        iconLabel.setForeground(Color.WHITE);
    }
    
    private void setupOutlinedStyling() {
        valueLabel.setForeground(primaryColor);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(AppColors.TEXT_SECONDARY);
        }
    }
    
    private void setupElevatedStyling() {
        setupDefaultStyling();
    }
    
    private void setupInteractions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isClickable) {
                    isHovered = true;
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isClickable) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isClickable && clickAction != null) {
                    clickAction.run();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cornerRadius = 15;
        
        switch (style) {
            case DEFAULT:
                paintDefaultBackground(g2d, cornerRadius);
                break;
            case GRADIENT:
                paintGradientBackground(g2d, cornerRadius);
                break;
            case OUTLINED:
                paintOutlinedBackground(g2d, cornerRadius);
                break;
            case ELEVATED:
                paintElevatedBackground(g2d, cornerRadius);
                break;
        }
    }
    
    private void paintDefaultBackground(Graphics2D g2d, int cornerRadius) {
        // Background
        g2d.setColor(isHovered ? new Color(248, 249, 250) : Color.WHITE);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        // Border
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Accent line at top
        g2d.setColor(primaryColor);
        g2d.fillRoundRect(0, 0, getWidth(), 4, cornerRadius, cornerRadius);
    }
    
    private void paintGradientBackground(Graphics2D g2d, int cornerRadius) {
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, primaryColor,
            getWidth(), getHeight(), secondaryColor
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        // Hover overlay
        if (isHovered) {
            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
    
    private void paintOutlinedBackground(Graphics2D g2d, int cornerRadius) {
        // Transparent background with border
        if (isHovered) {
            g2d.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 20));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
        
        g2d.setColor(primaryColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
    }
    
    private void paintElevatedBackground(Graphics2D g2d, int cornerRadius) {
        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 10));
        g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        
        // Background
        g2d.setColor(isHovered ? new Color(248, 249, 250) : Color.WHITE);
        g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        
        // Accent line
        g2d.setColor(primaryColor);
        g2d.fillRoundRect(0, 0, getWidth() - 2, 4, cornerRadius, cornerRadius);
    }
    
    // Font size calculations based on card size
    private int getIconSize() {
        return size == CardSize.SMALL ? 20 : size == CardSize.MEDIUM ? 24 : 28;
    }
    
    private int getValueSize() {
        return size == CardSize.SMALL ? 24 : size == CardSize.MEDIUM ? 32 : 36;
    }
    
    private int getTitleSize() {
        return size == CardSize.SMALL ? 12 : size == CardSize.MEDIUM ? 14 : 16;
    }
    
    private int getSubtitleSize() {
        return size == CardSize.SMALL ? 10 : size == CardSize.MEDIUM ? 12 : 14;
    }
    
    // Public methods for updating card content
    public void updateValue(String newValue) {
        this.value = newValue;
        valueLabel.setText(newValue);
        repaint();
    }
    
    public void updateTitle(String newTitle) {
        this.title = newTitle;
        titleLabel.setText(newTitle);
        repaint();
    }
    
    public void updateIcon(String newIcon) {
        this.icon = newIcon;
        iconLabel.setText(newIcon);
        repaint();
    }
    
    public void updateSubtitle(String newSubtitle) {
        this.subtitle = newSubtitle;
        if (subtitleLabel != null) {
            subtitleLabel.setText(newSubtitle);
        }
        repaint();
    }
    
    public void setClickable(boolean clickable, Runnable action) {
        this.isClickable = clickable;
        this.clickAction = action;
    }
    
    public void updateColors(Color primary, Color secondary) {
        this.primaryColor = primary;
        this.secondaryColor = secondary != null ? secondary : primary.darker();
        setupStyling();
        repaint();
    }
    
    // Getters
    public String getValue() { return value; }
    public String getTitle() { return title; }
    public String getIcon() { return icon; }
    public String getSubtitle() { return subtitle; }
    public Color getPrimaryColor() { return primaryColor; }
    public Color getSecondaryColor() { return secondaryColor; }
    public CardStyle getCardStyle() { return style; }
    public CardSize getCardSize() { return size; }
}

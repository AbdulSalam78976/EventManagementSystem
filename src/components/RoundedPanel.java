package components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A panel with rounded corners
 */
public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private Color borderColor;
    private int cornerRadius;
    private int borderThickness;
    private boolean hasShadow;
    private int shadowSize;
    
    /**
     * Creates a panel with rounded corners
     * 
     * @param layout The layout manager
     * @param backgroundColor The background color
     * @param cornerRadius The corner radius
     */
    public RoundedPanel(LayoutManager layout, Color backgroundColor, int cornerRadius) {
        super(layout);
        this.backgroundColor = backgroundColor;
        this.cornerRadius = cornerRadius;
        this.borderColor = backgroundColor;
        this.borderThickness = 0;
        this.hasShadow = false;
        this.shadowSize = 0;
        
        // Make the panel transparent to show the rounded corners
        setOpaque(false);
    }
    
    /**
     * Creates a panel with rounded corners and default layout
     * 
     * @param backgroundColor The background color
     * @param cornerRadius The corner radius
     */
    public RoundedPanel(Color backgroundColor, int cornerRadius) {
        this(new FlowLayout(), backgroundColor, cornerRadius);
    }
    
    /**
     * Sets the border properties
     * 
     * @param borderColor The border color
     * @param borderThickness The border thickness
     * @return This panel for method chaining
     */
    public RoundedPanel setBorder(Color borderColor, int borderThickness) {
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        return this;
    }
    
    /**
     * Enables a shadow effect
     * 
     * @param shadowSize The size of the shadow
     * @return This panel for method chaining
     */
    public RoundedPanel setShadow(int shadowSize) {
        this.hasShadow = true;
        this.shadowSize = shadowSize;
        
        // Add padding for the shadow
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate the size accounting for the shadow
        int width = getWidth() - (2 * shadowSize);
        int height = getHeight() - (2 * shadowSize);
        int x = shadowSize;
        int y = shadowSize;
        
        // Draw shadow if enabled
        if (hasShadow) {
            for (int i = 0; i < shadowSize; i++) {
                g2.setColor(new Color(0, 0, 0, 20 - i));
                g2.fill(new RoundRectangle2D.Double(x + i, y + i, width, height, cornerRadius, cornerRadius));
            }
        }
        
        // Draw background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(x, y, width, height, cornerRadius, cornerRadius));
        
        // Draw border if thickness > 0
        if (borderThickness > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, cornerRadius, cornerRadius));
        }
        
        g2.dispose();
    }
}

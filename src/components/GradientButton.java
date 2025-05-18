package components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;

/**
 * A button with gradient background and rounded corners
 */
public class GradientButton extends JButton {
    private Color startColor;
    private Color endColor;
    private int cornerRadius;
    private String icon;

    /**
     * Creates a new gradient button
     *
     * @param text The button text
     * @param icon The button icon (emoji or text)
     * @param startColor The start color of the gradient
     * @param endColor The end color of the gradient
     * @param cornerRadius The corner radius
     */
    public GradientButton(String text, String icon, Color startColor, Color endColor, int cornerRadius) {
        super(text);
        this.icon = icon;
        this.startColor = startColor;
        this.endColor = endColor;
        this.cornerRadius = cornerRadius;

        setupButton();
    }

    /**
     * Creates a primary gradient button
     *
     * @param text The button text
     * @return The created button
     */
    public static GradientButton createPrimaryButton(String text) {
        return new GradientButton(
            text,
            null,
            AppColors.PRIMARY,
            AppColors.PRIMARY_DARK,
            UIConstants.CORNER_RADIUS_MEDIUM
        );
    }

    /**
     * Creates a secondary gradient button
     *
     * @param text The button text
     * @return The created button
     */
    public static GradientButton createSecondaryButton(String text) {
        return new GradientButton(
            text,
            null,
            AppColors.SECONDARY,
            AppColors.SECONDARY_DARK,
            UIConstants.CORNER_RADIUS_MEDIUM
        );
    }

    /**
     * Creates an error gradient button
     *
     * @param text The button text
     * @return The created button
     */
    public static GradientButton createErrorButton(String text) {
        return new GradientButton(
            text,
            null,
            AppColors.ERROR,
            AppColors.ERROR,
            UIConstants.CORNER_RADIUS_MEDIUM
        );
    }

    private void setupButton() {
        setFont(UIConstants.BODY_FONT);
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Create gradient paint
        GradientPaint gp = new GradientPaint(
            0, 0, startColor,
            width, 0, endColor
        );

        // Draw background
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        if (icon != null) {
            text = icon + "  " + text;
        }
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);

        g2.dispose();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setForeground(Color.WHITE);
        } else {
            setForeground(new Color(200, 200, 200));
        }
    }
}

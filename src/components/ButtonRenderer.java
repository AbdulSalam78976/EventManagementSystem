package components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;

/**
 * A custom cell renderer for table buttons
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setFont(UIConstants.SMALL_FONT);
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        if (isSelected) {
            setBackground(AppColors.PRIMARY);
        } else {
            setBackground(AppColors.PRIMARY_DARK);
        }
        setText(value.toString());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(
            0, 0, getWidth(), getHeight(),
            UIConstants.CORNER_RADIUS_SMALL,
            UIConstants.CORNER_RADIUS_SMALL
        ));

        // Draw text
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);

        g2.dispose();
    }
}

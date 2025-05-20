package components;

import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class StatCard extends RoundedPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel iconLabel;

    public StatCard(String title, String value, String icon, Color valueColor) {
        super(new BorderLayout(0, 5), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));

        // Create title label
        titleLabel = UIUtils.createLabel(
            title,
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create value label
        valueLabel = UIUtils.createLabel(
            value,
            UIConstants.DASHBOARD_NUMBER_FONT,
            valueColor
        );
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create icon label if provided
        if (icon != null && !icon.isEmpty()) {
            iconLabel = UIUtils.createLabel(
                icon,
                UIConstants.DASHBOARD_NUMBER_FONT,
                valueColor
            );
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, BorderLayout.WEST);
        } else {
            iconLabel = null;
        }

        // Layout components
        JPanel contentPanel = new JPanel(new BorderLayout(0, 5));
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    public void updateValue(String value) {
        valueLabel.setText(value);
    }

    public void updateTitle(String title) {
        titleLabel.setText(title);
    }
} 
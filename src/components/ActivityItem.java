package components;

import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class ActivityItem extends RoundedPanel {
    private final JLabel actionLabel;
    private final JLabel detailsLabel;
    private final JLabel timeLabel;

    public ActivityItem(String action, String details, String time) {
        super(new BorderLayout(10, 0), AppColors.BACKGROUND_LIGHT, UIConstants.CORNER_RADIUS_SMALL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Action label
        actionLabel = UIUtils.createLabel(
            action,
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        add(actionLabel, BorderLayout.NORTH);
        
        // Details label
        detailsLabel = UIUtils.createLabel(
            details,
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        add(detailsLabel, BorderLayout.CENTER);
        
        // Time label
        timeLabel = UIUtils.createLabel(
            time,
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(timeLabel, BorderLayout.SOUTH);
    }

    public void updateAction(String action) {
        actionLabel.setText(action);
    }

    public void updateDetails(String details) {
        detailsLabel.setText(details);
    }

    public void updateTime(String time) {
        timeLabel.setText(time);
    }
} 
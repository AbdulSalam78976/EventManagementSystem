package components;

import javax.swing.*;
import java.awt.*;
import models.User;
import utils.UIUtils;
import utils.UIConstants;
import utils.AppColors;

public class SettingsPanel extends JPanel {
    public SettingsPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel("Settings", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Example: Allow user to update phone number
        formPanel.add(UIUtils.createLabel("Phone:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(user.getPhone() != null ? user.getPhone() : "");
        formPanel.add(phoneField, gbc);

        // Add more settings fields as needed...

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton saveButton = UIUtils.createButton("Save Settings", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        formPanel.add(saveButton, gbc);

        // Add action listener for saving settings
        // saveButton.addActionListener(...);

        add(formPanel, BorderLayout.CENTER);
    }
} 
package components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;

/**
 * A dialog for approving or rejecting event registrations
 */
public class EventApprovalDialog extends JDialog {
    private final String eventName;
    private final String participantName;
    private final String participantEmail;
    private final Consumer<Boolean> onApprovalComplete;
    private JTextArea commentsArea;
    private JLabel errorLabel;
    private boolean approved = false;

    /**
     * Creates a new event approval dialog
     *
     * @param parent The parent window
     * @param eventName The name of the event
     * @param participantName The name of the participant
     * @param participantEmail The email of the participant
     * @param onApprovalComplete Callback for when approval is complete
     */
    public EventApprovalDialog(
            Window parent,
            String eventName,
            String participantName,
            String participantEmail,
            Consumer<Boolean> onApprovalComplete) {
        
        super(parent, "Review Registration", ModalityType.APPLICATION_MODAL);
        
        this.eventName = eventName;
        this.participantName = participantName;
        this.participantEmail = participantEmail;
        this.onApprovalComplete = onApprovalComplete;
        
        setupUI();
        
        // Set dialog properties
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create content panel
        JPanel contentPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add header
        JLabel headerLabel = UIUtils.createLabel(
            "Review Registration",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(headerLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add participant info
        JPanel infoPanel = createInfoPanel();
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add comments field
        JLabel commentsLabel = UIUtils.createLabel(
            "Comments (Optional):",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        commentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(commentsLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        commentsArea = new JTextArea(4, 30);
        commentsArea.setFont(UIConstants.BODY_FONT);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane commentsScroll = new JScrollPane(commentsArea);
        commentsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentPanel.add(commentsScroll);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add error label
        errorLabel = UIUtils.createLabel(
            " ",
            UIConstants.SMALL_FONT,
            AppColors.ERROR
        );
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add buttons
        contentPanel.add(createButtonPanel());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Event name
        JLabel eventLabel = UIUtils.createLabel(
            "Event: " + eventName,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(eventLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Participant name
        JLabel nameLabel = UIUtils.createLabel(
            "Participant: " + participantName,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Participant email
        JLabel emailLabel = UIUtils.createLabel(
            "Email: " + participantEmail,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(emailLabel);

        return infoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT), false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton rejectButton = UIUtils.createButton(
            "Reject",
            null,
            UIUtils.ButtonType.ERROR,
            UIUtils.ButtonSize.NORMAL
        );
        rejectButton.addActionListener(e -> handleRejection());

        JButton approveButton = UIUtils.createButton(
            "Approve",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        approveButton.addActionListener(e -> handleApproval());

        buttonPanel.add(rejectButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(approveButton);

        return buttonPanel;
    }

    private void handleApproval() {
        // Disable form while processing
        commentsArea.setEnabled(false);
        
        try {
            if (onApprovalComplete != null) {
                onApprovalComplete.accept(true);
            }
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Registration has been approved.",
                "Approval Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            approved = true;
            dispose();
        } catch (Exception e) {
            // Show error message
            JOptionPane.showMessageDialog(this,
                "Error during approval: " + e.getMessage(),
                "Approval Failed",
                JOptionPane.ERROR_MESSAGE);
            
            // Re-enable form
            commentsArea.setEnabled(true);
        }
    }

    private void handleRejection() {
        String comments = commentsArea.getText().trim();
        
        // Validate comments
        if (comments.isEmpty()) {
            errorLabel.setText("Please provide a reason for rejection.");
            return;
        }
        
        // Validate comments length
        if (comments.length() < 10) {
            errorLabel.setText("Please provide a more detailed reason for rejection (minimum 10 characters).");
            return;
        }
        
        // Confirm rejection
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject this registration?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Disable form while processing
        commentsArea.setEnabled(false);
        
        try {
            if (onApprovalComplete != null) {
                onApprovalComplete.accept(false);
            }
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Registration has been rejected.",
                "Rejection Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            approved = false;
            dispose();
        } catch (Exception e) {
            // Show error message
            JOptionPane.showMessageDialog(this,
                "Error during rejection: " + e.getMessage(),
                "Rejection Failed",
                JOptionPane.ERROR_MESSAGE);
            
            // Re-enable form
            commentsArea.setEnabled(true);
        }
    }

    /**
     * Shows the dialog and returns whether the registration was approved
     *
     * @return true if the registration was approved, false if rejected
     */
    public boolean showDialog() {
        setVisible(true);
        return approved;
    }

    /**
     * Gets the approval comments
     *
     * @return The comments entered by the approver
     */
    public String getComments() {
        return commentsArea.getText().trim();
    }
}

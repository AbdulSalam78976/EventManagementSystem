package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.AppColors;
import utils.EmojiUtils;
import utils.UIUtils;

/**
 * A panel for uploading and managing event media
 */
public class MediaUploadPanel extends JPanel {
    private JComboBox<String> eventSelector;
    private JPanel mediaGallery;
    private JPanel uploadedFilesPanel;
    private ArrayList<File> selectedFiles = new ArrayList<>();

    /**
     * Creates a new media upload panel
     */
    public MediaUploadPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Media Upload & Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);

        // Add event selector and upload panel
        contentPanel.add(createTopPanel(), BorderLayout.NORTH);

        // Add media gallery
        RoundedPanel galleryContainer = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        galleryContainer.setShadow(3);
        galleryContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel galleryTitle = new JLabel("Media Gallery");
        galleryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        galleryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        mediaGallery = createMediaGallery();
        JScrollPane galleryScroll = new JScrollPane(mediaGallery);
        galleryScroll.setBorder(null);
        galleryScroll.getVerticalScrollBar().setUnitIncrement(16);

        galleryContainer.add(galleryTitle, BorderLayout.NORTH);
        galleryContainer.add(galleryScroll, BorderLayout.CENTER);

        contentPanel.add(galleryContainer, BorderLayout.CENTER);

        // Add uploaded files panel
        RoundedPanel uploadedContainer = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        uploadedContainer.setShadow(3);
        uploadedContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel uploadedTitle = new JLabel("Uploaded Files");
        uploadedTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        uploadedTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        uploadedFilesPanel = new JPanel();
        uploadedFilesPanel.setLayout(new BoxLayout(uploadedFilesPanel, BoxLayout.Y_AXIS));
        uploadedFilesPanel.setOpaque(false);

        JLabel noFilesLabel = new JLabel("No files uploaded yet");
        noFilesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        noFilesLabel.setForeground(AppColors.TEXT_SECONDARY);
        noFilesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadedFilesPanel.add(noFilesLabel);

        JScrollPane uploadedScroll = new JScrollPane(uploadedFilesPanel);
        uploadedScroll.setBorder(null);
        uploadedScroll.setPreferredSize(new Dimension(0, 150));

        uploadedContainer.add(uploadedTitle, BorderLayout.NORTH);
        uploadedContainer.add(uploadedScroll, BorderLayout.CENTER);

        contentPanel.add(uploadedContainer, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);

        // Event selector panel
        RoundedPanel selectorPanel = new RoundedPanel(new FlowLayout(FlowLayout.LEFT, 15, 10), Color.WHITE, UIUtils.CORNER_RADIUS);
        selectorPanel.setShadow(3);
        selectorPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel eventLabel = new JLabel("Select Event:");
        eventLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String[] events = {
            "Tech Conference 2023",
            "Charity Gala",
            "Workshop Series",
            "Product Launch"
        };
        eventSelector = new JComboBox<>(events);
        eventSelector.setPreferredSize(new Dimension(200, 35));
        eventSelector.addActionListener(e -> loadEventMedia());

        selectorPanel.add(eventLabel);
        selectorPanel.add(eventSelector);

        // Upload panel
        RoundedPanel uploadPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER, 15, 10), Color.WHITE, UIUtils.CORNER_RADIUS);
        uploadPanel.setShadow(3);
        uploadPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        GradientButton uploadButton = GradientButton.createPrimaryButton("Upload New Media");
        uploadButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        uploadButton.setPreferredSize(new Dimension(180, 40));
        uploadButton.addActionListener(e -> uploadMedia());

        uploadPanel.add(uploadButton);

        panel.add(selectorPanel, BorderLayout.WEST);
        panel.add(uploadPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMediaGallery() {
        JPanel gallery = new JPanel(new GridLayout(0, 4, 10, 10));
        gallery.setOpaque(false);
        gallery.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add sample media items
        for (int i = 1; i <= 8; i++) {
            gallery.add(createMediaItem("Sample Image " + i));
        }

        return gallery;
    }

    private JPanel createMediaItem(String title) {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        panel.setBorder(BorderFactory.createLineBorder(AppColors.BORDER_LIGHT, 1));

        // Create a placeholder for the image with emoji
        JPanel imagePlaceholder = new JPanel(new BorderLayout());
        imagePlaceholder.setBackground(new Color(248, 249, 250));
        imagePlaceholder.setPreferredSize(new Dimension(150, 150));

        // Add emoji placeholder
        JLabel emojiPlaceholder = new JLabel("🖼️");
        emojiPlaceholder.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 48));
        emojiPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        emojiPlaceholder.setVerticalAlignment(SwingConstants.CENTER);
        emojiPlaceholder.setForeground(new Color(150, 150, 150));

        imagePlaceholder.add(emojiPlaceholder, BorderLayout.CENTER);

        // Add mouse listener for hover effect
        imagePlaceholder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(AppColors.BORDER_LIGHT, 1));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showMediaActions(title);
            }
        });

        // Add title label
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(imagePlaceholder, BorderLayout.CENTER);
        panel.add(titleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadEventMedia() {
        String selectedEvent = (String) eventSelector.getSelectedItem();

        // In a real application, this would load media for the selected event
        // For now, we'll just show a message
        JOptionPane.showMessageDialog(this,
            "Loading media for event: " + selectedEvent,
            "Load Event Media",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void uploadMedia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();

            // Add files to the list
            for (File file : files) {
                selectedFiles.add(file);
                addFileToUploadedPanel(file);
            }

            // Show success message
            JOptionPane.showMessageDialog(this,
                files.length + " file(s) uploaded successfully!",
                "Upload Successful",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addFileToUploadedPanel(File file) {
        // Remove "No files uploaded yet" label if it exists
        if (uploadedFilesPanel.getComponentCount() == 1 &&
            uploadedFilesPanel.getComponent(0) instanceof JLabel) {
            uploadedFilesPanel.removeAll();
        }

        // Create file item panel
        JPanel filePanel = new JPanel(new BorderLayout(10, 0));
        filePanel.setOpaque(false);
        filePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // File icon and name with appropriate emoji
        String fileEmoji = EmojiUtils.getFileTypeEmoji(file.getName());
        JLabel fileIcon = new JLabel(fileEmoji);
        fileIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel fileName = new JLabel(file.getName());
        fileName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Delete button
        JButton deleteButton = new JButton("❌");
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            uploadedFilesPanel.remove(filePanel);
            selectedFiles.remove(file);

            // Add "No files uploaded yet" label if no files remain
            if (uploadedFilesPanel.getComponentCount() == 0) {
                JLabel noFilesLabel = new JLabel("No files uploaded yet");
                noFilesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                noFilesLabel.setForeground(AppColors.TEXT_SECONDARY);
                noFilesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                uploadedFilesPanel.add(noFilesLabel);
            }

            uploadedFilesPanel.revalidate();
            uploadedFilesPanel.repaint();
        });

        JPanel fileInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        fileInfoPanel.setOpaque(false);
        fileInfoPanel.add(fileIcon);
        fileInfoPanel.add(fileName);

        filePanel.add(fileInfoPanel, BorderLayout.WEST);
        filePanel.add(deleteButton, BorderLayout.EAST);

        uploadedFilesPanel.add(filePanel);
        uploadedFilesPanel.revalidate();
        uploadedFilesPanel.repaint();
    }

    private void showMediaActions(String mediaTitle) {
        // Create a popup menu with actions
        JPopupMenu actionsMenu = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Full Size");
        viewItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Viewing full size image: " + mediaTitle,
                "View Image",
                JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem downloadItem = new JMenuItem("Download");
        downloadItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Downloading image: " + mediaTitle,
                "Download Image",
                JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + mediaTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Image deleted: " + mediaTitle,
                    "Delete Image",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        actionsMenu.add(viewItem);
        actionsMenu.add(downloadItem);
        actionsMenu.add(deleteItem);

        // Show the popup menu
        actionsMenu.show(this, MouseInfo.getPointerInfo().getLocation().x,
                         MouseInfo.getPointerInfo().getLocation().y);
    }
}

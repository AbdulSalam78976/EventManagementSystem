package components;

import utils.AppColors;
import javax.swing.*;
import java.awt.*;

/**
 * Demo class showcasing different ModernStatCard styles and configurations
 * This demonstrates the flexibility and reusability of the ModernStatCard component
 */
public class ModernStatCardDemo extends JFrame {
    
    public ModernStatCardDemo() {
        setTitle("Modern Stat Card Demo - All Styles & Sizes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("ModernStatCard Component Demo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Create demo sections
        JPanel demoPanel = new JPanel();
        demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
        demoPanel.setOpaque(false);
        
        // Different styles demo
        demoPanel.add(createStylesDemo());
        demoPanel.add(Box.createVerticalStrut(40));
        
        // Different sizes demo
        demoPanel.add(createSizesDemo());
        demoPanel.add(Box.createVerticalStrut(40));
        
        // Interactive features demo
        demoPanel.add(createInteractiveDemo());
        
        JScrollPane scrollPane = new JScrollPane(demoPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createStylesDemo() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        JLabel sectionTitle = new JLabel("🎨 Different Card Styles");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        
        // DEFAULT style
        ModernStatCard defaultCard = new ModernStatCard(
            "📊", "Default Style", "1,234", "Clean white background",
            new Color(74, 144, 226), null, 
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM
        );
        
        // GRADIENT style
        ModernStatCard gradientCard = new ModernStatCard(
            "🌈", "Gradient Style", "5,678", "Beautiful gradients",
            new Color(52, 168, 83), new Color(34, 139, 34),
            ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.MEDIUM
        );
        
        // OUTLINED style
        ModernStatCard outlinedCard = new ModernStatCard(
            "🔲", "Outlined Style", "9,012", "Minimalist borders",
            new Color(251, 188, 5), null,
            ModernStatCard.CardStyle.OUTLINED, ModernStatCard.CardSize.MEDIUM
        );
        
        // ELEVATED style
        ModernStatCard elevatedCard = new ModernStatCard(
            "⬆️", "Elevated Style", "3,456", "Subtle shadow effects",
            new Color(234, 67, 53), null,
            ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM
        );
        
        cardsPanel.add(defaultCard);
        cardsPanel.add(gradientCard);
        cardsPanel.add(outlinedCard);
        cardsPanel.add(elevatedCard);
        
        section.add(cardsPanel, BorderLayout.CENTER);
        return section;
    }
    
    private JPanel createSizesDemo() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        JLabel sectionTitle = new JLabel("📏 Different Card Sizes");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        cardsPanel.setOpaque(false);
        
        // SMALL size
        ModernStatCard smallCard = new ModernStatCard(
            "🔸", "Small", "42", "Compact size",
            new Color(156, 39, 176), null,
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.SMALL
        );
        
        // MEDIUM size
        ModernStatCard mediumCard = new ModernStatCard(
            "🔹", "Medium", "123", "Standard size",
            new Color(3, 169, 244), null,
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM
        );
        
        // LARGE size
        ModernStatCard largeCard = new ModernStatCard(
            "🔷", "Large", "456", "Spacious layout",
            new Color(76, 175, 80), null,
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.LARGE
        );
        
        // EXTRA_LARGE size
        ModernStatCard extraLargeCard = new ModernStatCard(
            "🔶", "Extra Large", "789", "Maximum visibility",
            new Color(255, 152, 0), null,
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.EXTRA_LARGE
        );
        
        cardsPanel.add(smallCard);
        cardsPanel.add(mediumCard);
        cardsPanel.add(largeCard);
        cardsPanel.add(extraLargeCard);
        
        section.add(cardsPanel, BorderLayout.CENTER);
        return section;
    }
    
    private JPanel createInteractiveDemo() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        JLabel sectionTitle = new JLabel("🖱️ Interactive Features");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        section.add(sectionTitle, BorderLayout.NORTH);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        
        // Clickable card with action
        ModernStatCard clickableCard = new ModernStatCard(
            "👆", "Clickable", "Click Me!", "Interactive card",
            new Color(103, 58, 183), new Color(63, 81, 181),
            ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.MEDIUM
        );
        clickableCard.setClickable(true, () -> {
            JOptionPane.showMessageDialog(this, "Card clicked! 🎉", "Interactive Demo", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Updatable card
        ModernStatCard updatableCard = new ModernStatCard(
            "🔄", "Dynamic", "0", "Updates in real-time",
            new Color(0, 150, 136), null,
            ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM
        );
        
        // Timer to update the card value
        Timer timer = new Timer(1000, e -> {
            int currentValue = Integer.parseInt(updatableCard.getValue());
            updatableCard.updateValue(String.valueOf(currentValue + 1));
        });
        timer.start();
        
        // Color-changing card
        ModernStatCard colorCard = new ModernStatCard(
            "🎨", "Color Change", "Rainbow", "Dynamic colors",
            new Color(233, 30, 99), null,
            ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM
        );
        
        // Timer to change colors
        Color[] colors = {
            new Color(233, 30, 99),   // Pink
            new Color(156, 39, 176),  // Purple
            new Color(103, 58, 183),  // Deep Purple
            new Color(63, 81, 181),   // Indigo
            new Color(33, 150, 243),  // Blue
            new Color(3, 169, 244)    // Light Blue
        };
        
        Timer colorTimer = new Timer(2000, new java.awt.event.ActionListener() {
            private int colorIndex = 0;
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                colorCard.updateColors(colors[colorIndex], null);
                colorIndex = (colorIndex + 1) % colors.length;
            }
        });
        colorTimer.start();
        
        cardsPanel.add(clickableCard);
        cardsPanel.add(updatableCard);
        cardsPanel.add(colorCard);
        
        section.add(cardsPanel, BorderLayout.CENTER);
        
        // Add description
        JLabel descLabel = new JLabel("<html><center>Left: Click to see interaction • Center: Auto-updating counter • Right: Color-changing animation</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        section.add(descLabel, BorderLayout.SOUTH);
        
        return section;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ModernStatCardDemo();
        });
    }
}

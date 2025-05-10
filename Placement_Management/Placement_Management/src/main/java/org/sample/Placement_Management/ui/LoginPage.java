package org.sample.Placement_Management.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    private JButton studentButton, professorButton, tpoButton;

    public LoginPage() {
        // Set Nimbus Look and Feel for a modern UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, the default look and feel will be used.
        }

        setTitle("Login Page");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create header label
        JLabel headerLabel = new JLabel("Welcome to Placement Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Create a panel for the buttons using BoxLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Let the gradient background show through
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Create buttons with modern styling
        studentButton = createModernButton("Student");
        professorButton = createModernButton("Professor");
        tpoButton = createModernButton("Training and Placement Office");

        // Add action listeners (logic remains unchanged)
        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentLoginPage();
                dispose();
            }
        });

        professorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProfessorLoginPage();
                dispose();
            }
        });

        tpoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TPOLoginPage();
                dispose();
            }
        });

        // Align buttons and add spacing
        studentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        professorButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tpoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(studentButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(professorButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(tpoButton);

        // Create a background panel with a gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                // Define gradient colors
                Color color1 = new Color(66, 133, 244);
                Color color2 = new Color(219, 68, 55);
                g2d.setPaint(new GradientPaint(0, 0, color1, width, height, color2));
                g2d.fillRect(0, 0, width, height);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(headerLabel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    // Utility method to create a button with modern styling
    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 255, 255, 200));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}

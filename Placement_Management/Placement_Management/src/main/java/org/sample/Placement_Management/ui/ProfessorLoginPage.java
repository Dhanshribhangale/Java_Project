package org.sample.Placement_Management.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.sample.Placement_Management.dao.MySQLConnection;

public class ProfessorLoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public ProfessorLoginPage() {
        // Set Nimbus Look and Feel for a modern UI appearance.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        setTitle("Professor Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a main panel with a BorderLayout and padding.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Add a title label at the top.
        JLabel titleLabel = new JLabel("Professor Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create a form panel using GridBagLayout.
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Email Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);

        // Email Field
        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel for the login button.
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener for Login: Verify credentials against the users table.
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // Exception handling: Check for empty fields.
                if(email.isEmpty() || password.isEmpty()){
                    JOptionPane.showMessageDialog(ProfessorLoginPage.this, "Enter email and password!");
                    return;
                }
                
                // Exception handling: Validate email format.
                if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
                    JOptionPane.showMessageDialog(ProfessorLoginPage.this, "Please write the email in proper format");
                    return;
                }

                try (Connection conn = MySQLConnection.getConnection()) {
                    String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = 'PROFESSOR'";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(ProfessorLoginPage.this,
                                "Login successful as Professor with email: " + email);
                        new ProfessorDashboard();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ProfessorLoginPage.this,
                                "Invalid credentials!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(ProfessorLoginPage.this,
                            "Database error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProfessorLoginPage::new);
    }
}

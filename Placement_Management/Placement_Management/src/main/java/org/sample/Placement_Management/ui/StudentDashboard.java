package org.sample.Placement_Management.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.sample.Placement_Management.dao.MySQLConnection;

public class StudentDashboard extends JFrame {
    private JTabbedPane tabbedPane;

    // Table models for each tab
    private DefaultTableModel lectureTableModel;
    private DefaultTableModel bookingTableModel;
    private DefaultTableModel applicationTableModel;
    private DefaultTableModel companyTableModel;

    // Tables
    private JTable lectureTable;
    private JTable bookingTable;
    private JTable applicationTable;
    private JTable companyTable;

    public StudentDashboard() {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        // Global font customization
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TabbedPane.font", defaultFont);

        setTitle("Student Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(defaultFont);

        // ---------------- Tab 1: Lectures ----------------
        JPanel lecturePanel = new JPanel(new BorderLayout());
        lecturePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        lectureTableModel = new DefaultTableModel(new Object[]{"Day", "Teacher Name", "Classroom", "Time Slot"}, 0);
        lectureTable = new JTable(lectureTableModel);
        lectureTable.setFillsViewportHeight(true);
        lectureTable.setRowHeight(25);
        JScrollPane lectureScrollPane = new JScrollPane(lectureTable);
        lecturePanel.add(lectureScrollPane, BorderLayout.CENTER);

        JPanel lectureControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshLecturesButton = new JButton("Refresh Lectures");
        refreshLecturesButton.addActionListener(e -> loadLectures());
        lectureControlPanel.add(refreshLecturesButton);
        lecturePanel.add(lectureControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Lectures", lecturePanel);

        // ---------------- Tab 2: Extra Activities ----------------
        JPanel bookingPanel = new JPanel(new BorderLayout());
        bookingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bookingTableModel = new DefaultTableModel(new Object[]{"Classroom", "Purpose", "Time Slot"}, 0);
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.setRowHeight(25);
        JScrollPane bookingScrollPane = new JScrollPane(bookingTable);
        bookingPanel.add(bookingScrollPane, BorderLayout.CENTER);

        JPanel bookingControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBookingsButton = new JButton("Refresh Activities");
        refreshBookingsButton.addActionListener(e -> loadExtraActivities());
        bookingControlPanel.add(refreshBookingsButton);
        bookingPanel.add(bookingControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Extra Activities", bookingPanel);

        // ---------------- Tab 3: Application Status ----------------
        JPanel applicationPanel = new JPanel(new BorderLayout());
        applicationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        applicationTableModel = new DefaultTableModel(new Object[]{"Company", "Status"}, 0);
        applicationTable = new JTable(applicationTableModel);
        applicationTable.setFillsViewportHeight(true);
        applicationTable.setRowHeight(25);
        JScrollPane applicationScrollPane = new JScrollPane(applicationTable);
        applicationPanel.add(applicationScrollPane, BorderLayout.CENTER);

        JPanel appControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        appControlPanel.add(new JLabel("Student ID:"));
        JTextField studentIdField = new JTextField(10);
        appControlPanel.add(studentIdField);
        JButton loadApplicationsButton = new JButton("Load Applications");
        loadApplicationsButton.addActionListener(e -> {
            String studentIdText = studentIdField.getText();
            if (studentIdText == null || studentIdText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
                return;
            }
            loadApplications(studentIdText);
        });
        appControlPanel.add(loadApplicationsButton);
        applicationPanel.add(appControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Application Status", applicationPanel);

        // ---------------- Tab 4: Companies & Apply ----------------
        JPanel companyPanel = new JPanel(new BorderLayout());
        companyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        companyTableModel = new DefaultTableModel(new Object[]{"Company", "Min CGPA", "Visit Date"}, 0);
        companyTable = new JTable(companyTableModel);
        companyTable.setFillsViewportHeight(true);
        companyTable.setRowHeight(25);
        JScrollPane companyScrollPane = new JScrollPane(companyTable);
        companyPanel.add(companyScrollPane, BorderLayout.CENTER);

        JPanel companyControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadCompaniesButton = new JButton("Load Companies");
        loadCompaniesButton.addActionListener(e -> loadCompanies());
        companyControlPanel.add(loadCompaniesButton);
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> applyForCompany());
        companyControlPanel.add(applyButton);
        companyPanel.add(companyControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Companies", companyPanel);

        add(tabbedPane);
        setVisible(true);
    }

    // Load lectures from schedule table: join schedule, users (for teacher's name), and classrooms.
    private void loadLectures() {
        lectureTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT s.day, u.name as teacher_name, cl.room_number, " +
                         "CONCAT(s.start_time, ' - ', s.end_time) as time_slot " +
                         "FROM schedule s " +
                         "JOIN users u ON s.professor_id = u.user_id " +
                         "JOIN classrooms cl ON s.classroom_id = cl.classroom_id " +
                         "ORDER BY FIELD(s.day, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'), s.start_time ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String day = rs.getString("day");
                String teacherName = rs.getString("teacher_name");
                String roomNumber = rs.getString("room_number");
                String timeSlot = rs.getString("time_slot");
                lectureTableModel.addRow(new Object[]{day, teacherName, roomNumber, timeSlot});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading lectures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load extra activities from bookings table: join with classrooms to get room number.
    private void loadExtraActivities() {
        bookingTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT cl.room_number, b.purpose, CONCAT(b.start_time, ' - ', b.end_time) as time_slot " +
                         "FROM bookings b " +
                         "JOIN classrooms cl ON b.classroom_id = cl.classroom_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String purpose = rs.getString("purpose");
                String timeSlot = rs.getString("time_slot");
                bookingTableModel.addRow(new Object[]{roomNumber, purpose, timeSlot});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading extra activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load application status for a student from applications table (join companies).
    private void loadApplications(String studentId) {
        applicationTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT comp.company_name, app.status " +
                         "FROM applications app " +
                         "JOIN companies comp ON app.company_id = comp.company_id " +
                         "WHERE app.student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(studentId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String companyName = rs.getString("company_name");
                String status = rs.getString("status");
                applicationTableModel.addRow(new Object[]{companyName, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load list of companies from companies table.
    private void loadCompanies() {
        companyTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT company_name, min_cgpa, visit_date FROM companies";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String companyName = rs.getString("company_name");
                double minCgpa = rs.getDouble("min_cgpa");
                Date visitDate = rs.getDate("visit_date");
                companyTableModel.addRow(new Object[]{companyName, minCgpa, visitDate});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading companies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Apply for a company: check student CGPA, update application status if eligible.
    private void applyForCompany() {
        String studentIdStr = JOptionPane.showInputDialog(this, "Enter your Student ID:");
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Student ID.");
            return;
        }
        int studentId = Integer.parseInt(studentIdStr.trim());

        String companyName = JOptionPane.showInputDialog(this, "Enter the Company Name you want to apply for:");
        if (companyName == null || companyName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the Company Name.");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sqlCompany = "SELECT company_id, min_cgpa FROM companies WHERE company_name = ?";
            PreparedStatement stmtCompany = conn.prepareStatement(sqlCompany);
            stmtCompany.setString(1, companyName);
            ResultSet rsCompany = stmtCompany.executeQuery();
            if (rsCompany.next()) {
                String companyId = rsCompany.getString("company_id");
                double minCgpa = rsCompany.getDouble("min_cgpa");

                String sqlStudent = "SELECT cgpa FROM student_details WHERE student_id = ?";
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setInt(1, studentId);
                ResultSet rsStudent = stmtStudent.executeQuery();
                if (rsStudent.next()) {
                    double studentCgpa = rsStudent.getDouble("cgpa");
                    if (studentCgpa >= minCgpa) {
                        String sqlApp = "INSERT INTO applications (student_id, company_id, status) VALUES (?, ?, 'Applied') " +
                                        "ON DUPLICATE KEY UPDATE status = 'Applied'";
                        PreparedStatement stmtApp = conn.prepareStatement(sqlApp);
                        stmtApp.setInt(1, studentId);
                        stmtApp.setString(2, companyId);
                        int rows = stmtApp.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(this, "Application successful!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Application failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Your CGPA does not meet the minimum requirement for " + companyName);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Student details not found.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Company not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error applying for company: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentDashboard::new);
    }
}

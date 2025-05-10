package org.sample.Placement_Management.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.sample.Placement_Management.dao.MySQLConnection;

public class TPODashboard extends JFrame {

    private JTabbedPane tabbedPane;

    private DefaultTableModel appTableModel;
    private JTable appTable;

    private DefaultTableModel companyTableModel;
    private JTable companyTable;

    private DefaultTableModel availClassTableModel;
    private JTable availClassTable;

    private DefaultTableModel extraActivityTableModel;
    private JTable extraActivityTable;

    private JComboBox<String> dayComboBox;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;

    public TPODashboard() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        setTitle("TPO Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // ---------------- Tab 1: Applications ----------------
        JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        appTableModel = new DefaultTableModel(
                new Object[]{"Student ID", "Student Name", "Company ID", "Company Name", "Status"}, 0);
        appTable = new JTable(appTableModel);
        appPanel.add(new JScrollPane(appTable), BorderLayout.CENTER);

        JPanel appControlPanel = new JPanel(new FlowLayout());
        JTextField studentIdField = new JTextField(10);
        appControlPanel.add(new JLabel("Student ID:"));
        appControlPanel.add(studentIdField);

        JButton loadAppButton = new JButton("Load Applications");
        loadAppButton.addActionListener(e -> loadApplications(studentIdField.getText()));
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(e -> updateApplicationStatus());

        appControlPanel.add(loadAppButton);
        appControlPanel.add(updateStatusButton);

        appPanel.add(appControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Applications", appPanel);

        // ---------------- Tab 2: Companies ----------------
        JPanel compPanel = new JPanel(new BorderLayout());
        compPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        companyTableModel = new DefaultTableModel(new Object[]{"Company ID", "Company", "Min CGPA", "Visit Date"}, 0);
        companyTable = new JTable(companyTableModel);
        compPanel.add(new JScrollPane(companyTable), BorderLayout.CENTER);

        JPanel compControlPanel = new JPanel(new FlowLayout());
        JButton loadCompButton = new JButton("Load Companies");
        JButton deleteCompButton = new JButton("Delete Company");
        JButton addCompButton = new JButton("Add Company");

        loadCompButton.addActionListener(e -> loadCompanies());
        deleteCompButton.addActionListener(e -> deleteCompany());
        addCompButton.addActionListener(e -> addCompany());

        compControlPanel.add(loadCompButton);
        compControlPanel.add(deleteCompButton);
        compControlPanel.add(addCompButton);
        compPanel.add(compControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Companies", compPanel);

        // ---------------- Tab 3: Classroom Booking ----------------
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel bookingInputPanel = new JPanel();
        bookingInputPanel.setLayout(new BoxLayout(bookingInputPanel, BoxLayout.Y_AXIS));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(new JLabel("Select Day:"));
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        dayComboBox = new JComboBox<>(days);
        timePanel.add(dayComboBox);

        timePanel.add(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm:ss");
        startTimeSpinner.setEditor(startEditor);
        timePanel.add(startTimeSpinner);

        timePanel.add(new JLabel("End Time:"));
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm:ss");
        endTimeSpinner.setEditor(endEditor);
        timePanel.add(endTimeSpinner);

        JTextField companyIdField = new JTextField(10);
        timePanel.add(new JLabel("Company ID:"));
        timePanel.add(companyIdField);

        JButton showRoomsButton = new JButton("Show Available Rooms");
        showRoomsButton.addActionListener(e -> {
            String day = (String) dayComboBox.getSelectedItem();
            String start = new SimpleDateFormat("HH:mm:ss").format(startTimeSpinner.getValue());
            String end = new SimpleDateFormat("HH:mm:ss").format(endTimeSpinner.getValue());
            loadAvailableClassrooms(day, start, end);
        });

        timePanel.add(showRoomsButton);
        bookingInputPanel.add(timePanel);
        bookPanel.add(bookingInputPanel, BorderLayout.NORTH);

        availClassTableModel = new DefaultTableModel(new Object[]{"Room Number"}, 0);
        availClassTable = new JTable(availClassTableModel);
        JScrollPane scrollPane = new JScrollPane(availClassTable);
        bookPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bookControlPanel = new JPanel();
        JButton bookRoomButton = new JButton("Book Selected Room");
        bookRoomButton.addActionListener(e -> {
            String compInput = companyIdField.getText().trim();
            if (!compInput.isEmpty()) {
                bookSelectedRoom(compInput);
            } else {
                JOptionPane.showMessageDialog(this, "Enter Company ID before booking.");
            }
        });
        bookControlPanel.add(bookRoomButton);
        bookPanel.add(bookControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Classroom Booking", bookPanel);

        // ---------------- Tab 4: Extra Activities ----------------
        JPanel extraPanel = new JPanel(new BorderLayout());
        extraPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        extraActivityTableModel = new DefaultTableModel(
                new Object[]{"Booking ID", "User ID", "Room Number", "Day", "Start Time", "End Time", "Purpose"}, 0);
        extraActivityTable = new JTable(extraActivityTableModel);
        extraPanel.add(new JScrollPane(extraActivityTable), BorderLayout.CENTER);
        JButton refreshButton = new JButton("Refresh Extra Activities");
        refreshButton.addActionListener(e -> loadExtraActivities());
        extraPanel.add(refreshButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Extra Activities", extraPanel);

        add(tabbedPane);
        setVisible(true);
    }

    // ---------------- Tab 1: Applications ----------------
    private void loadApplications(String studentId) {
        appTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql;
            PreparedStatement stmt;
            if (studentId == null || studentId.trim().isEmpty()) {
                sql = "SELECT app.student_id, u.name AS student_name, app.company_id, comp.company_name, app.status " +
                      "FROM applications app " +
                      "JOIN users u ON app.student_id = u.user_id " +
                      "JOIN companies comp ON app.company_id = comp.company_id " +
                      "ORDER BY app.student_id";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT app.student_id, u.name AS student_name, app.company_id, comp.company_name, app.status " +
                      "FROM applications app " +
                      "JOIN users u ON app.student_id = u.user_id " +
                      "JOIN companies comp ON app.company_id = comp.company_id " +
                      "WHERE app.student_id = ? " +
                      "ORDER BY app.student_id";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(studentId.trim()));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int studId = rs.getInt("student_id");
                String studentName = rs.getString("student_name");
                String compId = rs.getString("company_id");
                String companyName = rs.getString("company_name");
                String status = rs.getString("status");
                appTableModel.addRow(new Object[]{studId, studentName, compId, companyName, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateApplicationStatus() {
        String studentIdStr = JOptionPane.showInputDialog(this, "Enter Student ID:");
        if (studentIdStr == null || studentIdStr.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
            return;
        }
        String companyId = JOptionPane.showInputDialog(this, "Enter Company ID:");
        if (companyId == null || companyId.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Company ID.");
            return;
        }
        String newStatus = JOptionPane.showInputDialog(this, "Enter New Status (Allowed: APPLIED, SHORTLISTED, REJECTED, PLACED):");
        if (newStatus == null || newStatus.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a New Status.");
            return;
        }
        int studentId = Integer.parseInt(studentIdStr.trim());
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE applications SET status = ? WHERE student_id = ? AND company_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, studentId);
            stmt.setString(3, companyId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Application status updated successfully!");
                loadApplications("");
            } else {
                JOptionPane.showMessageDialog(this, "No matching application found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- Tab 2: Companies ----------------
    private void loadCompanies() {
        companyTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT company_id, company_name, min_cgpa, visit_date FROM companies";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String compId = rs.getString("company_id");
                String companyName = rs.getString("company_name");
                double minCgpa = rs.getDouble("min_cgpa");
                Date visitDate = rs.getDate("visit_date");
                companyTableModel.addRow(new Object[]{compId, companyName, minCgpa, visitDate});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading companies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCompany() {
        String companyId = JOptionPane.showInputDialog(this, "Enter Company ID to Delete:");
        if (companyId == null || companyId.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Company ID to delete.");
            return;
        }
        companyId = companyId.trim();

        try (Connection conn = MySQLConnection.getConnection()) {
            // Delete dependent rows first
            PreparedStatement stmtApps = conn.prepareStatement("DELETE FROM applications WHERE company_id = ?");
            stmtApps.setString(1, companyId);
            stmtApps.executeUpdate();

            PreparedStatement stmtTests = conn.prepareStatement(
                "DELETE FROM company_tests WHERE company_name IN (SELECT company_name FROM companies WHERE company_id = ?)"
            );
            stmtTests.setString(1, companyId);
            stmtTests.executeUpdate();

            // Then delete company
            PreparedStatement stmtCompany = conn.prepareStatement("DELETE FROM companies WHERE company_id = ?");
            stmtCompany.setString(1, companyId);
            int rows = stmtCompany.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Company deleted successfully!");
                loadCompanies();
            } else {
                JOptionPane.showMessageDialog(this, "No such company found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting company: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addCompany() {
        String companyId = JOptionPane.showInputDialog(this, "Enter Company ID (short form, e.g., GGL2):");
        if (companyId == null || companyId.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Company ID.");
            return;
        }
        String companyName = JOptionPane.showInputDialog(this, "Enter Company Name:");
        if (companyName == null || companyName.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a Company Name.");
            return;
        }
        String minCgpaStr = JOptionPane.showInputDialog(this, "Enter Minimum CGPA:");
        if (minCgpaStr == null || minCgpaStr.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter Minimum CGPA.");
            return;
        }
        String visitDate = JOptionPane.showInputDialog(this, "Enter Visit Date (YYYY-MM-DD):");
        if (visitDate == null || visitDate.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter Visit Date.");
            return;
        }
        double minCgpa = Double.parseDouble(minCgpaStr.trim());
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO companies (company_id, company_name, min_cgpa, visit_date) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, companyId.trim());
            stmt.setString(2, companyName.trim());
            stmt.setDouble(3, minCgpa);
            stmt.setString(4, visitDate.trim());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Company added successfully!");
                loadCompanies();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add company.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding company: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- Tab 3: Classroom Booking ----------------
    private void loadAvailableClassrooms(String day, String startTime, String endTime) {
        availClassTableModel.setRowCount(0);
        if (day == null || day.trim().isEmpty() ||
            startTime == null || startTime.trim().isEmpty() ||
            endTime == null || endTime.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter valid day, start time, and end time.");
            return;
        }
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT room_number FROM classrooms WHERE classroom_id NOT IN ( " +
                         "SELECT classroom_id FROM schedule " +
                         "WHERE day = ? " +
                         "  AND start_time < ? " +
                         "  AND end_time > ? " +
                         "UNION " +
                         "SELECT classroom_id FROM bookings " +
                         "WHERE day = ? " +
                         "  AND start_time < ? " +
                         "  AND end_time > ? " +
                         ")";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, day.trim());
            stmt.setString(2, endTime.trim());
            stmt.setString(3, startTime.trim());
            stmt.setString(4, day.trim());
            stmt.setString(5, endTime.trim());
            stmt.setString(6, startTime.trim());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                availClassTableModel.addRow(new Object[]{roomNumber});
            }
            if (availClassTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No available classrooms found for the specified day and time slot.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading available classrooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookSelectedRoom(String companyIdInput) {
        int selectedRow = availClassTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom from the table.");
            return;
        }
        String roomNumber = (String) availClassTable.getValueAt(selectedRow, 0);

        String tpoUserIdStr = JOptionPane.showInputDialog(this, "Enter your TPO User ID:");
        if (tpoUserIdStr == null || tpoUserIdStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your TPO User ID.");
            return;
        }
        int tpoUserId = Integer.parseInt(tpoUserIdStr.trim());

        String purpose = JOptionPane.showInputDialog(this, "Enter the Purpose of Booking:");
        if (purpose == null || purpose.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the purpose of booking.");
            return;
        }

        // ← Fixed: pull directly from the existing components
        String day = (String) dayComboBox.getSelectedItem();
        String bookingStartTime = new SimpleDateFormat("HH:mm:ss")
                .format((Date) startTimeSpinner.getValue());
        String bookingEndTime = new SimpleDateFormat("HH:mm:ss")
                .format((Date) endTimeSpinner.getValue());

        String companyId = companyIdInput;
        if (companyId == null || companyId.trim().isEmpty()) {
            companyId = JOptionPane.showInputDialog(this, "Enter Company ID for this booking:");
            if (companyId == null || companyId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Company ID for booking.");
                return;
            }
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sqlBooking = "INSERT INTO bookings (user_id, classroom_id, day, start_time, end_time, purpose) " +
                                "VALUES (?, (SELECT classroom_id FROM classrooms WHERE room_number = ?), ?, ?, ?, ?)";
            PreparedStatement stmtBooking = conn.prepareStatement(sqlBooking);
            stmtBooking.setInt(1, tpoUserId);
            stmtBooking.setString(2, roomNumber);
            stmtBooking.setString(3, day.trim());
            stmtBooking.setString(4, bookingStartTime.trim());
            stmtBooking.setString(5, bookingEndTime.trim());
            stmtBooking.setString(6, purpose.trim());
            int rows = stmtBooking.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Classroom booked successfully!");

                // Insert into company_tests automatically
                String companyName = "";
                PreparedStatement stmtCompany = conn.prepareStatement(
                        "SELECT company_name FROM companies WHERE company_id = ?");
                stmtCompany.setString(1, companyId.trim());
                ResultSet rsCompany = stmtCompany.executeQuery();
                if (rsCompany.next()) {
                    companyName = rsCompany.getString("company_name");
                } else {
                    JOptionPane.showMessageDialog(this, "Company not found. Cannot insert into company tests.");
                    return;
                }

                PreparedStatement stmtApps = conn.prepareStatement(
                    "SELECT u.name AS student_name " +
                    "FROM applications app " +
                    "JOIN users u ON app.student_id = u.user_id " +
                    "WHERE app.company_id = ? AND app.status = 'SHORTLISTED'"
                );
                stmtApps.setString(1, companyId.trim());
                ResultSet rsApps = stmtApps.executeQuery();
                StringBuilder studentNames = new StringBuilder();
                while (rsApps.next()) {
                    if (studentNames.length() > 0) studentNames.append(", ");
                    studentNames.append(rsApps.getString("student_name"));
                }

                PreparedStatement stmtTest = conn.prepareStatement(
                    "INSERT INTO company_tests " +
                    "(company_name, day, start_time, end_time, room_number, student_names) " +
                    "VALUES (?, ?, ?, ?, ?, ?)"
                );
                stmtTest.setString(1, companyName);
                stmtTest.setString(2, day.trim());
                stmtTest.setString(3, bookingStartTime.trim());
                stmtTest.setString(4, bookingEndTime.trim());
                stmtTest.setString(5, roomNumber);
                stmtTest.setString(6, studentNames.toString());
                int testRows = stmtTest.executeUpdate();
                if (testRows > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Company test record inserted automatically for " + day + "!");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to insert company test record automatically.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error booking classroom: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- Tab 4: Extra Activities ----------------
    private void loadExtraActivities() {
        extraActivityTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT b.booking_id, b.user_id, cl.room_number, b.day, " +
                         "b.start_time, b.end_time, b.purpose " +
                         "FROM bookings b " +
                         "JOIN classrooms cl ON b.classroom_id = cl.classroom_id " +
                         "ORDER BY b.booking_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                extraActivityTableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getInt("user_id"),
                    rs.getString("room_number"),
                    rs.getString("day"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getString("purpose")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading extra activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TPODashboard::new);
    }
}

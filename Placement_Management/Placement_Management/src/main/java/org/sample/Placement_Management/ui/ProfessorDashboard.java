package org.sample.Placement_Management.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner.DateEditor;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.sample.Placement_Management.dao.MySQLConnection;

public class ProfessorDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    // Tab 1: My Schedule
    private DefaultTableModel scheduleTableModel;
    private JTable scheduleTable;

    // Tab 2: Extra Activities
    private DefaultTableModel extraActivitiesTableModel;
    private JTable extraActivitiesTable;
    private JComboBox<String> extraDayCombo;

    // Tab 3: Book Extra Activity
    private DefaultTableModel availClassTableModel;
    private JTable availClassTable;
    private JTextField profIdField, purposeField;
    private JComboBox<String> bookDayCombo;
    private JSpinner bookStartSpinner, bookEndSpinner;

    // Tab 4: Company Tests Data
    private DefaultTableModel companyTestsTableModel;
    private JTable companyTestsTable;
    private JComboBox<String> testDayCombo;

    public ProfessorDashboard() {
        // Nimbus Look & Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("Professor Dashboard");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] days = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        tabbedPane = new JTabbedPane();

        // --- Tab 1: My Schedule ---
        JPanel schedulePanel = new JPanel(new BorderLayout(10,10));
        schedulePanel.setBorder(new EmptyBorder(10,10,10,10));
        scheduleTableModel = new DefaultTableModel(new Object[]{"Day","Start Time","End Time","Room Number"},0);
        scheduleTable = new JTable(scheduleTableModel);
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        JPanel schedCtrl = new JPanel();
        schedCtrl.setLayout(new BoxLayout(schedCtrl, BoxLayout.X_AXIS));
        schedCtrl.setBorder(new EmptyBorder(5,5,5,5));
        schedCtrl.add(new JLabel("Enter Professor ID: "));
        JTextField profIdInput = new JTextField(10);
        schedCtrl.add(profIdInput);
        schedCtrl.add(Box.createRigidArea(new Dimension(10,0)));
        JButton loadSchedBtn = new JButton("Load Schedule");
        loadSchedBtn.addActionListener(e -> {
            String id = profIdInput.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Professor ID.");
                return;
            }
            loadSchedule(id);
        });
        schedCtrl.add(loadSchedBtn);
        schedulePanel.add(schedCtrl, BorderLayout.SOUTH);
        tabbedPane.addTab("My Schedule", schedulePanel);

        // --- Tab 2: Extra Activities ---
        JPanel extraPanel = new JPanel(new BorderLayout(10,10));
        extraPanel.setBorder(new EmptyBorder(10,10,10,10));
        extraActivitiesTableModel = new DefaultTableModel(new Object[]{"Booking ID","Room Number","Purpose","Start Time","End Time"},0);
        extraActivitiesTable = new JTable(extraActivitiesTableModel);
        extraPanel.add(new JScrollPane(extraActivitiesTable), BorderLayout.CENTER);
        JPanel extraCtrl = new JPanel();
        extraCtrl.setLayout(new BoxLayout(extraCtrl, BoxLayout.X_AXIS));
        extraCtrl.setBorder(new EmptyBorder(5,5,5,5));
        extraCtrl.add(new JLabel("Enter Day: "));
        extraDayCombo = new JComboBox<>(days);
        extraCtrl.add(extraDayCombo);
        extraCtrl.add(Box.createRigidArea(new Dimension(10,0)));
        JButton loadExtraBtn = new JButton("Load Extra Activities");
        loadExtraBtn.addActionListener(e -> {
            String day = (String) extraDayCombo.getSelectedItem();
            loadExtraActivitiesForDay(day);
        });
        extraCtrl.add(loadExtraBtn);
        extraPanel.add(extraCtrl, BorderLayout.SOUTH);
        tabbedPane.addTab("Extra Activities", extraPanel);

        // --- Tab 3: Book Extra Activity ---
        JPanel bookPanel = new JPanel(new BorderLayout(10,10));
        bookPanel.setBorder(new EmptyBorder(10,10,10,10));
        JPanel bookingInput = new JPanel();
        bookingInput.setLayout(new BoxLayout(bookingInput, BoxLayout.Y_AXIS));
        bookingInput.setBorder(new EmptyBorder(5,5,5,5));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r1.add(new JLabel("Professor ID:"));
        profIdField = new JTextField(10);
        r1.add(profIdField);
        bookingInput.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Day:"));
        bookDayCombo = new JComboBox<>(days);
        r2.add(bookDayCombo);
        bookingInput.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r3.add(new JLabel("Start Time:"));
        bookStartSpinner = new JSpinner(new SpinnerDateModel());
        bookStartSpinner.setEditor(new DateEditor(bookStartSpinner, "HH:mm:ss"));
        r3.add(bookStartSpinner);
        bookingInput.add(r3);

        JPanel r4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r4.add(new JLabel("End Time:"));
        bookEndSpinner = new JSpinner(new SpinnerDateModel());
        bookEndSpinner.setEditor(new DateEditor(bookEndSpinner, "HH:mm:ss"));
        r4.add(bookEndSpinner);
        bookingInput.add(r4);

        JPanel r5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r5.add(new JLabel("Purpose:"));
        purposeField = new JTextField(10);
        r5.add(purposeField);
        bookingInput.add(r5);

        JButton availBtn = new JButton("Get Available Rooms");
        availBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        availBtn.addActionListener(e -> {
            String day = (String) bookDayCombo.getSelectedItem();
            Date sd = (Date)bookStartSpinner.getValue();
            String start = new SimpleDateFormat("HH:mm:ss").format(sd);
            Date ed = (Date)bookEndSpinner.getValue();
            String end = new SimpleDateFormat("HH:mm:ss").format(ed);
            loadAvailableClassrooms(day, start, end);
        });
        bookingInput.add(availBtn);

        bookPanel.add(bookingInput, BorderLayout.NORTH);
        availClassTableModel = new DefaultTableModel(new Object[]{"Room Number"},0);
        availClassTable = new JTable(availClassTableModel);
        JScrollPane availScroll = new JScrollPane(availClassTable);
        availScroll.setPreferredSize(new Dimension(0,200));
        availScroll.setBorder(new EmptyBorder(20,0,0,0));
        bookPanel.add(availScroll, BorderLayout.CENTER);

        JPanel bookCtrl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton bookBtn = new JButton("Book Extra Activity");
        bookBtn.addActionListener(e -> bookExtraActivity());
        bookCtrl.add(bookBtn);
        bookPanel.add(bookCtrl, BorderLayout.SOUTH);
        tabbedPane.addTab("Book Extra Activity", bookPanel);

        // --- Tab 4: Company Tests Data ---
        JPanel testsPanel = new JPanel(new BorderLayout(10,10));
        testsPanel.setBorder(new EmptyBorder(10,10,10,10));
        companyTestsTableModel = new DefaultTableModel(
            new Object[]{"Test ID","Company Name","Day","Start Time","End Time","Room Number","Student Names"},0
        );
        companyTestsTable = new JTable(companyTestsTableModel);
        testsPanel.add(new JScrollPane(companyTestsTable), BorderLayout.CENTER);
        JPanel testsCtrl = new JPanel();
        testsCtrl.setLayout(new BoxLayout(testsCtrl, BoxLayout.X_AXIS));
        testsCtrl.setBorder(new EmptyBorder(5,5,5,5));
        testsCtrl.add(new JLabel("Enter Day: "));
        testDayCombo = new JComboBox<>(days);
        testsCtrl.add(testDayCombo);
        testsCtrl.add(Box.createRigidArea(new Dimension(10,0)));
        JButton loadTestsBtn = new JButton("Load Company Tests");
        loadTestsBtn.addActionListener(e -> {
            String td = (String) testDayCombo.getSelectedItem();
            loadCompanyTestsForDay(td);
        });
        testsCtrl.add(loadTestsBtn);
        testsPanel.add(testsCtrl, BorderLayout.SOUTH);
        tabbedPane.addTab("Company Tests Data", testsPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private void loadSchedule(String professorId) {
        scheduleTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.day,s.start_time,s.end_time,cl.room_number " +
                "FROM schedule s JOIN classrooms cl ON s.classroom_id=cl.classroom_id " +
                "WHERE s.professor_id=?"
            );
            stmt.setInt(1, Integer.parseInt(professorId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                scheduleTableModel.addRow(new Object[]{
                    rs.getString("day"), rs.getString("start_time"),
                    rs.getString("end_time"), rs.getString("room_number")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExtraActivitiesForDay(String day) {
        extraActivitiesTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT b.booking_id,cl.room_number,b.purpose,b.start_time,b.end_time " +
                "FROM bookings b JOIN classrooms cl ON b.classroom_id=cl.classroom_id " +
                "WHERE b.day=?"
            );
            stmt.setString(1, day);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                extraActivitiesTableModel.addRow(new Object[]{
                    rs.getInt("booking_id"), rs.getString("room_number"),
                    rs.getString("purpose"), rs.getString("start_time"),
                    rs.getString("end_time")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading extra activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAvailableClassrooms(String day, String startTime, String endTime) {
        availClassTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT room_number FROM classrooms WHERE classroom_id NOT IN (" +
                "SELECT classroom_id FROM schedule WHERE day=? AND start_time<? AND end_time>? " +
                "UNION SELECT classroom_id FROM bookings WHERE day=? AND start_time<? AND end_time>?)"
            );
            stmt.setString(1, day);
            stmt.setString(2, endTime);
            stmt.setString(3, startTime);
            stmt.setString(4, day);
            stmt.setString(5, endTime);
            stmt.setString(6, startTime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availClassTableModel.addRow(new Object[]{rs.getString("room_number")});
            }
            if (availClassTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No available classrooms found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading available classrooms: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookExtraActivity() {
        int row = availClassTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room.");
            return;
        }
        String room = (String) availClassTable.getValueAt(row, 0);
        int pid = Integer.parseInt(profIdField.getText().trim());
        String day = (String) bookDayCombo.getSelectedItem();
        String start = new SimpleDateFormat("HH:mm:ss").format((Date) bookStartSpinner.getValue());
        String end = new SimpleDateFormat("HH:mm:ss").format((Date) bookEndSpinner.getValue());
        String purpose = purposeField.getText().trim();
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO bookings(user_id,classroom_id,day,start_time,end_time,purpose) " +
                "VALUES(?,(SELECT classroom_id FROM classrooms WHERE room_number=?),?,?,?,?)"
            );
            stmt.setInt(1, pid);
            stmt.setString(2, room);
            stmt.setString(3, day);
            stmt.setString(4, start);
            stmt.setString(5, end);
            stmt.setString(6, purpose);
            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Booking successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Booking failed.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error booking activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCompanyTestsForDay(String day) {
        companyTestsTableModel.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT test_id,company_name,day,start_time,end_time,room_number,student_names " +
                "FROM company_tests WHERE day=?"
            );
            stmt.setString(1, day);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                companyTestsTableModel.addRow(new Object[]{
                    rs.getInt("test_id"), rs.getString("company_name"),
                    rs.getString("day"), rs.getString("start_time"),
                    rs.getString("end_time"), rs.getString("room_number"),
                    rs.getString("student_names")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading company tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProfessorDashboard::new);
    }
}

package org.sample.Placement_Management.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    // Update these constants based on your MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/placement";
    private static final String USER = "root";
    private static final String PASSWORD = "am2005";

    // Returns a Connection object to the MySQL database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

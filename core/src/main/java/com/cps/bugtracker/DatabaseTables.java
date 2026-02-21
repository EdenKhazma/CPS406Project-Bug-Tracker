package com.cps.bugtracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;

public class DatabaseTables {

    public void CreateTables()
    {
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:BugTracker.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CALL start_ui()");
            System.out.println("UI started. Press Enter to exit..."); //if I want to see the DB put a break point here

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
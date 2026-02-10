package com.cps.bugtracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;

public class DatabaseTables {

    public void CreateTables()
    {
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:analytics.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CALL start_ui()");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
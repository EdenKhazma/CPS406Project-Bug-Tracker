package com.cps.bugtracker;

import java.util.Scanner;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;


public class ScrumMethodClass extends BugTracker {


    public Long cretatePBI(String name, String des){
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:BugTracker.db")) {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO product_backlog_items(name, description)"+
                    "VALUES (?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, des);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        }
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return null;

    }


}

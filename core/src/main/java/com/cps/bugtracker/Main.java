package com.cps.bugtracker;

import java.sql.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
//        System.out.printf("Hello and welcome!");
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }

        // 1. Establish connection (this creates the file if it doesn't exist)
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:analytics.db")) {

            Statement stmt = conn.createStatement();

            stmt.execute("CALL start_ui()");

            // 2. Create a table
//            stmt.execute("CREATE TABLE IF NOT EXISTS sensors (type TEXT, reading DOUBLE); " +
//                    "CREATE TABLE course(" +
//                    "    course_number INT PRIMARY KEY," +
//                    "    course_name VARCHAR(100)," +
//                    "    credit_hours INT," +
//                    "    department VARCHAR(100)" +
//                    ");");

            // 3. Insert data using PreparedStatements (Best Practice)
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO sensors VALUES (?, ?)");
            pstmt.setString(1, "temperature");
            pstmt.setDouble(2, 22.5);
            pstmt.executeUpdate();

            // 4. Query the data
            ResultSet rs = stmt.executeQuery("SELECT * FROM sensors");
            while (rs.next()) {
                System.out.println(rs.getString("type") + ": " + rs.getDouble("reading"));
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
package com.cps.bugtracker;

import java.util.Scanner;
import java.io.IOException;
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
//            System.out.println("UI started. Press Enter to exit..."); //if I want to see the DB put a break point here
//            int i = System.in.read();  // temporary pause so DB stays alive


            stmt.execute("CREATE SEQUENCE IF NOT EXISTS user_id_seq START 1 INCREMENT BY 1;" +
                    "CREATE SEQUENCE IF NOT EXISTS artificat_id_seq START 1 INCREMENT BY 1;" +

                    "CREATE TABLE IF NOT EXISTS users(" +
                    "USER_ID INTEGER PRIMARY KEY DEFAULT(nextval('user_id_seq'))," +
                    "USERNAME VARCHAR(10) NOT NULL," +
                    "FULL_NAME VARCHAR(40)," +
                    "EMAIL VARCHAR(40)); " +

                    "");

            stmt.execute("CALL start_ui()");
            System.out.println("UI started. Press Enter to exit...");
            int i = System.in.read();  // temporary pause so DB stays alive


        } catch (SQLException | IOException e) {
            System.err.println("Database error: " + e.getMessage());
        }





    }
}
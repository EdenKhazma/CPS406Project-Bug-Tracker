package com.cps.bugtracker;

import java.util.Scanner;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;

public class DatabaseTables {

    private Connection conn = null;

    public Connection getConnection() {
        return conn;
    }


    public void CreateConnection() {
        try {
            // prevent reopening connection
            if (conn != null && !conn.isClosed()) {
                return;
            }

            conn = DriverManager.getConnection("jdbc:duckdb:BugTracker.db");

        } catch (SQLException e) {
            conn = null; // IMPORTANT

            if (e.getMessage().contains("lock")) {
                System.err.println(" Database is already in use. Please close other instances and try again.");
            } else {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }


    public void CreateTables()
    {
        try  {
            CreateConnection();

            Statement stmt = conn.createStatement();
           // stmt.execute("CALL start_ui()"); //-> uncomment to start UI immediately when running this class, comment out in main class to avoid starting UI twice


            stmt.execute("CREATE SEQUENCE IF NOT EXISTS Bug_id_seq START 1 INCREMENT BY 1;");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS Pbi_id_seq START 1 INCREMENT BY 1;");



            stmt.execute("CREATE TABLE IF NOT EXISTS product_backlog_items (" +
                    "id BIGINT PRIMARY KEY DEFAULT nextval('Pbi_id_seq')," +
                    "name VARCHAR(255) NOT NULL," +
                    "description TEXT);" );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bugs (" +
                    "    Bug_ID  INTEGER PRIMARY KEY DEFAULT nextval('Bug_id_seq') ," +
                    "    title   VARCHAR(255) NOT NULL,        " +
                    "    description  TEXT NOT NULL,          " +
                    "    severity     VARCHAR(20) NOT NULL CHECK (" +
                    "        severity IN ('CRITICAL','MAJOR','MINOR','TRIVIAL')),"+
                    "    status       VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (" +
                    "        status IN ('NEW','PLANNED','IN PROGRESS','RESOLVED/CLOSED','TESTED','REJECTED'))," +
                    "    fast_track BOOLEAN NOT NULL DEFAULT FALSE,   " +
                    "    created_at DATE NOT NULL DEFAULT CURRENT_DATE,   " +
                    "    updated_at DATE NULL,   " +
                    "    resolved_at DATE NULL,                                " +
                    "    external_link TEXT NULL,            " +
                    "    pbi_id BIGINT NULL REFERENCES product_backlog_items(id),                 " +
                    "    phase VARCHAR(30) NULL CHECK (" +
                    "        phase IN ('REQUIREMENTS','DESIGN'," +
                    "                  'IMPLEMENTATION','TESTING','DEPLOYMENT')" +
                    "    )                                   " +
                    ");");


            System.out.println("Tables created successfully!");


        } catch (SQLException  e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    public void waitAndClose() {
        try {
            System.out.println("Press Enter to close...");
            int i = System.in.read();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            closeConnection();
        }
    }
}
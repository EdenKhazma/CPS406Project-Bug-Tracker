package com.cps.bugtracker;

import java.util.Scanner;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;


public class ScrumMethodClass extends BugTracker {

    public int cretatePBI(Connection conn, String name, String des) {
        if ((!name.isEmpty()) && (name.length() <= 255) && (!des.isEmpty())) {
            try (Statement stmt = conn.createStatement();) {
                String sql = "INSERT INTO product_backlog_items(name, description)" +
                        "VALUES (?, ?) RETURNING id";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, des);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
        return 0;
    }

//in this function status will get Nes automatically and the created date as well.
    public int insertBug(Connection conn,
                         String title,
                         String description,
                         String severity,
                         Boolean fastTrack,
                         String externalLink,
                         Integer pbiId,
                         String phase) throws SQLException {
        if (conn.isClosed()) {
            int a = 0;
        }

        String sql = "INSERT INTO bugs (title, description, severity, fast_track, external_link, pbi_id, phase) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING Bug_ID";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, severity);
            stmt.setBoolean(4, fastTrack != null ? fastTrack : false);

            // Nullable fields
            if (externalLink != null) stmt.setString(5, externalLink);
            else stmt.setNull(5, Types.VARCHAR);

            if (pbiId != null) stmt.setInt(6, pbiId);
            else stmt.setNull(6, Types.BIGINT);

            if (phase != null) stmt.setString(7, phase);
            else stmt.setNull(7, Types.VARCHAR);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Bug_ID");
                }

            }
            throw new SQLException("Insert failed, no ID returned.");
        }

    }
}

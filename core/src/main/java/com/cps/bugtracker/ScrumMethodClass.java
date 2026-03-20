package com.cps.bugtracker;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ScrumMethodClass extends BugTracker {

    public ScrumMethodClass(String title, String description, String severity, String status, boolean fastTrack, LocalDate updatedAt, LocalDate resolvedAt, String externalLink, Long pbiId, String phase) {
        super(title, description, severity, status, fastTrack, updatedAt, resolvedAt, externalLink, pbiId, phase);
    }

    public ScrumMethodClass() {
        super();
    }


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

    public List<ScrumMethodClass> getScrumBugs(Connection conn) {
        List<ScrumMethodClass> bugs = new ArrayList<>();
        String sql = "SELECT * FROM bugs WHERE pbi_id IS NOT NULL ORDER BY pbi_id ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ScrumMethodClass scrum = new ScrumMethodClass();     // create a new Scrum object

                scrum.mapResultSetToBug(rs);   // fill it using inherited mapper
                bugs.add(scrum);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching scrum bugs: " + e.getMessage());
        }

        return bugs;
    }


}

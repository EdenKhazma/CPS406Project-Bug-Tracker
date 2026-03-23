package com.cps.bugtracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class Waterfall extends BugTracker {

    public Waterfall() {
        super();
    }

    public Waterfall(String title, String description, String severity, String status, boolean fastTrack, java.time.LocalDate updatedAt, java.time.LocalDate resolvedAt, String externalLink, String phase) {
        super(title, description, severity, status, fastTrack, updatedAt, resolvedAt, externalLink, null, phase);
    }

    public void createWaterfallBug(Connection conn, String phase, String title, String description, String severity, String status, boolean fastTrack, String externalLink) {
        try {
            int bugId = insertBug(conn, title, description, severity, fastTrack, externalLink, null, phase);

            if (status != null && !status.equalsIgnoreCase("NEW")) {
                String sql = "UPDATE bugs SET status = ?, updated_at = CURRENT_DATE WHERE Bug_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, status);
                    pstmt.setInt(2, bugId);
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Waterfall bug created with ID: " + bugId);

        } catch (SQLException e) {
            System.err.println("Error creating Waterfall bug: " + e.getMessage());
        }
    }

    public boolean updateWaterfallBug(Connection conn, int bugId, String phase, String title, String description, String severity, String status, boolean fastTrack, String externalLink) {

        String sql = "UPDATE bugs " +
                     "SET phase = ?, title = ?, description = ?, severity = ?, status = ?, " +
                     "    fast_track = ?, external_link = ?, updated_at = CURRENT_DATE " +
                     "WHERE Bug_ID = ? AND pbi_id IS NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phase);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, severity);
            pstmt.setString(5, status);
            pstmt.setBoolean(6, fastTrack);

            if (externalLink != null) {
                pstmt.setString(7, externalLink);
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            pstmt.setInt(8, bugId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                if ("RESOLVED".equalsIgnoreCase(status) ||
                    "CLOSED".equalsIgnoreCase(status) ||
                    "REJECTED".equalsIgnoreCase(status)) {

                    String resolvedSql = "UPDATE bugs SET resolved_at = CURRENT_DATE WHERE Bug_ID = ?";
                    try (PreparedStatement resolvedStmt = conn.prepareStatement(resolvedSql)) {
                        resolvedStmt.setInt(1, bugId);
                        resolvedStmt.executeUpdate();
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating Waterfall bug: " + e.getMessage());
        }

        return false;
    }

    public List<Waterfall> showWaterfallBugs(Connection conn) {
        List<Waterfall> bugs = new ArrayList<>();

        String sql = "SELECT * FROM bugs " +
                     "WHERE pbi_id IS NULL AND phase IS NOT NULL " +
                     "ORDER BY phase ASC, Bug_ID ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Waterfall bug = new Waterfall();
                bug.mapResultSetToBug(rs);
                bugs.add(bug);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching Waterfall bugs: " + e.getMessage());
        }

        return bugs;
    }
}

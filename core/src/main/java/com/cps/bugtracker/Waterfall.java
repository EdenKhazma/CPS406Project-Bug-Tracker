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

    public boolean updateWaterfallBug(Connection conn, int bugId, String phase, String title, String description, String severity, String status, Boolean fastTrack, String externalLink) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE bugs SET updated_at = CURRENT_DATE");
        int paramIndex = 1;

        // Build dynamic SQL based on non-null parameters
        if (phase != null) {
            sqlBuilder.append(", phase = ?");
        }
        if (title != null) {
            sqlBuilder.append(", title = ?");
        }
        if (description != null) {
            sqlBuilder.append(", description = ?");
        }
        if (severity != null) {
            sqlBuilder.append(", severity = ?");
        }
        if (status != null) {
            sqlBuilder.append(", status = ?");
        }
        if (fastTrack != null) {
            sqlBuilder.append(", fast_track = ?");
        }
        if (externalLink != null) {
            sqlBuilder.append(", external_link = ?");
        }

        sqlBuilder.append(" WHERE Bug_ID = ? AND pbi_id IS NULL");

        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            // Set the non-null parameters in order
            if (phase != null) {
                pstmt.setString(paramIndex++, phase);
            }
            if (title != null) {
                pstmt.setString(paramIndex++, title);
            }
            if (description != null) {
                pstmt.setString(paramIndex++, description);
            }
            if (severity != null) {
                pstmt.setString(paramIndex++, severity);
            }
            if (status != null) {
                pstmt.setString(paramIndex++, status);
            }
            if (fastTrack != null) {
                pstmt.setBoolean(paramIndex++, fastTrack);
            }
            if (externalLink != null) {
                pstmt.setString(paramIndex++, externalLink);
            }

            // Set the WHERE clause parameter
            pstmt.setInt(paramIndex, bugId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Set resolved_at if status is a resolved state
                if (status != null && ("RESOLVED".equalsIgnoreCase(status) ||
                    "CLOSED".equalsIgnoreCase(status) ||
                    "REJECTED".equalsIgnoreCase(status))) {

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

package com.cps.bugtracker;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ScrumMethodClass extends BugTracker {

    String Pbi_name;
    String Pbi_des;

    public ScrumMethodClass(String Pbi_name,String Pbi_des,String title, String description, String severity, String status, boolean fastTrack, LocalDate updatedAt, LocalDate resolvedAt, String externalLink, Long pbiId, String phase) {
        super(title, description, severity, status, fastTrack, updatedAt, resolvedAt, externalLink, pbiId, phase);
        this.Pbi_name  = Pbi_name;
        this.Pbi_des = Pbi_des;

    }

    public ScrumMethodClass() {
        super();
    }

    public String getPbiName() {
        return Pbi_name;
    }

    public int createPBI(Connection conn, String name, String des) {
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

    public void createScrumBug(Connection conn, String pbi_name, String pbi_des,
                               String title,
                               String description, String severity, boolean fastTrack, String externalLink) {
        int pbi_id = createPBI(conn, pbi_name, pbi_des);
        if (pbi_id != 0) {
            try {
                int bug_id = insertBug(conn, title, description, severity, fastTrack, externalLink, pbi_id, null);
                System.out.println("Bug created with ID: " + bug_id);
            } catch (SQLException e) {
                System.err.println("Database error creating bug: " + e.getMessage());
            }
        } else {
            System.out.println("Something went wrong with creating PBI. Bug not created.");
        }
    }


    public List<ScrumMethodClass> getScrumBugs(Connection conn) {
        List<ScrumMethodClass> bugs = new ArrayList<>();
        String sql = "SELECT b.*, p.name AS pbi_name " +
                "FROM bugs b " +
                "JOIN product_backlog_items p ON b.pbi_id = p.id " +
                "WHERE b.pbi_id IS NOT NULL " +
                "ORDER BY b.pbi_id ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ScrumMethodClass scrum = new ScrumMethodClass();
                scrum.mapResultSetToBug(rs);
                scrum.Pbi_name = rs.getString("pbi_name");  // capture the joined name
                bugs.add(scrum);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching scrum bugs: " + e.getMessage());
        }

        return bugs;
    }


    public List<String> getAllPbiNames(Connection conn) {
        List<String> pbiNames = new ArrayList<>();
        String sql = "SELECT name FROM product_backlog_items ORDER BY id ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                pbiNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching PBI names: " + e.getMessage());
        }

        return pbiNames;
    }


    public boolean updateScrumBug(Connection conn, int bugId, Long pbiId, String title, 
                                   String description, String severity, String status, Boolean fastTrack) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE bugs SET updated_at = CURRENT_DATE");
        int paramIndex = 1;

        // Build dynamic SQL based on non-null parameters
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

        sqlBuilder.append(" WHERE Bug_ID = ? AND pbi_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            // Set the non-null parameters in order
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

            // Set the WHERE clause parameters
            pstmt.setInt(paramIndex++, bugId);
            pstmt.setLong(paramIndex, pbiId);

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
                System.out.println("Scrum bug " + bugId + " updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating Scrum bug: " + e.getMessage());
        }

        return false;
    }

}

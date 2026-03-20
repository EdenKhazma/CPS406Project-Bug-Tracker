package com.cps.bugtracker;

import java.sql.*;
import java.time.LocalDate;

//holds shared functions

public class BugTracker {



    private int bugId;
    private String title;
    private String description;
    private String severity;      // CRITICAL, MAJOR, MINOR, TRIVIAL
    private String status;        // NEW, PLANNED, IN_PROGRESS, RESOLVED, TESTED, CLOSED, REJECTED
    private boolean fastTrack;
    private LocalDate createdAt;
    private LocalDate updatedAt;  // nullable
    private LocalDate resolvedAt; // nullable
    private String externalLink;  // nullable
    private Long pbiId;           // nullable → Long (capital L, not long)
    private String phase;         // nullable


    public BugTracker() {}  // ← add this

    public BugTracker(String title, String description, String severity, String status, boolean fastTrack, LocalDate updatedAt, LocalDate resolvedAt, String externalLink, Long pbiId, String phase) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.fastTrack = fastTrack;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.externalLink = externalLink;
        this.pbiId = pbiId;
        this.phase = phase;
    }

    public int getBugId() {return bugId;}

    public void setBugId(int bugId) {this.bugId = bugId;}

    public LocalDate getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDate createdAt) {this.createdAt = createdAt;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getSeverity() {return severity;}

    public void setSeverity(String severity) {this.severity = severity;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public boolean isFastTrack() {return fastTrack;}

    public void setFastTrack(boolean fastTrack) {this.fastTrack = fastTrack;}

    public LocalDate getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(LocalDate updatedAt) {this.updatedAt = updatedAt;}

    public LocalDate getResolvedAt() {return resolvedAt;}

    public void setResolvedAt(LocalDate resolvedAt) {this.resolvedAt = resolvedAt;}

    public String getExternalLink() {return externalLink;}

    public void setExternalLink(String externalLink) {this.externalLink = externalLink;}

    public Long getPbiId() {return pbiId;}

    public void setPbiId(Long pbiId) {this.pbiId = pbiId;}

    public String getPhase() {return phase;}

    public void setPhase(String phase) {this.phase = phase;}

    public int insertBug(Connection conn,
                         String title,
                         String description,
                         String severity,
                         Boolean fastTrack,
                         String externalLink,
                         Integer pbiId,
                         String phase) throws SQLException {
//        if (conn.isClosed()) {
//            int a = 0;
//        }

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

    public void mapResultSetToBug(ResultSet rs) throws SQLException {
        this.setBugId(rs.getInt("Bug_ID"));          // ← was missing
        this.setTitle(rs.getString("title"));
        this.setDescription(rs.getString("description"));
        this.setSeverity(rs.getString("severity"));
        this.setStatus(rs.getString("status"));
        this.setFastTrack(rs.getBoolean("fast_track"));
        this.setCreatedAt(rs.getDate("created_at").toLocalDate());

        Date updatedAt = rs.getDate("updated_at");
        this.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDate() : null);

        Date resolvedAt = rs.getDate("resolved_at");
        this.setResolvedAt(resolvedAt != null ? resolvedAt.toLocalDate() : null);

        this.setExternalLink(rs.getString("external_link"));

        long pbiId = rs.getLong("pbi_id");           // ← safe null handling
        this.setPbiId(rs.wasNull() ? null : pbiId);

        this.setPhase(rs.getString("phase"));
    }
}

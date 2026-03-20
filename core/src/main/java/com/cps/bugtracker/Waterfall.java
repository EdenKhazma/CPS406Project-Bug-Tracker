package com.cps.bugtracker;

import java.time.LocalDate;

public class Waterfall extends BugTracker {
    public Waterfall(String title, String description, String severity, String status, boolean fastTrack, LocalDate updatedAt, LocalDate resolvedAt, String externalLink, Long pbiId, String phase) {
        super(title, description, severity, status, fastTrack, updatedAt, resolvedAt, externalLink, null, phase);
    }

}

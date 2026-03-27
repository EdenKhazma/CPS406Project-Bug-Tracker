package com.cps.bugtracker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuite {

    private static DatabaseTables db;
    private static ScrumMethodClass scrum;
    private static Waterfall waterfall;

    @BeforeAll
    static void setup() {
        db = new DatabaseTables();
        db.CreateConnection();
        db.CreateTables();

        scrum = new ScrumMethodClass();
        waterfall = new Waterfall();
    }

//    @AfterAll
//    static void tearDown() {
//        db.waitAndClose();
//        db.closeConnection();
//    }

    @Test
    void testCreatePBI_returnsValidId() {
        int pbiId = scrum.createPBI(db.getConnection(), "Test PBI", "Test PBI description");
        assertTrue(pbiId > 0, "PBI ID should be greater than 0");
    }
    
    @Test
    void testCreatePBI_emptyName() {
        int pbiId = scrum.createPBI(db.getConnection(), "", "Test description");
        assertEquals(0, pbiId);
    }

    @Test
    void testCreatePBI_longName() {
        String longName = "a".repeat(256);
        int pbiId = scrum.createPBI(db.getConnection(), longName, "Test description");
        assertEquals(0, pbiId);
    }

    @Test
    void testCreatePBI_emptyDescription() {
        int pbiId = scrum.createPBI(db.getConnection(), "Test name", "");
        assertEquals(0, pbiId);
    }

    @Test
    void testCreateScrumBug() {
        int initialSize = scrum.getScrumBugs(db.getConnection()).size();
        scrum.createScrumBug(db.getConnection(), "PBI_test", "testing scrum creation", "Bug Title", "Bug Desc", "MAJOR", true, "link");
        int finalSize = scrum.getScrumBugs(db.getConnection()).size();
        assertEquals(initialSize + 1, finalSize);
    }
    
    @Test
    void testUpdateScrumBug() {
        // Get existing scrum bugs
        List<ScrumMethodClass> bugs = scrum.getScrumBugs(db.getConnection());
        ScrumMethodClass bugToUpdate;

        if (bugs.isEmpty()) {
            // Create a bug if none exist
            scrum.createScrumBug(db.getConnection(), "Update Test PBI", "PBI for update testing", "Update Test Bug", "Initial Description", "MINOR", false, null);
            bugs = scrum.getScrumBugs(db.getConnection());
        }

        bugToUpdate = bugs.get(bugs.size() - 1);
        Long pbiId = bugToUpdate.getPbiId();

        // Update the bug with new values
        boolean updated = scrum.updateScrumBug(db.getConnection(), bugToUpdate.getBugId(), pbiId, 
                                                "Updated Title", "Updated Description", "MAJOR", "IN_PROGRESS", true);
        assertTrue(updated);

        // Verify the update
        bugs = scrum.getScrumBugs(db.getConnection());
        ScrumMethodClass updatedBug = bugs.stream().filter(b -> b.getBugId() == bugToUpdate.getBugId()).findFirst().orElse(null);
        assertNotNull(updatedBug);
        assertEquals("Updated Title", updatedBug.getTitle());
        assertEquals("Updated Description", updatedBug.getDescription());
        assertEquals("MAJOR", updatedBug.getSeverity());
        assertEquals("IN_PROGRESS", updatedBug.getStatus());
        assertTrue(updatedBug.isFastTrack());
        assertNotNull(updatedBug.getUpdatedAt());
    }

    @Test
    void testUpdateScrumBug_PartialUpdate() {
        // Get existing scrum bugs
        List<ScrumMethodClass> bugs = scrum.getScrumBugs(db.getConnection());
        ScrumMethodClass bugToUpdate;

        if (bugs.isEmpty()) {
            // Create a bug if none exist
            scrum.createScrumBug(db.getConnection(), "Partial Update PBI", "PBI for partial update testing", "Partial Update Bug", "Initial Description", "CRITICAL", true, null);
            bugs = scrum.getScrumBugs(db.getConnection());
        }

        bugToUpdate = bugs.get(bugs.size() - 1);
        Long pbiId = bugToUpdate.getPbiId();
        String originalTitle = bugToUpdate.getTitle();
        String originalDescription = bugToUpdate.getDescription();

        // Update only status, leaving other fields as null (unchanged)
        boolean updated = scrum.updateScrumBug(db.getConnection(), bugToUpdate.getBugId(), pbiId, 
                                                null, null, null, "RESOLVED", null);
        assertTrue(updated);

        // Verify the update - status changed, others remain the same
        bugs = scrum.getScrumBugs(db.getConnection());
        ScrumMethodClass updatedBug = bugs.stream().filter(b -> b.getBugId() == bugToUpdate.getBugId()).findFirst().orElse(null);
        assertNotNull(updatedBug);
        assertEquals("RESOLVED", updatedBug.getStatus());
        assertEquals(originalTitle, updatedBug.getTitle()); // Title should remain unchanged
        assertEquals(originalDescription, updatedBug.getDescription()); // Description should remain unchanged
        assertNotNull(updatedBug.getResolvedAt()); // resolved_at should be set when status is RESOLVED
    }

    @Test
    void testScrumBugs() {
        Connection conn = db.getConnection();
        List<ScrumMethodClass> scrumBugs = scrum.getScrumBugs(conn);

        System.out.println("Total Scrum Bugs: " + scrumBugs.size());
        System.out.println("─────────────────────────────────────────");

        for (ScrumMethodClass bug : scrumBugs) {
            System.out.println("Bug ID    : " + bug.getBugId());
            System.out.println("PBI ID    : " + bug.getPbiId());
            System.out.println("Title     : " + bug.getTitle());
            System.out.println("Severity  : " + bug.getSeverity());
            System.out.println("Status    : " + bug.getStatus());
            System.out.println("Phase     : " + bug.getPhase());
            System.out.println("Fast Track: " + bug.isFastTrack());
            System.out.println("Created   : " + bug.getCreatedAt());
            System.out.println("Updated   : " + bug.getUpdatedAt());
            System.out.println("Resolved  : " + bug.getResolvedAt());
            System.out.println("─────────────────────────────────────────");
        }
    }

    @Test
    void testCreateWaterfallBug() {
        int initialSize = waterfall.showWaterfallBugs(db.getConnection()).size();
        waterfall.createWaterfallBug(db.getConnection(), "REQUIREMENTS", "Test Waterfall Bug", "Test Description", "MAJOR", "NEW", false, "http://example.com");
        int finalSize = waterfall.showWaterfallBugs(db.getConnection()).size();
        assertEquals(initialSize + 1, finalSize);
    }

    @Test
    void testUpdateWaterfallBug() {
        // Get existing bugs
        List<Waterfall> bugs = waterfall.showWaterfallBugs(db.getConnection());
        Waterfall bugToUpdate;

        if (bugs.isEmpty()) {
            // Create a bug if none exist
            waterfall.createWaterfallBug(db.getConnection(), "DESIGN", "Update Test Bug", "Initial Description", "MINOR", "NEW", false, null);
            bugs = waterfall.showWaterfallBugs(db.getConnection());
        }

        bugToUpdate = bugs.get(bugs.size() - 1);

        // Update the bug
        boolean updated = waterfall.updateWaterfallBug(db.getConnection(), bugToUpdate.getBugId(), "IMPLEMENTATION", "Updated Title", "Updated Description", "MAJOR", "IN_PROGRESS", true, "http://updated.com");
        assertTrue(updated);

        // Verify the update
        bugs = waterfall.showWaterfallBugs(db.getConnection());
        Waterfall updatedBug = bugs.stream().filter(b -> b.getBugId() == bugToUpdate.getBugId()).findFirst().orElse(null);
        assertNotNull(updatedBug);
        assertEquals("Updated Title", updatedBug.getTitle());
        assertEquals("Updated Description", updatedBug.getDescription());
        assertEquals("IMPLEMENTATION", updatedBug.getPhase());
        assertEquals("MAJOR", updatedBug.getSeverity());
        assertEquals("IN_PROGRESS", updatedBug.getStatus());
        assertTrue(updatedBug.isFastTrack());
        assertEquals("http://updated.com", updatedBug.getExternalLink());
    }

    @Test
    void testShowWaterfallBugs() {
        List<Waterfall> bugs = waterfall.showWaterfallBugs(db.getConnection());
        assertNotNull(bugs);

        System.out.println("Total Waterfall Bugs: " + bugs.size());
        System.out.println("─────────────────────────────────────────");

        for (Waterfall bug : bugs) {
            System.out.println("Bug ID    : " + bug.getBugId());
            System.out.println("Title     : " + bug.getTitle());
            System.out.println("Description: " + bug.getDescription());
            System.out.println("Severity  : " + bug.getSeverity());
            System.out.println("Status    : " + bug.getStatus());
            System.out.println("Phase     : " + bug.getPhase());
            System.out.println("Fast Track: " + bug.isFastTrack());
            System.out.println("External Link: " + bug.getExternalLink());
            System.out.println("Created   : " + bug.getCreatedAt());
            System.out.println("Updated   : " + bug.getUpdatedAt());
            System.out.println("Resolved  : " + bug.getResolvedAt());
            System.out.println("─────────────────────────────────────────");

            assertNotNull(bug.getPhase());
            assertNull(bug.getPbiId()); // Waterfall bugs should not have PBI ID
        }
    }

}

package com.cps.bugtracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuite {

    private static DatabaseTables db;
    private static ScrumMethodClass scrum;

    @BeforeAll
    static void setup() {
        db = new DatabaseTables();
        db.CreateConnection();

        scrum = new ScrumMethodClass();
    }
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
    void testScrumBugs() {
        // connect to database and create tables

        DatabaseTables db = new DatabaseTables();
        db.CreateConnection();

        ScrumMethodClass scrum = new ScrumMethodClass();
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
            System.out.println("─────────────────────────────────────────");
        }
//        db.waitAndClose();
       db.closeConnection();
    }
}

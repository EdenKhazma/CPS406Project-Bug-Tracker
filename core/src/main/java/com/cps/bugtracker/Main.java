package com.cps.bugtracker;

import java.sql.*;
//import java.util.Scanner;
import java.sql.Connection;
import java.util.List;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

//        Scanner scanner = new Scanner(System.in);


        DatabaseTables db = new DatabaseTables();
        ScrumMethodClass scrum = new ScrumMethodClass();

        db.CreateConnection();
        db.CreateTables();
        Connection conn = db.getConnection();


//        int pbiCreated = scrum.cretatePBI(conn, "Pb_01","Test Scrum");
//
//        System.out.println(pbiCreated);
//
//        if(pbiCreated != 0)
//        {
//            System.out.println("Entered Correctly\n");//call the bug entering function
//        }
//        else{
//            System.out.println("Something went wrong\n");//tell user he entered something incorrectly...
//        }
////        db.waitAndClose();
////        add a line to ask use to choose Product backlog (this is for updating)
//
//    if(pbiCreated != 0)
//    {
//        BugDetails bugDetails1 = new BugDetails("Test4", "CRITICAL",
//                "Testing creation of bug in scrum",true,"www.link.com","TESTING");
//
//        try {
//            int bugID = scrum.insertBug(conn,
//                    bugDetails1.getTitle(),
//                    bugDetails1.getBug_description(),
//                    bugDetails1.getBug_severity(),
//                    bugDetails1.isBug_fastrackF(),
//                    bugDetails1.getLink(),
//                    pbiCreated,
//                    null);
//            System.out.println(bugID);
//        } catch (SQLException e) {
//            throw new RuntimeException ("Failed to insert bug",e);
//        }
//
//    }
//    else{
//        System.out.println("Something went wrong\n");
//    }


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

    db.waitAndClose();
   }

}
package com.cps.bugtracker;

import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String namePB;
        String PB_description;

        DatabaseTables db = new DatabaseTables();
        ScrumMethodClass scrum = new ScrumMethodClass();

        db.CreateConnection();
        db.CreateTables();
        Connection conn = db.getConnection();

        namePB = "Pb_test";
        PB_description = "ABC";

        int pbiCreated = scrum.cretatePBI(conn, namePB,PB_description);

        System.out.println(pbiCreated);

        if(pbiCreated != 0)
        {
            System.out.println("Entered Correctly\n");//call the bug entering function
        }
        else{
            System.out.println("Something went wrong\n");//tell user he entered something incorrectly...
        }
//        db.waitAndClose();
//        add a line to ask use to choose proudect back log (this is for updating)

    if(pbiCreated != 0)
    {
        BugDetails bugDetails1 = new BugDetails("Test3", "CRITICAL",
                "Testing creation of bug in scrum",true,"www.link.com","TESTING");

        try {
            int bugID = scrum.insertBug(conn,
                    bugDetails1.getTitle(),
                    bugDetails1.getBug_description(),
                    bugDetails1.getBug_severity(),
                    bugDetails1.isBug_fastrackF(),
                    bugDetails1.getLink(),
                    pbiCreated,
                    bugDetails1.getPhase());
            System.out.println(bugID);
        } catch (SQLException e) {
            throw new RuntimeException ("Failed to insert bug",e);
        }

    }
    else{
        System.out.println("Something went wrong\n");
    }
    db.waitAndClose();
   }
}
package com.cps.bugtracker;

import java.sql.*;
import java.util.Scanner;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


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

        System.out.println("Enter Prodect Backlog item name\n");
        namePB = scanner.nextLine();
        System.out.println("Enter Prodect Backlog item description\n");
        PB_description = scanner.nextLine();

        if(scrum.cretatePBI(namePB,PB_description) != null)
        {
            System.out.println("Entered Correctly\n");
        }
        else{
            System.out.println("Something went wrong\n");
        }
        db.waitAndClose();




//      Scanner scanner = new Scanner(System.in);
//
//      String namePB;
//      String PB_description;
//
//     DatabaseTables db = new DatabaseTables();
//     ScrumMethodClass scrum = new ScrumMethodClass();
//
//     db.CreateConnection();
//     db.CreateTables();
//
//     System.out.println("Enter Prodect Backlog item name\n");
//     namePB = scanner.next();
//     System.out.println("Enter Prodect Backlog item description\n");
//     PB_description = scanner.next();
//
//     scrum.cretatePBI(namePB,PB_description);

//    if(PBid != 0)
//    {
//        System.out.println("Now can enter Scrum Bug details\n");
//        System.out.println("Enter Bug title\n");
//        String title = scanner.next();
//        System.out.println("Enter description\n");
//        String bug_description = scanner.next();
//        System.out.println("Choose severity(For the UI but for now just write it)\n");
//        String severity = scanner.next();
//        System.out.println("Choose status(For the UI but for now just write it)\n");
//        String status = scanner.next();
//        System.out.println("Copy links to this text box\n");
//        String link = scanner.next();
//        System.out.println("choose phase(For the UI but for now just write it)\n");
//        String phase = scanner.next();
//
//        scrum.insertBug();
//
//    }
//    else{
//        System.out.println("Something went wrong\n");
//    }
//    db.waitAndClose();
   }
}
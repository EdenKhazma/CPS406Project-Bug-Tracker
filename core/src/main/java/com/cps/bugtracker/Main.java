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
    }
}
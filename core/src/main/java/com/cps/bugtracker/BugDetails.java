package com.cps.bugtracker;
//for testing scrum
public class BugDetails {


    public BugDetails(String title,
                      String bug_severity,
                      String bug_description,
                      boolean bug_fastrackF,
                      String link,
                      String phase) {
        this.title = title;
        this.bug_severity = bug_severity;
        this.bug_description = bug_description;
        this.bug_fastrackF = bug_fastrackF;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }


    public String getLink() {
        return link;
    }

    public boolean isBug_fastrackF() {
        return bug_fastrackF;
    }

    public String getBug_severity() {
        return bug_severity;
    }

    public String getBug_description() {
        return bug_description;
    }

    String title;
    String bug_description;
    String bug_severity;
    boolean bug_fastrackF;
    String link;
}

package com.cps.bugtracker;

//Class for testing pearpses


public class BugWaterfallDetails extends BugDetails{

    String Phase;

    public BugWaterfallDetails(String title, String bug_severity, String bug_description, boolean bug_fastrackF, String link, String phase, String phase1) {
        super(title, bug_severity, bug_description, bug_fastrackF, link, phase);
        Phase = phase1;
    }

    public String getPhase() {
        return Phase;
    }
}

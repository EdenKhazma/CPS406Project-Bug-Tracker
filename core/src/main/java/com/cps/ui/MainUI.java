package com.cps.ui;

import com.cps.bugtracker.DatabaseTables;
import com.cps.bugtracker.ScrumMethodClass;
import com.cps.bugtracker.Waterfall;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.List;

public class MainUI extends JFrame {

    private DatabaseTables db;
    private ScrumMethodClass scrum;
    private DefaultTableModel model;
    private JTable table;
    private Waterfall waterfall;

    public MainUI() {

        db = new DatabaseTables();
        db.CreateTables();
        scrum = new ScrumMethodClass();
        waterfall = new Waterfall();

        setTitle("Bug Tracker");
        setSize(1100,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Bug Tracker System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel tip = new JLabel("Double-click row to update bug", SwingConstants.CENTER);

        JPanel top = new JPanel(new GridLayout(2,1));
        top.add(title);
        top.add(tip);
        add(top,BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "ID","PBI Title","Phase","Bug Title","Description","Severity","Status","Created","Updated","Fast Track","Resolved"
        },0){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);
        table.setRowHeight(25);

        // 🎨 Severity coloring
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean f, int r, int c) {

                // Find column indexes by name
                int sevCol = -1, ftCol = -1;
                for (int i = 0; i < t.getColumnCount(); i++) {
                    if (t.getColumnName(i).equals("Severity"))   sevCol = i;
                    if (t.getColumnName(i).equals("Fast Track")) ftCol  = i;
                }

                // For Fast Track column — return a checkbox instead of text
                if (c == ftCol) {
                    JCheckBox cb = new JCheckBox();
                    cb.setSelected(Boolean.TRUE.equals(val));
                    cb.setHorizontalAlignment(SwingConstants.CENTER);
                    cb.setOpaque(true);

                    // Apply same severity background to keep row color consistent
                    if (!sel) {
                        String sev = sevCol >= 0 ? t.getValueAt(r, sevCol).toString() : "";
                        if      (sev.equals("CRITICAL")) cb.setBackground(new Color(255, 180, 180));
                        else if (sev.equals("MAJOR"))    cb.setBackground(new Color(255, 220, 180));
                        else if (sev.equals("MINOR"))    cb.setBackground(new Color(255, 255, 180));
                        else if (sev.equals("TRIVIAL"))  cb.setBackground(new Color(200, 255, 200));
                        else                             cb.setBackground(Color.WHITE);
                    } else {
                        cb.setBackground(new Color(180, 200, 255));
                    }
                    return cb;
                }

                // All other columns — normal text with severity coloring
                Component comp = super.getTableCellRendererComponent(t, val, sel, f, r, c);
                if (!sel) {
                    String sev = sevCol >= 0 ? t.getValueAt(r, sevCol).toString() : "";
                    if      (sev.equals("CRITICAL")) comp.setBackground(new Color(255, 180, 180));
                    else if (sev.equals("MAJOR"))    comp.setBackground(new Color(255, 220, 180));
                    else if (sev.equals("MINOR"))    comp.setBackground(new Color(255, 255, 180));
                    else if (sev.equals("TRIVIAL"))  comp.setBackground(new Color(200, 255, 200));
                    else                             comp.setBackground(Color.WHITE);
                } else {
                    comp.setBackground(new Color(180, 200, 255));
                }
                return comp;
            }
        });

        add(new JScrollPane(table),BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton create = new JButton("Create Bug");
        JButton Add = new JButton("Add a Bug to Existing PBI");
        JButton refresh = new JButton("Refresh");
        JButton reload = new JButton("Reload Application");

        bottom.add(create);
        bottom.add(Add);
        bottom.add(refresh);
        bottom.add(reload);
        
        add(bottom,BorderLayout.SOUTH);

        refresh.addActionListener(e->refresh());
        create.addActionListener(e->openCreate());
        Add.addActionListener(e->openAddBugToPBI());
        reload.addActionListener(e->{
            dispose();
            SwingUtilities.invokeLater(()->new MainUI().setVisible(true));
        });

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    openEdit(table.getSelectedRow());
                }
            }
        });

        refresh();
    }

    // ================= CREATE =================
    private void openCreate(){

        JDialog d = new JDialog(this,"Create Bug",true);
        d.setSize(400,400);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0,2));
        d.add(panel);

        JComboBox<String> type = new JComboBox<>(new String[]{"Scrum","Waterfall"});

        JTextField pbiName = new JTextField();
        JTextField pbiDesc = new JTextField();

        JTextField title = new JTextField();
        JTextField desc = new JTextField();
        JComboBox<String> severity = new JComboBox<>(new String[]{"CRITICAL","MAJOR","MINOR","TRIVIAL"});
        JCheckBox fast = new JCheckBox();

        JComboBox<String> phase = new JComboBox<>(new String[]{
                "BACKLOG","DESIGN","IMPLEMENTATION","TESTING","DONE"
        });

        panel.add(new JLabel("Type")); panel.add(type);

        panel.add(new JLabel("PBI Name")); panel.add(pbiName);
        panel.add(new JLabel("PBI Description")); panel.add(pbiDesc);

        JButton next = new JButton("Next");
        panel.add(new JLabel()); panel.add(next);

        type.addActionListener(e->{
            panel.removeAll();
            panel.add(new JLabel("Type")); panel.add(type);

            if(type.getSelectedItem().equals("Scrum")){
                panel.add(new JLabel("PBI Name")); panel.add(pbiName);
                panel.add(new JLabel("PBI Description")); panel.add(pbiDesc);
                panel.add(new JLabel()); panel.add(next);
            } else {
                panel.add(new JLabel("Title")); panel.add(title);
                panel.add(new JLabel("Description")); panel.add(desc);
                panel.add(new JLabel("Severity")); panel.add(severity);
                panel.add(new JLabel("Phase")); panel.add(phase);
                panel.add(new JLabel("Fast Track")); panel.add(fast);

                JButton create = new JButton("Create Bug");
                panel.add(new JLabel()); panel.add(create);

                create.addActionListener(ev->{
                    try{
                        Connection conn = db.getConnection();

                        waterfall.createWaterfallBug(conn,
                                phase.getSelectedItem().toString(),
                                title.getText(),
                                desc.getText(),
                                severity.getSelectedItem().toString(),
                                "NEW",
                                fast.isSelected(),
                                null
                        );

                        refresh();
                        d.dispose();

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                });
            }

            panel.revalidate(); panel.repaint();
        });

        next.addActionListener(e->{

            if(pbiName.getText().isEmpty() || pbiDesc.getText().isEmpty()){
                JOptionPane.showMessageDialog(d,"Fill PBI info");
                return;
            }

            panel.removeAll();

            type.setEnabled(false);

            JTextField lockedName = new JTextField(pbiName.getText());
            lockedName.setEditable(false);

            JTextField lockedDesc = new JTextField(pbiDesc.getText());
            lockedDesc.setEditable(false);

            panel.add(new JLabel("Type")); panel.add(type);
            panel.add(new JLabel("PBI Name")); panel.add(lockedName);
            panel.add(new JLabel("PBI Description")); panel.add(lockedDesc);

            panel.add(new JLabel("Title")); panel.add(title);
            panel.add(new JLabel("Description")); panel.add(desc);
            panel.add(new JLabel("Severity")); panel.add(severity);
            panel.add(new JLabel("Fast Track")); panel.add(fast);

            JButton create = new JButton("Create Bug");
            panel.add(new JLabel()); panel.add(create);

            panel.revalidate(); panel.repaint();

            create.addActionListener(ev->{
                try{
                    Connection conn = db.getConnection();

                    scrum.createScrumBug(conn,
                            lockedName.getText(),
                            lockedDesc.getText(),
                            title.getText(),
                            desc.getText(),
                            severity.getSelectedItem().toString(),
                            fast.isSelected(),
                            null
                    );

                    refresh();
                    d.dispose();

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            });
        });

        d.setVisible(true);
    }

    // ================= UPDATE =================
    private void openEdit(int row){

        int id = Integer.parseInt(model.getValueAt(row,0).toString());

        final boolean[] isScrum = {false};
        final Long[] pbiId = {null};
        final String[] existingPhase = {null};
        String existingDesc = "";
       // String existingTitle = model.getValueAt(row,1).toString();
       // String existingSeverity = model.getValueAt(row,2).toString();
        String existingTitle      = model.getValueAt(row, 3).toString(); // Bug Title
        String existingSeverity   = model.getValueAt(row, 5).toString(); // Severity  ← was 4
        String existingStatus     = model.getValueAt(row, 6).toString(); // Status    ← was 5
        boolean existingFastTrack = Boolean.parseBoolean(model.getValueAt(row, 9).toString()); // ← was 8
        //boolean existingFastTrack = Boolean.parseBoolean(model.getValueAt(row,4).toString());

        try{
            // Check if it's a Scrum bug
            for(ScrumMethodClass b : scrum.getScrumBugs(db.getConnection())){
                if(b.getBugId()==id){
                    isScrum[0] = true;
                    existingDesc = b.getDescription();
                    pbiId[0] = b.getPbiId();
                    break;
                }
            }
            if (!isScrum[0]) {
                // Check Waterfall bugs
                for(Waterfall b : waterfall.showWaterfallBugs(db.getConnection())){
                    if(b.getBugId()==id){
                        existingDesc = b.getDescription();
                        existingPhase[0] = b.getPhase();
                        break;
                    }
                }
            }
        }catch(Exception ignored){}

        JDialog d = new JDialog(this,"Update Bug",true);
        d.setSize(400,350);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0,2));
        d.add(panel);

        JTextField title = new JTextField(existingTitle);
        JTextField desc = new JTextField(existingDesc);

        JComboBox<String> severity = new JComboBox<>(new String[]{"CRITICAL","MAJOR","MINOR","TRIVIAL"});
        severity.setSelectedItem(existingSeverity);

        JComboBox<String> status = new JComboBox<>(new String[]{
                "NEW","PLANNED","IN PROGRESS","RESOLVED/CLOSED","TESTED","REJECTED"
        });
        status.setSelectedItem(existingStatus);

        JCheckBox fastTrack = new JCheckBox();
        fastTrack.setSelected(existingFastTrack);

        final JComboBox<String> phase = !isScrum[0] ? new JComboBox<>(new String[]{
                "BACKLOG","DESIGN","IMPLEMENTATION","TESTING","DONE"
        }) : null;
        if (phase != null && existingPhase[0] != null) {
            phase.setSelectedItem(existingPhase[0]);
        }

        JButton update = new JButton("Update");

        panel.add(new JLabel("Title")); panel.add(title);
        panel.add(new JLabel("Description")); panel.add(desc);
        panel.add(new JLabel("Severity")); panel.add(severity);
        panel.add(new JLabel("Status")); panel.add(status);
        panel.add(new JLabel("Fast Track")); panel.add(fastTrack);
        if (!isScrum[0]) {
            panel.add(new JLabel("Phase")); panel.add(phase);
        }
        panel.add(new JLabel()); panel.add(update);

        update.addActionListener(e->{
            try{
                Connection conn = db.getConnection();

                String newTitle = title.getText().isEmpty() ? null : title.getText();
                String newDesc = desc.getText().isEmpty() ? null : desc.getText();
                String newSeverity = (String) severity.getSelectedItem();
                String newStatus = (String) status.getSelectedItem();
                Boolean newFastTrack = fastTrack.isSelected();

                if (isScrum[0]) {
                    scrum.updateScrumBug(
                            conn,
                            id,
                            pbiId[0],
                            newTitle,
                            newDesc,
                            newSeverity,
                            newStatus,
                            newFastTrack
                    );
                } else {
                    String newPhase = phase != null ? (String) phase.getSelectedItem() : null;
                    waterfall.updateWaterfallBug(
                            conn,
                            id,
                            newPhase,
                            newTitle,
                            newDesc,
                            newSeverity,
                            newStatus,
                            newFastTrack,
                            null // externalLink not in UI
                    );
                }

                model.setValueAt(newTitle != null ? newTitle : existingTitle, row, 1);
                model.setValueAt(newSeverity, row, 2);
                model.setValueAt(newStatus, row, 3);
                model.setValueAt(newFastTrack, row, 4);

                refresh();
                d.dispose();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        });

        d.setVisible(true);
    }

    private void refresh(){

        model.setRowCount(0);

        // Update column headers to include Description
        model.setColumnIdentifiers(new String[]{
                "ID","PBI Title","Phase","Bug Title","Description","Severity","Status","Created","Updated","Fast Track","Resolved"
        });

        List<ScrumMethodClass> scrumBugs = scrum.getScrumBugs(db.getConnection());
        List<Waterfall> waterfallBugs = waterfall.showWaterfallBugs(db.getConnection());

        // Combine all rows into one list so we can sort fast track first
        java.util.List<Object[]> allRows = new java.util.ArrayList<>();

        for(ScrumMethodClass b : scrumBugs){
            String pbiName = "N/A";
            try {
                if (b.getPbiId() != null) {
                    Connection conn = db.getConnection();
                    String sql = "SELECT name FROM product_backlog_items WHERE id = ?";
                    java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setLong(1, b.getPbiId());
                    java.sql.ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) pbiName = rs.getString("name");
                }
            } catch (Exception e) { e.printStackTrace(); }

            allRows.add(new Object[]{
                    b.getBugId(),
                    pbiName,
                    "",                   // no phase for Scrum
                    b.getTitle(),
                    b.getDescription(),   // ← Description
                    b.getSeverity(),
                    b.getStatus(),
                    b.getCreatedAt(),
                    b.getUpdatedAt(),
                    b.isFastTrack(),
                    b.getResolvedAt()
            });
        }

        for(Waterfall b : waterfallBugs){
            allRows.add(new Object[]{
                    b.getBugId(),
                    "N/A",
                    b.getPhase(),
                    b.getTitle(),
                    b.getDescription(),   // ← Description
                    b.getSeverity(),
                    b.getStatus(),
                    b.getCreatedAt(),
                    b.getUpdatedAt(),
                    b.isFastTrack(),
                    b.getResolvedAt()
            });
        }

        // Sort: fast track true rows come first
        allRows.sort((a, b) -> Boolean.compare((Boolean) b[9], (Boolean) a[9]));

        for(Object[] row : allRows){
            model.addRow(row);
        }
    }


    // ================= ADD BUG TO EXISTING PBI =================
    private void openAddBugToPBI() {
        JDialog d = new JDialog(this, "Add Bug to Existing PBI", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        d.add(panel);

        JLabel selectPBI = new JLabel("Select PBI:");
        JComboBox<String> pbiList = new JComboBox<>();
        // Populate PBI list from the database
        try {
            Connection conn = db.getConnection();
            List<String> pbis = scrum.getAllPbiNames(conn);
            for (String pbi : pbis) {
                pbiList.addItem(pbi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel bugTitle = new JLabel("Bug Title:");
        JTextField bugTitleField = new JTextField();

        JLabel bugDesc = new JLabel("Bug Description:");
        JTextField bugDescField = new JTextField();

        JLabel severity = new JLabel("Severity:");
        JComboBox<String> severityList = new JComboBox<>(new String[]{"CRITICAL", "MAJOR", "MINOR", "TRIVIAL"});

        JLabel fastTrack = new JLabel("Fast Track:");
        JCheckBox fastTrackBox = new JCheckBox();

        JButton addButton = new JButton("Add Bug");

        panel.add(selectPBI);
        panel.add(pbiList);
        panel.add(bugTitle);
        panel.add(bugTitleField);
        panel.add(bugDesc);
        panel.add(bugDescField);
        panel.add(severity);
        panel.add(severityList);
        panel.add(fastTrack);
        panel.add(fastTrackBox);
        panel.add(new JLabel());
        panel.add(addButton);

        addButton.addActionListener(e -> {
            try {
                Connection conn = db.getConnection();
                String selectedName = (String) pbiList.getSelectedItem();

                if (selectedName == null) {
                    JOptionPane.showMessageDialog(d, "Select a PBI");
                    return;
                }

                if (bugTitleField.getText().isEmpty() || bugDescField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Please fill in all bug fields.");
                    return;
                }

                // Look up the existing PBI id by name
                String sql = "SELECT id FROM product_backlog_items WHERE name = ? LIMIT 1";
                try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, selectedName);
                    java.sql.ResultSet rs = pstmt.executeQuery();

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(d, "PBI not found.");
                        return;
                    }

                    int pbiId = rs.getInt("id");

                    // Insert bug linked to the EXISTING pbi_id — no new PBI created
                    scrum.insertBug(
                            conn,
                            bugTitleField.getText(),
                            bugDescField.getText(),
                            (String) severityList.getSelectedItem(),
                            fastTrackBox.isSelected(),
                            null,   // externalLink
                            pbiId,  // existing PBI id
                            null    // no phase for Scrum
                    );
                }

                refresh();
                d.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });
        d.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new MainUI().setVisible(true));
    }
}


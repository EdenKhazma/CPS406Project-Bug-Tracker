package com.cps.ui;

import com.cps.bugtracker.DatabaseTables;
import com.cps.bugtracker.ScrumMethodClass;

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

    public MainUI() {

        db = new DatabaseTables();
        db.CreateTables();
        scrum = new ScrumMethodClass();

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
                "ID","Title","Severity","Status","Fast Track","Created","Updated","Resolved"
        },0){
            public boolean isCellEditable(int r,int c){return false;}
        };

        table = new JTable(model);
        table.setRowHeight(25);

        // 🎨 Severity coloring
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean f,int r,int c){
                Component comp = super.getTableCellRendererComponent(t,val,sel,f,r,c);

                if(!sel){
                    String sev = t.getValueAt(r,2).toString();

                    if(sev.equals("CRITICAL")) comp.setBackground(new Color(255,180,180));
                    else if(sev.equals("MAJOR")) comp.setBackground(new Color(255,220,180));
                    else if(sev.equals("MINOR")) comp.setBackground(new Color(255,255,180));
                    else comp.setBackground(new Color(200,255,200));
                } else {
                    comp.setBackground(new Color(180,200,255));
                }

                return comp;
            }
        });

        add(new JScrollPane(table),BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton create = new JButton("Create Bug");
        JButton refresh = new JButton("Refresh");

        bottom.add(create);
        bottom.add(refresh);
        add(bottom,BorderLayout.SOUTH);

        refresh.addActionListener(e->refresh());
        create.addActionListener(e->openCreate());

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

                        scrum.createScrumBug(conn,
                                "Waterfall","Waterfall",
                                title.getText(),
                                desc.getText(),
                                severity.getSelectedItem().toString(),
                                fast.isSelected(),
                                phase.getSelectedItem().toString()
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

        String existingDesc = "";
        Long tempPbiId = 0L;

        try{
            for(ScrumMethodClass b : scrum.getScrumBugs(db.getConnection())){
                if(b.getBugId()==id){
                    existingDesc = b.getDescription();
                    tempPbiId = Long.valueOf(b.getPbiId());
                }
            }
        }catch(Exception ignored){}

        final Long pbiId = tempPbiId;

        JDialog d = new JDialog(this,"Update Bug",true);
        d.setSize(400,300);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0,2));
        d.add(panel);

        JTextField title = new JTextField(model.getValueAt(row,1).toString());
        JTextField desc = new JTextField(existingDesc);

        JComboBox<String> severity = new JComboBox<>(new String[]{"CRITICAL","MAJOR","MINOR","TRIVIAL"});
        JComboBox<String> status = new JComboBox<>(new String[]{
                "NEW","PLANNED","IN PROGRESS","RESOLVED","TESTED","CLOSED","REJECTED"
        });

        // ✅ FIX: set correct values
        severity.setSelectedItem(model.getValueAt(row, 2).toString());
        status.setSelectedItem(model.getValueAt(row, 3).toString());

        JButton update = new JButton("Update");

        panel.add(new JLabel("Title")); panel.add(title);
        panel.add(new JLabel("Description")); panel.add(desc);
        panel.add(new JLabel("Severity")); panel.add(severity);
        panel.add(new JLabel("Status")); panel.add(status);
        panel.add(new JLabel()); panel.add(update);

        update.addActionListener(e->{
            try{
                Connection conn = db.getConnection();

                String newTitle = title.getText();
                String newDesc = desc.getText();
                String newSeverity = severity.getSelectedItem().toString();
                String newStatus = status.getSelectedItem().toString();

                scrum.updateScrumBug(
                        conn,
                        id,
                        pbiId,
                        newTitle,
                        newDesc,
                        newSeverity,
                        newStatus,
                        false
                );

                model.setValueAt(newTitle, row, 1);
                model.setValueAt(newSeverity, row, 2);
                model.setValueAt(newStatus, row, 3);

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

        List<ScrumMethodClass> bugs = scrum.getScrumBugs(db.getConnection());

        for(ScrumMethodClass b:bugs){
            model.addRow(new Object[]{
                    b.getBugId(),
                    b.getTitle(),
                    b.getSeverity(),
                    b.getStatus(),
                    b.isFastTrack(),
                    b.getCreatedAt(),
                    b.getUpdatedAt(),
                    b.getResolvedAt()
            });
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new MainUI().setVisible(true));
    }
}
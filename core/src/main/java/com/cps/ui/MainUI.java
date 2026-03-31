package com.cps.ui;

import com.cps.bugtracker.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.List;

public class MainUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;

    private Connection conn;
    private ScrumMethodClass scrum = new ScrumMethodClass();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI().initUI());
    }

    private void initUI() {
        DatabaseTables db = new DatabaseTables();
        db.CreateConnection();
        conn = db.getConnection();

        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database failed to connect.");
            return;
        }

        db.CreateTables();

        frame = new JFrame("Bug Tracker");
        frame.setSize(950, 500);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Bug Tracker System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Severity", "Status", "Fast Track", "Created"},
                0
        ) {
            public boolean isCellEditable(int row, int col) {
                return col == 1; // ONLY title editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);

        // ===== SEVERITY COLOR RENDERER =====
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col
                );

                String severity = table.getValueAt(row, 2).toString();
                Color bg = getSeverityColor(severity, isSelected);

                c.setBackground(bg);
                c.setForeground(isSelected ? Color.WHITE : Color.BLACK);

                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        frame.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton createBtn = new JButton("Create Bug");
        JButton refreshBtn = new JButton("Refresh");

        bottom.add(createBtn);
        bottom.add(refreshBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> openCreateBugPopup());
        refreshBtn.addActionListener(e -> loadData());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int bugId = (int) tableModel.getValueAt(row, 0);
                    openBugDetails(bugId);
                }
            }
        });

        loadData();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // ===== COLOR LOGIC =====
    private Color getSeverityColor(String severity, boolean selected) {
        Color color;

        switch (severity) {
            case "CRITICAL":
                color = new Color(255, 182, 182);
                break;
            case "MAJOR":
                color = new Color(255, 204, 153);
                break;
            case "MINOR":
                color = new Color(255, 255, 153);
                break;
            case "TRIVIAL":
                color = new Color(204, 255, 204);
                break;
            default:
                color = Color.LIGHT_GRAY;
        }

        if (selected) {
            color = color.darker();
        }

        return color;
    }

    // ===== CREATE BUG POPUP =====
    private void openCreateBugPopup() {

        JFrame popup = new JFrame("Create Bug");
        popup.setSize(450, 420);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Scrum", "Waterfall"});
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();

        JComboBox<String> severityBox = new JComboBox<>(
                new String[]{"CRITICAL", "MAJOR", "MINOR", "TRIVIAL"}
        );

        JCheckBox fastTrackBox = new JCheckBox();

        JTextField pbiName = new JTextField();
        JTextField pbiDesc = new JTextField();

        JComboBox<String> phaseBox = new JComboBox<>(
                new String[]{"REQUIREMENTS", "DESIGN", "IMPLEMENTATION", "TESTING", "DEPLOYMENT"}
        );

        JPanel dynamicPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JButton createBtn = new JButton("Create");

        form.add(new JLabel("Type"));
        form.add(typeBox);

        form.add(new JLabel("Title"));
        form.add(titleField);

        form.add(new JLabel("Description"));
        form.add(descField);

        form.add(new JLabel("Severity"));
        form.add(severityBox);

        form.add(new JLabel("Fast Track"));
        form.add(fastTrackBox);

        form.add(dynamicPanel);
        form.add(createBtn);

        Runnable updateDynamic = () -> {
            dynamicPanel.removeAll();

            if (typeBox.getSelectedItem().equals("Scrum")) {
                dynamicPanel.add(new JLabel("PBI Name"));
                dynamicPanel.add(pbiName);
                dynamicPanel.add(new JLabel("PBI Description"));
                dynamicPanel.add(pbiDesc);
            } else {
                dynamicPanel.add(new JLabel("Phase"));
                dynamicPanel.add(phaseBox);
                dynamicPanel.add(new JLabel());
                dynamicPanel.add(new JLabel());
            }

            dynamicPanel.revalidate();
            dynamicPanel.repaint();
        };

        typeBox.addActionListener(e -> updateDynamic.run());
        updateDynamic.run();

        createBtn.addActionListener(e -> {
            if (typeBox.getSelectedItem().equals("Scrum")) {
                scrum.createScrumBug(
                        conn,
                        pbiName.getText(),
                        pbiDesc.getText(),
                        titleField.getText(),
                        descField.getText(),
                        severityBox.getSelectedItem().toString(),
                        fastTrackBox.isSelected(),
                        null
                );
            } else {
                Waterfall wf = new Waterfall();
                wf.createWaterfallBug(
                        conn,
                        phaseBox.getSelectedItem().toString(),
                        titleField.getText(),
                        descField.getText(),
                        severityBox.getSelectedItem().toString(),
                        "NEW",
                        fastTrackBox.isSelected(),
                        null
                );
            }

            loadData();
            popup.dispose();
        });

        popup.add(form);
        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }

    // ===== LOAD TABLE =====
    private void loadData() {
        tableModel.setRowCount(0);

        List<ScrumMethodClass> bugs = scrum.getScrumBugs(conn);

        for (ScrumMethodClass bug : bugs) {
            tableModel.addRow(new Object[]{
                    bug.getBugId(),
                    bug.getTitle(),
                    bug.getSeverity(),
                    bug.getStatus(),
                    bug.isFastTrack(),
                    bug.getCreatedAt()
            });
        }
    }

    // ===== DOUBLE CLICK DETAILS =====
    private void openBugDetails(int bugId) {
        JFrame popup = new JFrame("Bug Details");
        popup.setSize(400, 350);
        popup.setLayout(new GridLayout(7, 2));

        List<ScrumMethodClass> bugs = scrum.getScrumBugs(conn);
        ScrumMethodClass selected = null;

        for (ScrumMethodClass b : bugs) {
            if (b.getBugId() == bugId) selected = b;
        }

        if (selected == null) return;

        JTextField title = new JTextField(selected.getTitle());
        JTextField desc = new JTextField(selected.getDescription());

        JComboBox<String> severity = new JComboBox<>(
                new String[]{"CRITICAL", "MAJOR", "MINOR", "TRIVIAL"}
        );
        severity.setSelectedItem(selected.getSeverity());

        JComboBox<String> status = new JComboBox<>(
                new String[]{"NEW", "IN_PROGRESS", "RESOLVED", "CLOSED"}
        );
        status.setSelectedItem(selected.getStatus());

        JCheckBox fastTrack = new JCheckBox();
        fastTrack.setSelected(selected.isFastTrack());

        popup.add(new JLabel("Title")); popup.add(title);
        popup.add(new JLabel("Description")); popup.add(desc);
        popup.add(new JLabel("Severity")); popup.add(severity);
        popup.add(new JLabel("Status")); popup.add(status);
        popup.add(new JLabel("Fast Track")); popup.add(fastTrack);

        JButton updateBtn = new JButton("Update Bug");
        popup.add(new JLabel());
        popup.add(updateBtn);

        ScrumMethodClass finalSelected = selected;

        updateBtn.addActionListener(e -> {
            scrum.updateScrumBug(
                    conn,
                    finalSelected.getBugId(),
                    finalSelected.getPbiId(),
                    title.getText(),
                    desc.getText(),
                    severity.getSelectedItem().toString(),
                    status.getSelectedItem().toString(),
                    fastTrack.isSelected()
            );

            loadData();
            popup.dispose();
        });

        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }
}
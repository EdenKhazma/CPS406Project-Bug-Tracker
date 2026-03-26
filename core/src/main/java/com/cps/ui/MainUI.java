package com.cps.ui;

import com.cps.bugtracker.DatabaseTables;
import com.cps.bugtracker.ScrumMethodClass;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainUI extends JFrame {

    private DatabaseTables db;
    private ScrumMethodClass scrum;
    private DefaultTableModel tableModel;
    private JTable bugTable;

    public MainUI() {

        db = new DatabaseTables();
        db.CreateTables();
        scrum = new ScrumMethodClass();

        setTitle("BTR - Software Bug Tracker");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== TITLE =====
        JLabel title = new JLabel("BTR - SOFTWARE BUG TRACKER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton createPBIButton = new JButton("Create PBI");
        JButton createBugButton = new JButton("Create Bug");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(createPBIButton);
        buttonPanel.add(createBugButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== TABLE =====
        String[] columns = {
                "Bug ID",
                "PBI ID",
                "Title",
                "Description",
                "Severity",
                "Status",
                "Phase",
                "Fast Track",
                "Created",
                "Updated",
                "Resolved"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bugTable = new JTable(tableModel);
        bugTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bugTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOLD BUG ID =====
        bugTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        // ===== COLOR SEVERITY =====
        bugTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (value != null) {
                    switch (value.toString()) {
                        case "CRITICAL":
                            c.setForeground(Color.RED);
                            break;
                        case "MAJOR":
                            c.setForeground(new Color(255, 140, 0));
                            break;
                        case "MINOR":
                            c.setForeground(Color.BLUE);
                            break;
                        default:
                            c.setForeground(Color.GRAY);
                    }
                }

                return c;
            }
        });

        refreshTable();

        // ===== CREATE PBI =====
        createPBIButton.addActionListener(e -> {

            String name = JOptionPane.showInputDialog(this, "PBI Name:");
            if (name == null || name.trim().isEmpty()) return;

            String description = JOptionPane.showInputDialog(this, "PBI Description:");
            if (description == null || description.trim().isEmpty()) return;

            int id = scrum.createPBI(db.getConnection(), name, description);

            if (id > 0) {
                JOptionPane.showMessageDialog(this, "PBI Created! ID: " + id);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create PBI.");
            }
        });

        // ===== CREATE BUG (FIXED FLOW) =====
        createBugButton.addActionListener(e -> {

            String pbiName = JOptionPane.showInputDialog(this, "Existing PBI Name:");
            if (pbiName == null || pbiName.trim().isEmpty()) return;

            String pbiDesc = JOptionPane.showInputDialog(this, "Existing PBI Description:");
            if (pbiDesc == null || pbiDesc.trim().isEmpty()) return;

            String bugTitle = JOptionPane.showInputDialog(this, "Bug Title:");
            if (bugTitle == null || bugTitle.trim().isEmpty()) return;

            String bugDesc = JOptionPane.showInputDialog(this, "Bug Description:");
            if (bugDesc == null || bugDesc.trim().isEmpty()) return;

            String[] severityOptions = {"CRITICAL", "MAJOR", "MINOR", "TRIVIAL"};
            String severity = (String) JOptionPane.showInputDialog(
                    this,
                    "Severity:",
                    "Select Severity",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    severityOptions,
                    severityOptions[1]
            );

            if (severity == null) return;

            int fastTrackOption = JOptionPane.showConfirmDialog(
                    this,
                    "Fast Track? (Emergency Fix)",
                    "Fast Track",
                    JOptionPane.YES_NO_OPTION
            );

            boolean fastTrack = (fastTrackOption == JOptionPane.YES_OPTION);

            scrum.createScrumBug(
                    db.getConnection(),
                    pbiName,
                    pbiDesc,
                    bugTitle,
                    bugDesc,
                    severity,
                    fastTrack,
                    null
            );

            refreshTable();
        });

        refreshButton.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {

        tableModel.setRowCount(0);

        List<ScrumMethodClass> bugs = scrum.getScrumBugs(db.getConnection());

        for (ScrumMethodClass bug : bugs) {

            Object[] row = {
                    bug.getBugId(),
                    bug.getPbiId(),
                    bug.getTitle(),
                    bug.getDescription(),
                    bug.getSeverity(),
                    bug.getStatus(),
                    bug.getPhase() == null ? "BACKLOG" : bug.getPhase(),
                    bug.isFastTrack(),
                    bug.getCreatedAt(),
                    bug.getUpdatedAt(),
                    bug.getResolvedAt()
            };

            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }
}
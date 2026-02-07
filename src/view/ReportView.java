package view;

import controller.AssignmentController;
import controller.CitizenController;
import model.Assignment;
import model.Citizen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View สำหรับรายงานผล - ประชาชนที่ได้รับการจัดสรรและที่ตกค้าง
 */
public class ReportView extends JPanel {
    private CitizenController citizenController;
    private AssignmentController assignmentController;

    // UI Components
    private JTable assignedTable;
    private DefaultTableModel assignedTableModel;
    private JTable unassignedTable;
    private DefaultTableModel unassignedTableModel;
    private JLabel summaryLabel;

    public ReportView(CitizenController citizenController,
            AssignmentController assignmentController) {
        this.citizenController = citizenController;
        this.assignmentController = assignmentController;
        initComponents();
        loadReport();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ส่วนสรุป
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);

        // ส่วนตาราง (แบ่งเป็น 2 ส่วน)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);

        // ส่วนประชาชนที่ได้รับการจัดสรร
        JPanel assignedPanel = createAssignedPanel();
        splitPane.setTopComponent(assignedPanel);

        // ส่วนประชาชนที่ตกค้าง
        JPanel unassignedPanel = createUnassignedPanel();
        splitPane.setBottomComponent(unassignedPanel);

        add(splitPane, BorderLayout.CENTER);

        // ปุ่มรีเฟรช
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("รีเฟรชรายงาน");
        refreshButton.addActionListener(e -> loadReport());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("สรุปภาพรวม"));

        summaryLabel = new JLabel("กำลังโหลดข้อมูล...", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(summaryLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAssignedPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("ประชาชนที่ได้รับการจัดสรรแล้ว"));

        String[] columns = { "ID", "ชื่อ", "อายุ", "ประเภท", "ศูนย์พักพิง", "วันที่จัดสรร", "หมายเหตุ" };
        assignedTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedTable = new JTable(assignedTableModel);
        assignedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(assignedTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUnassignedPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("ประชาชนที่ยังไม่ได้รับการจัดสรร (ตกค้าง)"));

        String[] columns = { "ID", "ชื่อ", "อายุ", "สภาวะสุขภาพ", "ประเภท", "วันที่ลงทะเบียน", "ลำดับความสำคัญ" };
        unassignedTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        unassignedTable = new JTable(unassignedTableModel);
        unassignedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // เปลี่ยนสีพื้นหลังเพื่อเน้นความสำคัญ
        unassignedTable.setBackground(new Color(255, 240, 240));

        JScrollPane scrollPane = new JScrollPane(unassignedTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadReport() {
        loadAssignedCitizens();
        loadUnassignedCitizens();
        updateSummary();
    }

    private void loadAssignedCitizens() {
        assignedTableModel.setRowCount(0);
        List<Assignment> assignments = assignmentController.getAllAssignmentsWithDetails();

        for (Assignment assignment : assignments) {
            Citizen citizen = assignment.getCitizen();
            if (citizen != null) {
                Object[] row = {
                        citizen.getId(),
                        citizen.getName(),
                        citizen.getAge(),
                        citizen.getType().getDisplayName(),
                        assignment.getShelter() != null ? assignment.getShelter().getName() : "N/A",
                        assignment.getAssignmentDate(),
                        assignment.getNotes()
                };
                assignedTableModel.addRow(row);
            }
        }
    }

    private void loadUnassignedCitizens() {
        unassignedTableModel.setRowCount(0);
        List<Citizen> unassignedCitizens = citizenController.getUnassignedCitizens();

        for (Citizen citizen : unassignedCitizens) {
            String priority = citizen.isPriorityGroup() ? "⚠ สูง (เด็ก/ผู้สูงอายุ)" : "ปกติ";
            String healthStatus = citizen.getHealthCondition() != null &&
                    !citizen.getHealthCondition().isEmpty()
                            ? citizen.getHealthCondition()
                            : "ไม่ระบุ";

            Object[] row = {
                    citizen.getId(),
                    citizen.getName(),
                    citizen.getAge(),
                    healthStatus,
                    citizen.getType().getDisplayName(),
                    citizen.getRegistrationDate(),
                    priority
            };
            unassignedTableModel.addRow(row);
        }
    }

    private void updateSummary() {
        int totalCitizens = citizenController.getAllCitizens().size();
        int assignedCount = assignedTableModel.getRowCount();
        int unassignedCount = unassignedTableModel.getRowCount();

        double assignedPercentage = totalCitizens > 0
                ? (assignedCount * 100.0 / totalCitizens)
                : 0;

        // นับกลุ่มความสำคัญในคนที่ยังไม่ได้รับการจัดสรร
        int priorityCount = 0;
        List<Citizen> unassignedCitizens = citizenController.getUnassignedCitizens();
        for (Citizen citizen : unassignedCitizens) {
            if (citizen.isPriorityGroup()) {
                priorityCount++;
            }
        }

        String summaryText = String.format(
                "<html><center>" +
                        "<b>สรุป:</b> ประชาชนทั้งหมด %d คน | " +
                        "<font color='green'>ได้รับการจัดสรร %d คน (%.1f%%)</font> | " +
                        "<font color='red'>ยังไม่ได้รับการจัดสรร %d คน (%.1f%%)</font>" +
                        (priorityCount > 0 ? " | <font color='orange'>⚠ กลุ่มเด็ก/ผู้สูงอายุรอ %d คน</font>" : "") +
                        "</center></html>",
                totalCitizens,
                assignedCount,
                assignedPercentage,
                unassignedCount,
                100 - assignedPercentage,
                priorityCount);

        summaryLabel.setText(summaryText);
    }
}

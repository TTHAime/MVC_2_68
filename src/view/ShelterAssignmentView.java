package view;

import controller.AssignmentController;
import controller.CitizenController;
import controller.ShelterController;
import model.Assignment;
import model.Citizen;
import model.Shelter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * View สำหรับจัดสรรที่พักพิง
 */
public class ShelterAssignmentView extends JPanel {
    private ShelterController shelterController;
    private CitizenController citizenController;
    private AssignmentController assignmentController;

    // UI Components
    private JTable shelterTable;
    private DefaultTableModel shelterTableModel;
    private JTable unassignedCitizenTable;
    private DefaultTableModel unassignedTableModel;
    private JTable assignedTable;
    private DefaultTableModel assignedTableModel;

    public ShelterAssignmentView(ShelterController shelterController,
            CitizenController citizenController,
            AssignmentController assignmentController) {
        this.shelterController = shelterController;
        this.citizenController = citizenController;
        this.assignmentController = assignmentController;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // แบ่ง layout เป็น 2 ส่วน: ซ้าย (ศูนย์พักพิงและประชาชน) และขวา (การจัดสรร)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);

        // ส่วนซ้าย
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        // ส่วนขวา
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        // ส่วนศูนย์พักพิง
        JPanel shelterPanel = new JPanel(new BorderLayout(5, 5));
        shelterPanel.setBorder(BorderFactory.createTitledBorder("ศูนย์พักพิง - รายละเอียด"));

        String[] shelterColumns = { "ID", "ชื่อ", "จำนวนคน", "ความจุ", "ที่ว่าง", "ความเสี่ยง", "สถานะ" };
        shelterTableModel = new DefaultTableModel(shelterColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        shelterTable = new JTable(shelterTableModel);
        shelterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane shelterScrollPane = new JScrollPane(shelterTable);
        shelterPanel.add(shelterScrollPane, BorderLayout.CENTER);

        JButton refreshShelterBtn = new JButton("รีเฟรช");
        refreshShelterBtn.addActionListener(e -> loadShelters());
        shelterPanel.add(refreshShelterBtn, BorderLayout.SOUTH);

        panel.add(shelterPanel);

        // ส่วนประชาชนที่ยังไม่ได้รับการจัดสรร
        JPanel unassignedPanel = new JPanel(new BorderLayout(5, 5));
        unassignedPanel.setBorder(BorderFactory.createTitledBorder("ประชาชนที่รอการจัดสรร"));

        String[] citizenColumns = { "ID", "ชื่อ", "อายุ", "ประเภท", "สุขภาพ", "ลำดับความสำคัญ" };
        unassignedTableModel = new DefaultTableModel(citizenColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        unassignedCitizenTable = new JTable(unassignedTableModel);
        unassignedCitizenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane citizenScrollPane = new JScrollPane(unassignedCitizenTable);
        unassignedPanel.add(citizenScrollPane, BorderLayout.CENTER);

        // ปุ่มจัดสรร
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton autoAssignBtn = new JButton("จัดสรรอัตโนมัติ");
        autoAssignBtn.addActionListener(e -> autoAssign());
        btnPanel.add(autoAssignBtn);

        JButton manualAssignBtn = new JButton("จัดสรรด้วยตัวเอง");
        manualAssignBtn.addActionListener(e -> manualAssign());
        btnPanel.add(manualAssignBtn);

        JButton refreshCitizenBtn = new JButton("รีเฟรช");
        refreshCitizenBtn.addActionListener(e -> loadUnassignedCitizens());
        btnPanel.add(refreshCitizenBtn);

        unassignedPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(unassignedPanel);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("การจัดสรรปัจจุบัน"));

        String[] assignedColumns = { "ID", "ชื่อประชาชน", "ศูนย์พักพิง", "วันที่", "หมายเหตุ" };
        assignedTableModel = new DefaultTableModel(assignedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedTable = new JTable(assignedTableModel);
        assignedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(assignedTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("รีเฟรช");
        refreshBtn.addActionListener(e -> loadAssignments());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void loadData() {
        loadShelters();
        loadUnassignedCitizens();
        loadAssignments();
    }

    private void loadShelters() {
        shelterTableModel.setRowCount(0);
        List<Shelter> shelters = shelterController.getAllShelters();

        for (Shelter shelter : shelters) {
            String status = shelter.isFull() ? "เต็ม" : "ว่าง";
            Object[] row = {
                    shelter.getId(),
                    shelter.getName(),
                    shelter.getCurrentOccupancy(),
                    shelter.getMaxCapacity(),
                    shelter.getAvailableSpace(),
                    shelter.getRiskLevel().getDisplayName(),
                    status
            };
            shelterTableModel.addRow(row);
        }
    }

    private void loadUnassignedCitizens() {
        unassignedTableModel.setRowCount(0);
        List<Citizen> citizens = citizenController.getCitizensSortedByPriority();

        for (Citizen citizen : citizens) {
            String priority = citizen.isPriorityGroup() ? "สูง" : "ปกติ";
            String healthStatus = citizen.hasHealthRisk() ? "เสี่ยง" : "ปกติ";

            Object[] row = {
                    citizen.getId(),
                    citizen.getName(),
                    citizen.getAge(),
                    citizen.getType().getDisplayName(),
                    healthStatus,
                    priority
            };
            unassignedTableModel.addRow(row);
        }
    }

    private void loadAssignments() {
        assignedTableModel.setRowCount(0);
        List<Assignment> assignments = assignmentController.getAllAssignmentsWithDetails();

        for (Assignment assignment : assignments) {
            Object[] row = {
                    assignment.getId(),
                    assignment.getCitizen() != null ? assignment.getCitizen().getName() : "N/A",
                    assignment.getShelter() != null ? assignment.getShelter().getName() : "N/A",
                    assignment.getAssignmentDate(),
                    assignment.getNotes()
            };
            assignedTableModel.addRow(row);
        }
    }

    private void autoAssign() {
        int selectedRow = unassignedCitizenTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "กรุณาเลือกประชาชนที่ต้องการจัดสรร",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citizenId = (Integer) unassignedTableModel.getValueAt(selectedRow, 0);

        try {
            int assignmentId = assignmentController.autoAssignShelter(citizenId);

            JOptionPane.showMessageDialog(this,
                    "จัดสรรสำเร็จ! Assignment ID: " + assignmentId,
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);

            loadData();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาด: " + ex.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manualAssign() {
        int selectedCitizenRow = unassignedCitizenTable.getSelectedRow();
        int selectedShelterRow = shelterTable.getSelectedRow();

        if (selectedCitizenRow == -1 || selectedShelterRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "กรุณาเลือกทั้งประชาชนและศูนย์พักพิง",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citizenId = (Integer) unassignedTableModel.getValueAt(selectedCitizenRow, 0);
        int shelterId = (Integer) shelterTableModel.getValueAt(selectedShelterRow, 0);

        try {
            int assignmentId = assignmentController.assignShelter(citizenId, shelterId);

            JOptionPane.showMessageDialog(this,
                    "จัดสรรสำเร็จ! Assignment ID: " + assignmentId,
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);

            loadData();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาด: " + ex.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "ข้อมูลไม่ถูกต้อง",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}

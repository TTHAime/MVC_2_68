package view;

import controller.CitizenController;
import model.Citizen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * View สำหรับลงทะเบียนและแสดงรายการประชาชน
 */
public class CitizenRegistrationView extends JPanel {
    private CitizenController citizenController;

    // UI Components
    private JTextField nameField;
    private JSpinner ageSpinner;
    private JTextArea healthConditionArea;
    private JComboBox<String> typeComboBox;
    private JTable citizenTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;

    public CitizenRegistrationView(CitizenController citizenController) {
        this.citizenController = citizenController;
        initComponents();
        loadCitizens();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ส่วนฟอร์มลงทะเบียน
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // ส่วนแสดงรายการประชาชน
        JPanel listPanel = createListPanel();
        add(listPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("ลงทะเบียนประชาชน"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ชื่อ
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ชื่อ:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // อายุ
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("อายุ:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        ageSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
        panel.add(ageSpinner, gbc);

        // สภาวะสุขภาพ
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("สภาวะสุขภาพ:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        healthConditionArea = new JTextArea(3, 20);
        healthConditionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(healthConditionArea);
        panel.add(scrollPane, gbc);

        // ประเภท
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("ประเภท:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        typeComboBox = new JComboBox<>(new String[] {
                Citizen.CitizenType.GENERAL.getDisplayName(),
                Citizen.CitizenType.AT_RISK.getDisplayName(),
                Citizen.CitizenType.VIP.getDisplayName()
        });
        panel.add(typeComboBox, gbc);

        // ปุ่มลงทะเบียน
        gbc.gridx = 1;
        gbc.gridy = 4;
        JButton registerButton = new JButton("ลงทะเบียน");
        registerButton.addActionListener(e -> registerCitizen());
        panel.add(registerButton, gbc);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("รายการประชาชนทั้งหมด"));

        // ส่วนกรอง
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("กรองตามประเภท:"));

        filterComboBox = new JComboBox<>(new String[] {
                "ทั้งหมด",
                Citizen.CitizenType.GENERAL.getDisplayName(),
                Citizen.CitizenType.AT_RISK.getDisplayName(),
                Citizen.CitizenType.VIP.getDisplayName()
        });
        filterComboBox.addActionListener(e -> filterCitizens());
        filterPanel.add(filterComboBox);

        JButton refreshButton = new JButton("รีเฟรช");
        refreshButton.addActionListener(e -> loadCitizens());
        filterPanel.add(refreshButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // ตาราง
        String[] columnNames = { "ID", "ชื่อ", "อายุ", "สภาวะสุขภาพ", "วันที่ลงทะเบียน", "ประเภท" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        citizenTable = new JTable(tableModel);
        citizenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(citizenTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void registerCitizen() {
        try {
            String name = nameField.getText().trim();
            int age = (Integer) ageSpinner.getValue();
            String healthCondition = healthConditionArea.getText().trim();

            // แปลง String เป็น CitizenType
            String selectedType = (String) typeComboBox.getSelectedItem();
            Citizen.CitizenType type = Citizen.CitizenType.GENERAL;

            for (Citizen.CitizenType ct : Citizen.CitizenType.values()) {
                if (ct.getDisplayName().equals(selectedType)) {
                    type = ct;
                    break;
                }
            }

            // ลงทะเบียน
            int id = citizenController.registerCitizen(name, age, healthCondition, type);

            JOptionPane.showMessageDialog(this,
                    "ลงทะเบียนสำเร็จ! ID: " + id,
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            nameField.setText("");
            ageSpinner.setValue(30);
            healthConditionArea.setText("");
            typeComboBox.setSelectedIndex(0);

            // Reload table
            loadCitizens();

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

    private void loadCitizens() {
        tableModel.setRowCount(0);
        List<Citizen> citizens = citizenController.getAllCitizens();

        for (Citizen citizen : citizens) {
            Object[] row = {
                    citizen.getId(),
                    citizen.getName(),
                    citizen.getAge(),
                    citizen.getHealthCondition(),
                    citizen.getRegistrationDate(),
                    citizen.getType().getDisplayName()
            };
            tableModel.addRow(row);
        }
    }

    private void filterCitizens() {
        tableModel.setRowCount(0);
        String selectedFilter = (String) filterComboBox.getSelectedItem();

        List<Citizen> citizens;

        if ("ทั้งหมด".equals(selectedFilter)) {
            citizens = citizenController.getAllCitizens();
        } else {
            Citizen.CitizenType type = null;
            for (Citizen.CitizenType ct : Citizen.CitizenType.values()) {
                if (ct.getDisplayName().equals(selectedFilter)) {
                    type = ct;
                    break;
                }
            }
            citizens = citizenController.getCitizensByType(type);
        }

        for (Citizen citizen : citizens) {
            Object[] row = {
                    citizen.getId(),
                    citizen.getName(),
                    citizen.getAge(),
                    citizen.getHealthCondition(),
                    citizen.getRegistrationDate(),
                    citizen.getType().getDisplayName()
            };
            tableModel.addRow(row);
        }
    }
}

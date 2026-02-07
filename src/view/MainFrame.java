package view;

import controller.AssignmentController;
import controller.CitizenController;
import controller.ShelterController;

import javax.swing.*;
import java.awt.*;

/**
 * Main Frame สำหรับแอปพลิเคชัน
 */
public class MainFrame extends JFrame {
    private CitizenController citizenController;
    private ShelterController shelterController;
    private AssignmentController assignmentController;

    private JTabbedPane tabbedPane;

    public MainFrame(CitizenController citizenController,
            ShelterController shelterController,
            AssignmentController assignmentController) {
        this.citizenController = citizenController;
        this.shelterController = shelterController;
        this.assignmentController = assignmentController;

        initComponents();
    }

    private void initComponents() {
        setTitle("ระบบจัดสรรที่พักพิงในสถานการณ์ฉุกเฉิน");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // สร้าง Tabbed Pane สำหรับแยก 3 Views
        tabbedPane = new JTabbedPane();

        // View 1: ลงทะเบียนประชาชน
        CitizenRegistrationView citizenView = new CitizenRegistrationView(citizenController);
        tabbedPane.addTab("ลงทะเบียนประชาชน", new ImageIcon(), citizenView, "ลงทะเบียนและแสดงรายการประชาชน");

        // View 2: จัดสรรที่พักพิง
        ShelterAssignmentView assignmentView = new ShelterAssignmentView(
                shelterController, citizenController, assignmentController);
        tabbedPane.addTab("จัดสรรที่พักพิง", new ImageIcon(), assignmentView, "จัดสรรศูนย์พักพิงให้กับประชาชน");

        // View 3: รายงานผล
        ReportView reportView = new ReportView(citizenController, assignmentController);
        tabbedPane.addTab("รายงานผล", new ImageIcon(), reportView, "รายงานผลการจัดสรร");

        add(tabbedPane, BorderLayout.CENTER);

        // สร้าง Menu Bar
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu: ไฟล์
        JMenu fileMenu = new JMenu("ไฟล์");

        JMenuItem exitItem = new JMenuItem("ออกจากโปรแกรม");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Menu: ช่วยเหลือ
        JMenu helpMenu = new JMenu("ช่วยเหลือ");

        JMenuItem aboutItem = new JMenuItem("เกี่ยวกับ");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        String message = "ระบบจัดสรรที่พักพิงในสถานการณ์ฉุกเฉิน\n\n" +
                "พัฒนาโดยใช้ MVC Design Pattern\n" +
                "Java Swing + SQLite Database\n\n" +
                "Features:\n" +
                "- ลงทะเบียนประชาชน\n" +
                "- จัดสรรที่พักพิงอัตโนมัติ\n" +
                "- รายงานผลการจัดสรร\n" +
                "- ตรวจสอบ Business Rules";

        JOptionPane.showMessageDialog(this,
                message,
                "เกี่ยวกับโปรแกรม",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void display() {
        setVisible(true);
    }
}

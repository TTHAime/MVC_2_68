import controller.AssignmentController;
import controller.CitizenController;
import controller.ShelterController;
import database.DatabaseManager;
import model.Citizen;
import model.Shelter;
import view.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main class สำหรับเริ่มต้นโปรแกรม
 */
public class Main {
    public static void main(String[] args) {
        // ใช้ Look and Feel ของระบบ
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // สร้าง Database Manager
        DatabaseManager dbManager = new DatabaseManager();

        // สร้าง Controllers
        CitizenController citizenController = new CitizenController(dbManager);
        ShelterController shelterController = new ShelterController(dbManager);
        AssignmentController assignmentController = new AssignmentController(
                dbManager, shelterController, citizenController);

        // ตรวจสอบและเพิ่มข้อมูลตัวอย่าง (ถ้ายังไม่มี)
        initializeSampleData(dbManager, shelterController, citizenController);

        // สร้างและแสดง GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(
                    citizenController,
                    shelterController,
                    assignmentController);
            mainFrame.display();
        });
    }

    /**
     * เพิ่มข้อมูลตัวอย่างตาม requirement:
     * - ศูนย์พักพิง > 5 แห่ง
     * - ประชาชน ≥ 30 คน
     * - ต้องมีประชาชนที่ไม่ได้รับการจัดสรร
     */
    private static void initializeSampleData(DatabaseManager dbManager,
            ShelterController shelterController,
            CitizenController citizenController) {
        try {
            // ตรวจสอบว่ามีข้อมูลอยู่แล้วหรือไม่
            if (!shelterController.getAllShelters().isEmpty()) {
                System.out.println("ข้อมูลตัวอย่างมีอยู่แล้ว");
                return;
            }

            System.out.println("กำลังสร้างข้อมูลตัวอย่าง...");

            // สร้างศูนย์พักพิง 8 แห่ง
            String[] shelterNames = {
                    "ศูนย์พักพิงโรงเรียนบ้านสวน",
                    "ศูนย์พักพิงวัดใหญ่",
                    "ศูนย์พักพิงโรงพยาบาลชุมชน",
                    "ศูนย์พักพิงหอประชุมเทศบาล",
                    "ศูนย์พักพิงสนามกีฬาเอนกประสงค์",
                    "ศูนย์พักพิงโรงเรียนบ้านเหนือ",
                    "ศูนย์พักพิงวัดป่า",
                    "ศูนย์พักพิงศาลาประชาคม"
            };

            int[] capacities = { 100, 150, 80, 120, 200, 90, 70, 1 };
            Shelter.RiskLevel[] riskLevels = {
                    Shelter.RiskLevel.LOW,
                    Shelter.RiskLevel.LOW,
                    Shelter.RiskLevel.LOW,
                    Shelter.RiskLevel.MEDIUM,
                    Shelter.RiskLevel.MEDIUM,
                    Shelter.RiskLevel.MEDIUM,
                    Shelter.RiskLevel.HIGH,
                    Shelter.RiskLevel.HIGH
            };

            for (int i = 0; i < shelterNames.length; i++) {
                shelterController.addShelter(shelterNames[i], capacities[i], riskLevels[i]);
            }

            System.out.println("สร้างศูนย์พักพิง " + shelterNames.length + " แห่ง");

            // สร้างประชาชน 35 คน
            String[] citizenNames = {
                    // กลุ่มเด็ก (10 คน)
                    "เด็กหญิงสมหมาย", "เด็กชายวิชัย", "เด็กหญิงนิภา", "เด็กชายสมชาย",
                    "เด็กหญิงมาลี", "เด็กชายประเสริฐ", "เด็กหญิงสุดา", "เด็กชายธนา",
                    "เด็กหญิงจิตรา", "เด็กชายสมศักดิ์",

                    // กลุ่มผู้สูงอายุ (8 คน)
                    "นายสมบัติ ใจดี", "นางสาวจันทร์ แสงทอง", "นายประดิษฐ์ มั่นคง",
                    "นางแสง รุ่งเรือง", "นายสมพร ศรีสุข", "นางประไพ สวัสดี",
                    "นายวิรัช เจริญ", "นางบุญมี สมบูรณ์",

                    // กลุ่มเสี่ยง (7 คน)
                    "นายอนุชา ป่วยไข้", "นางสาวพิมพ์ใจ หวัดหนัก", "นายประสิทธิ์ เจ็บไข้",
                    "นางกรรณิการ์ ชรา", "นายวีระ โรคเรื้อรัง", "นางสาววารี หอบหืด",
                    "นายสมบูรณ์ เบาหวาน",

                    // VIP (3 คน)
                    "นายกำนัน สมหมาย", "นายกรชิต นายอำเภอ", "พันตำรวจ วิชัย รักษาดี",

                    // ประชาชนทั่วไป (7 คน)
                    "นายสมชาย ทนงาม", "นางสาวนิดา สวยงาม", "นายธนา รวยดี",
                    "นางจริยา ขยัน", "นายชัยวัฒน์ แข็งแรง", "นางสาวพรทิพย์ ดีเด่น",
                    "นายกิตติ มั่งคั่ง"
            };

            int[] ages = {
                    // เด็ก
                    8, 10, 12, 15, 7, 9, 11, 14, 16, 13,

                    // ผู้สูงอายุ
                    65, 70, 68, 75, 62, 72, 80, 67,

                    // กลุ่มเสี่ยง
                    45, 38, 50, 55, 42, 48, 52,

                    // VIP
                    50, 45, 40,

                    // ทั่วไป
                    30, 28, 35, 32, 40, 25, 38
            };

            String[] healthConditions = {
                    // เด็ก
                    "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ",

                    // ผู้สูงอายุ
                    "ปกติ", "โรคข้อเข่า", "ปกติ", "โรคหัวใจ", "ปกติ", "ปกติ", "เบาหวาน", "ปกติ",

                    // กลุ่มเสี่ยง
                    "ไข้หวัด", "ไข้สูง", "มีอาการป่วย", "โรคข้อ", "โรคเรื้อรัง", "โรคหอบหืด", "เบาหวานความดันสูง",

                    // VIP
                    "ปกติ", "ปกติ", "แข็งแรงดี",

                    // ทั่วไป
                    "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ", "ปกติ"
            };

            Citizen.CitizenType[] types = {
                    // เด็ก
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL,

                    // ผู้สูงอายุ
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,

                    // กลุ่มเสี่ยง
                    Citizen.CitizenType.AT_RISK, Citizen.CitizenType.AT_RISK, Citizen.CitizenType.AT_RISK,
                    Citizen.CitizenType.AT_RISK, Citizen.CitizenType.AT_RISK, Citizen.CitizenType.AT_RISK,
                    Citizen.CitizenType.AT_RISK,

                    // VIP
                    Citizen.CitizenType.VIP, Citizen.CitizenType.VIP, Citizen.CitizenType.VIP,

                    // ทั่วไป
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL, Citizen.CitizenType.GENERAL,
                    Citizen.CitizenType.GENERAL
            };

            for (int i = 0; i < citizenNames.length; i++) {
                citizenController.registerCitizen(
                        citizenNames[i],
                        ages[i],
                        healthConditions[i],
                        types[i]);
            }

            System.out.println("สร้างประชาชน " + citizenNames.length + " คน");
            System.out.println("ข้อมูลตัวอย่างพร้อมใช้งาน!");

        } catch (SQLException e) {
            System.err.println("เกิดข้อผิดพลาดในการสร้างข้อมูลตัวอย่าง: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

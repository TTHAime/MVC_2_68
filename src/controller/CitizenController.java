package controller;

import database.DatabaseManager;
import model.Citizen;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller สำหรับจัดการ Citizen
 */
public class CitizenController {
    private DatabaseManager dbManager;

    public CitizenController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * ลงทะเบียนประชาชนใหม่
     */
    public int registerCitizen(String name, int age, String healthCondition, Citizen.CitizenType type)
            throws SQLException {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("กรุณาระบุชื่อ");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("กรุณาระบุอายุที่ถูกต้อง");
        }

        Citizen citizen = new Citizen();
        citizen.setName(name.trim());
        citizen.setAge(age);
        citizen.setHealthCondition(healthCondition);
        citizen.setType(type);
        citizen.setRegistrationDate(LocalDate.now());

        return dbManager.addCitizen(citizen);
    }

    /**
     * ดึงข้อมูลประชาชนทั้งหมด
     */
    public List<Citizen> getAllCitizens() {
        try {
            return dbManager.getAllCitizens();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ดึงข้อมูลประชาชนตามประเภท
     */
    public List<Citizen> getCitizensByType(Citizen.CitizenType type) {
        try {
            return dbManager.getCitizensByType(type);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ดึงข้อมูลประชาชนที่ยังไม่ได้รับการจัดสรร
     */
    public List<Citizen> getUnassignedCitizens() {
        try {
            List<Citizen> allCitizens = dbManager.getAllCitizens();
            List<Citizen> unassigned = new ArrayList<>();

            for (Citizen citizen : allCitizens) {
                if (!dbManager.isAssigned(citizen.getId())) {
                    unassigned.add(citizen);
                }
            }

            return unassigned;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * จัดเรียงประชาชนตามลำดับความสำคัญ
     * เด็กและผู้สูงอายุได้รับการจัดสรรก่อน
     */
    public List<Citizen> getCitizensSortedByPriority() {
        List<Citizen> citizens = getUnassignedCitizens();

        // เรียงลำดับตามความสำคัญ
        citizens.sort(new Comparator<Citizen>() {
            @Override
            public int compare(Citizen c1, Citizen c2) {
                // ลำดับความสำคัญ: เด็ก/ผู้สูงอายุ > กลุ่มเสี่ยง > VIP > ทั่วไป
                boolean p1 = c1.isPriorityGroup();
                boolean p2 = c2.isPriorityGroup();

                if (p1 && !p2)
                    return -1;
                if (!p1 && p2)
                    return 1;

                // ถ้าความสำคัญเท่ากัน เรียงตาม type
                int typeCompare = getTypePriority(c1.getType()) - getTypePriority(c2.getType());
                if (typeCompare != 0)
                    return typeCompare;

                // ถ้า type เท่ากัน เรียงตามอายุ (เด็กและผู้สูงอายุก่อน)
                if (c1.getAge() < 18 && c2.getAge() >= 18)
                    return -1;
                if (c1.getAge() >= 18 && c2.getAge() < 18)
                    return 1;
                if (c1.getAge() >= 60 && c2.getAge() < 60)
                    return -1;
                if (c1.getAge() < 60 && c2.getAge() >= 60)
                    return 1;

                return 0;
            }
        });

        return citizens;
    }

    private int getTypePriority(Citizen.CitizenType type) {
        switch (type) {
            case AT_RISK:
                return 1;
            case VIP:
                return 2;
            case GENERAL:
                return 3;
            default:
                return 4;
        }
    }

    /**
     * ดึงข้อมูลประชาชนตาม ID
     */
    public Citizen getCitizenById(int id) {
        try {
            return dbManager.getCitizenById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package controller;

import database.DatabaseManager;
import model.Shelter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller สำหรับจัดการ Shelter
 */
public class ShelterController {
    private DatabaseManager dbManager;

    public ShelterController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * เพิ่มศูนย์พักพิงใหม่
     */
    public int addShelter(String name, int maxCapacity, Shelter.RiskLevel riskLevel) throws SQLException {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("กรุณาระบุชื่อศูนย์พักพิง");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("กรุณาระบุความจุที่ถูกต้อง");
        }

        Shelter shelter = new Shelter();
        shelter.setName(name.trim());
        shelter.setMaxCapacity(maxCapacity);
        shelter.setRiskLevel(riskLevel);
        shelter.setCurrentOccupancy(0);

        return dbManager.addShelter(shelter);
    }

    /**
     * ดึงข้อมูลศูนย์พักพิงทั้งหมด
     */
    public List<Shelter> getAllShelters() {
        try {
            return dbManager.getAllShelters();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ดึงข้อมูลศูนย์พักพิงที่ยังมีที่ว่าง
     */
    public List<Shelter> getAvailableShelters() {
        List<Shelter> allShelters = getAllShelters();
        List<Shelter> available = new ArrayList<>();

        for (Shelter shelter : allShelters) {
            if (!shelter.isFull()) {
                available.add(shelter);
            }
        }

        return available;
    }

    /**
     * ดึงข้อมูลศูนย์พักพิงตามระดับความเสี่ยง
     */
    public List<Shelter> getSheltersByRiskLevel(Shelter.RiskLevel riskLevel) {
        List<Shelter> allShelters = getAllShelters();
        List<Shelter> filtered = new ArrayList<>();

        for (Shelter shelter : allShelters) {
            if (shelter.getRiskLevel() == riskLevel) {
                filtered.add(shelter);
            }
        }

        return filtered;
    }

    /**
     * ดึงข้อมูลศูนย์พักพิงตาม ID
     */
    public Shelter getShelterById(int id) {
        try {
            return dbManager.getShelterById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * อัพเดทจำนวนคนในศูนย์พักพิง
     */
    public void updateShelterOccupancy(int shelterId, int newOccupancy) throws SQLException {
        Shelter shelter = dbManager.getShelterById(shelterId);
        if (shelter == null) {
            throw new IllegalArgumentException("ไม่พบศูนย์พักพิง");
        }

        if (newOccupancy > shelter.getMaxCapacity()) {
            throw new IllegalArgumentException("จำนวนคนเกินความจุ");
        }

        shelter.setCurrentOccupancy(newOccupancy);
        dbManager.updateShelter(shelter);
    }

    /**
     * เพิ่มจำนวนคนในศูนย์พักพิง
     */
    public void incrementOccupancy(int shelterId) throws SQLException {
        Shelter shelter = dbManager.getShelterById(shelterId);
        if (shelter == null) {
            throw new IllegalArgumentException("ไม่พบศูนย์พักพิง");
        }

        if (shelter.isFull()) {
            throw new IllegalArgumentException("ศูนย์พักพิงเต็มแล้ว");
        }

        shelter.setCurrentOccupancy(shelter.getCurrentOccupancy() + 1);
        dbManager.updateShelter(shelter);
    }
}

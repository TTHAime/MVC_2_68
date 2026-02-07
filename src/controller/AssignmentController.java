package controller;

import database.DatabaseManager;
import model.Assignment;
import model.Citizen;
import model.Shelter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller สำหรับจัดการ Assignment (การจัดสรรที่พักพิง)
 */
public class AssignmentController {
    private DatabaseManager dbManager;
    private ShelterController shelterController;
    private CitizenController citizenController;

    public AssignmentController(DatabaseManager dbManager,
            ShelterController shelterController,
            CitizenController citizenController) {
        this.dbManager = dbManager;
        this.shelterController = shelterController;
        this.citizenController = citizenController;
    }

    /**
     * จัดสรรศูนย์พักพิงให้กับประชาชน
     */
    public int assignShelter(int citizenId, int shelterId) throws SQLException {
        // ตรวจสอบข้อมูล
        Citizen citizen = dbManager.getCitizenById(citizenId);
        Shelter shelter = dbManager.getShelterById(shelterId);

        if (citizen == null) {
            throw new IllegalArgumentException("ไม่พบข้อมูลประชาชน");
        }
        if (shelter == null) {
            throw new IllegalArgumentException("ไม่พบข้อมูลศูนย์พักพิง");
        }

        // ตรวจสอบว่าประชาชนได้รับการจัดสรรแล้วหรือยัง
        if (dbManager.isAssigned(citizenId)) {
            throw new SQLException("ประชาชนคนนี้ได้รับการจัดสรรแล้ว");
        }

        // ตรวจสอบความเหมาะสม (Business Rules)
        if (!shelter.canAccommodate(citizen)) {
            if (shelter.isFull()) {
                throw new SQLException("ศูนย์พักพิงเต็มแล้ว ไม่สามารถรับเพิ่มได้");
            } else if (citizen.hasHealthRisk() && shelter.getRiskLevel() != Shelter.RiskLevel.LOW) {
                throw new SQLException("ผู้มีความเสี่ยงด้านสุขภาพต้องถูกจัดไปยังศูนย์ความเสี่ยงต่ำเท่านั้น");
            }
        }

        // สร้าง Assignment
        Assignment assignment = new Assignment();
        assignment.setCitizenId(citizenId);
        assignment.setShelterId(shelterId);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setNotes(generateAssignmentNotes(citizen, shelter));

        int assignmentId = dbManager.addAssignment(assignment);

        // อัพเดทจำนวนคนในศูนย์พักพิง
        shelterController.incrementOccupancy(shelterId);

        return assignmentId;
    }

    private String generateAssignmentNotes(Citizen citizen, Shelter shelter) {
        StringBuilder notes = new StringBuilder();

        if (citizen.isPriorityGroup()) {
            notes.append("กลุ่มเด็ก/ผู้สูงอายุ; ");
        }
        if (citizen.hasHealthRisk()) {
            notes.append("มีความเสี่ยงด้านสุขภาพ; ");
        }
        if (citizen.getType() == Citizen.CitizenType.VIP) {
            notes.append("VIP; ");
        }

        notes.append("จัดสรรที่: ").append(shelter.getName());

        return notes.toString();
    }

    /**
     * จัดสรรศูนย์พักพิงแบบอัตโนมัติ
     */
    public int autoAssignShelter(int citizenId) throws SQLException {
        Citizen citizen = dbManager.getCitizenById(citizenId);
        if (citizen == null) {
            throw new IllegalArgumentException("ไม่พบข้อมูลประชาชน");
        }

        // หาศูนย์พักพิงที่เหมาะสม
        List<Shelter> shelters = shelterController.getAllShelters();
        Shelter bestShelter = null;

        // ถ้ามีความเสี่ยงด้านสุขภาพ ต้องเลือกศูนย์ความเสี่ยงต่ำ
        if (citizen.hasHealthRisk()) {
            for (Shelter shelter : shelters) {
                if (shelter.getRiskLevel() == Shelter.RiskLevel.LOW &&
                        shelter.canAccommodate(citizen)) {
                    if (bestShelter == null ||
                            shelter.getAvailableSpace() > bestShelter.getAvailableSpace()) {
                        bestShelter = shelter;
                    }
                }
            }
        } else {
            // เลือกศูนย์ที่มีที่ว่างมากที่สุด
            for (Shelter shelter : shelters) {
                if (shelter.canAccommodate(citizen)) {
                    if (bestShelter == null ||
                            shelter.getAvailableSpace() > bestShelter.getAvailableSpace()) {
                        bestShelter = shelter;
                    }
                }
            }
        }

        if (bestShelter == null) {
            throw new SQLException("ไม่พบศูนย์พักพิงที่เหมาะสม");
        }

        return assignShelter(citizenId, bestShelter.getId());
    }

    /**
     * ดึงข้อมูลการจัดสรรทั้งหมด พร้อมข้อมูล Citizen และ Shelter
     */
    public List<Assignment> getAllAssignmentsWithDetails() {
        try {
            List<Assignment> assignments = dbManager.getAllAssignments();

            // เติมข้อมูล Citizen และ Shelter
            for (Assignment assignment : assignments) {
                Citizen citizen = dbManager.getCitizenById(assignment.getCitizenId());
                Shelter shelter = dbManager.getShelterById(assignment.getShelterId());
                assignment.setCitizen(citizen);
                assignment.setShelter(shelter);
            }

            return assignments;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ดึงข้อมูลการจัดสรรตามศูนย์พักพิง
     */
    public List<Assignment> getAssignmentsByShelterId(int shelterId) {
        try {
            List<Assignment> assignments = dbManager.getAssignmentsByShelterId(shelterId);

            // เติมข้อมูล Citizen และ Shelter
            for (Assignment assignment : assignments) {
                Citizen citizen = dbManager.getCitizenById(assignment.getCitizenId());
                Shelter shelter = dbManager.getShelterById(assignment.getShelterId());
                assignment.setCitizen(citizen);
                assignment.setShelter(shelter);
            }

            return assignments;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ตรวจสอบว่าประชาชนได้รับการจัดสรรแล้วหรือยัง
     */
    public boolean isAssigned(int citizenId) {
        try {
            return dbManager.isAssigned(citizenId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

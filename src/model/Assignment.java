package model;

import java.time.LocalDate;

/**
 * Model class สำหรับข้อมูลการจัดสรรพักพิง
 */
public class Assignment {
    private int id;
    private int citizenId;
    private int shelterId;
    private LocalDate assignmentDate;
    private String notes;

    // เพิ่มข้อมูลเพื่อความสมบูรณ์
    private Citizen citizen;
    private Shelter shelter;

    // Constructor
    public Assignment() {
        this.assignmentDate = LocalDate.now();
    }

    public Assignment(int id, int citizenId, int shelterId, LocalDate assignmentDate) {
        this.id = id;
        this.citizenId = citizenId;
        this.shelterId = shelterId;
        this.assignmentDate = assignmentDate;
    }

    public Assignment(int id, int citizenId, int shelterId, LocalDate assignmentDate, String notes) {
        this.id = id;
        this.citizenId = citizenId;
        this.shelterId = shelterId;
        this.assignmentDate = assignmentDate;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(int citizenId) {
        this.citizenId = citizenId;
    }

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public String toString() {
        return String.format("Assignment[id=%d, citizenId=%d, shelterId=%d, date=%s]",
                id, citizenId, shelterId, assignmentDate);
    }
}

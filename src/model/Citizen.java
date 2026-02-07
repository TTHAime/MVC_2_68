package model;

import java.time.LocalDate;

/**
 * Model class สำหรับข้อมูลประชาชน
 */
public class Citizen {
    private int id;
    private String name;
    private int age;
    private String healthCondition;
    private LocalDate registrationDate;
    private CitizenType type;

    public enum CitizenType {
        GENERAL("ทั่วไป"),
        AT_RISK("กลุ่มเสี่ยง"),
        VIP("VIP");

        private final String displayName;

        CitizenType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor
    public Citizen() {
        this.registrationDate = LocalDate.now();
        this.type = CitizenType.GENERAL;
    }

    public Citizen(int id, String name, int age, String healthCondition,
            LocalDate registrationDate, CitizenType type) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.healthCondition = healthCondition;
        this.registrationDate = registrationDate;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHealthCondition() {
        return healthCondition;
    }

    public void setHealthCondition(String healthCondition) {
        this.healthCondition = healthCondition;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CitizenType getType() {
        return type;
    }

    public void setType(CitizenType type) {
        this.type = type;
    }

    // Business logic methods
    public boolean isPriorityGroup() {
        // เด็ก (อายุ < 18) หรือ ผู้สูงอายุ (อายุ >= 60) ได้รับการจัดสรรก่อน
        return age < 18 || age >= 60;
    }

    public boolean hasHealthRisk() {
        return type == CitizenType.AT_RISK ||
                (healthCondition != null && !healthCondition.trim().isEmpty() &&
                        !healthCondition.equalsIgnoreCase("ปกติ"));
    }

    @Override
    public String toString() {
        return String.format("Citizen[id=%d, name=%s, age=%d, type=%s]",
                id, name, age, type.getDisplayName());
    }
}

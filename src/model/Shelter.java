package model;

/**
 * Model class สำหรับข้อมูลศูนย์พักพิง
 */
public class Shelter {
    private int id;
    private String name;
    private int maxCapacity;
    private int currentOccupancy;
    private RiskLevel riskLevel;

    public enum RiskLevel {
        LOW("ต่ำ", 1),
        MEDIUM("กลาง", 2),
        HIGH("สูง", 3);

        private final String displayName;
        private final int level;

        RiskLevel(String displayName, int level) {
            this.displayName = displayName;
            this.level = level;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getLevel() {
            return level;
        }
    }

    // Constructor
    public Shelter() {
        this.currentOccupancy = 0;
        this.riskLevel = RiskLevel.LOW;
    }

    public Shelter(int id, String name, int maxCapacity, int currentOccupancy, RiskLevel riskLevel) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = currentOccupancy;
        this.riskLevel = riskLevel;
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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    // Business logic methods
    public boolean isFull() {
        return currentOccupancy >= maxCapacity;
    }

    public int getAvailableSpace() {
        return maxCapacity - currentOccupancy;
    }

    public double getOccupancyPercentage() {
        if (maxCapacity == 0)
            return 0;
        return (currentOccupancy * 100.0) / maxCapacity;
    }

    public boolean canAccommodate(Citizen citizen) {
        // ตรวจสอบว่ายังมีที่ว่างไหม
        if (isFull()) {
            return false;
        }

        // ถ้าประชาชนมีความเสี่ยงด้านสุขภาพ ต้องไปศูนย์ความเสี่ยงต่ำเท่านั้น
        if (citizen.hasHealthRisk() && this.riskLevel != RiskLevel.LOW) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("Shelter[id=%d, name=%s, occupancy=%d/%d, risk=%s]",
                id, name, currentOccupancy, maxCapacity, riskLevel.getDisplayName());
    }
}

package database;

import model.Assignment;
import model.Citizen;
import model.Shelter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager จัดการการเชื่อมต่อและ CRUD operations กับ SQLite
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:shelter_system.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createSheltersTable = "CREATE TABLE IF NOT EXISTS shelters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "max_capacity INTEGER NOT NULL, " +
                "current_occupancy INTEGER DEFAULT 0, " +
                "risk_level TEXT NOT NULL)";

        String createCitizensTable = "CREATE TABLE IF NOT EXISTS citizens (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "health_condition TEXT, " +
                "registration_date TEXT NOT NULL, " +
                "type TEXT NOT NULL)";

        String createAssignmentsTable = "CREATE TABLE IF NOT EXISTS assignments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "citizen_id INTEGER NOT NULL, " +
                "shelter_id INTEGER NOT NULL, " +
                "assignment_date TEXT NOT NULL, " +
                "notes TEXT, " +
                "FOREIGN KEY(citizen_id) REFERENCES citizens(id), " +
                "FOREIGN KEY(shelter_id) REFERENCES shelters(id))";

        Statement stmt = connection.createStatement();
        stmt.execute(createSheltersTable);
        stmt.execute(createCitizensTable);
        stmt.execute(createAssignmentsTable);
        stmt.close();
    }

    // ==================== Shelter CRUD ====================

    public int addShelter(Shelter shelter) throws SQLException {
        String sql = "INSERT INTO shelters (name, max_capacity, current_occupancy, risk_level) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, shelter.getName());
        pstmt.setInt(2, shelter.getMaxCapacity());
        pstmt.setInt(3, shelter.getCurrentOccupancy());
        pstmt.setString(4, shelter.getRiskLevel().name());
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : -1;
        pstmt.close();
        return id;
    }

    public List<Shelter> getAllShelters() throws SQLException {
        List<Shelter> shelters = new ArrayList<>();
        String sql = "SELECT * FROM shelters";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Shelter shelter = new Shelter(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("max_capacity"),
                    rs.getInt("current_occupancy"),
                    Shelter.RiskLevel.valueOf(rs.getString("risk_level")));
            shelters.add(shelter);
        }

        rs.close();
        stmt.close();
        return shelters;
    }

    public Shelter getShelterById(int id) throws SQLException {
        String sql = "SELECT * FROM shelters WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        Shelter shelter = null;
        if (rs.next()) {
            shelter = new Shelter(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("max_capacity"),
                    rs.getInt("current_occupancy"),
                    Shelter.RiskLevel.valueOf(rs.getString("risk_level")));
        }

        rs.close();
        pstmt.close();
        return shelter;
    }

    public void updateShelter(Shelter shelter) throws SQLException {
        String sql = "UPDATE shelters SET name = ?, max_capacity = ?, current_occupancy = ?, risk_level = ? WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, shelter.getName());
        pstmt.setInt(2, shelter.getMaxCapacity());
        pstmt.setInt(3, shelter.getCurrentOccupancy());
        pstmt.setString(4, shelter.getRiskLevel().name());
        pstmt.setInt(5, shelter.getId());
        pstmt.executeUpdate();
        pstmt.close();
    }

    // ==================== Citizen CRUD ====================

    public int addCitizen(Citizen citizen) throws SQLException {
        // ตรวจสอบว่าประชาชนลงทะเบียนแล้วหรือยัง (ตาม business rule)
        if (isCitizenRegistered(citizen.getName())) {
            throw new SQLException("ประชาชนคนนี้ลงทะเบียนแล้ว");
        }

        String sql = "INSERT INTO citizens (name, age, health_condition, registration_date, type) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, citizen.getName());
        pstmt.setInt(2, citizen.getAge());
        pstmt.setString(3, citizen.getHealthCondition());
        pstmt.setString(4, citizen.getRegistrationDate().toString());
        pstmt.setString(5, citizen.getType().name());
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : -1;
        pstmt.close();
        return id;
    }

    private boolean isCitizenRegistered(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM citizens WHERE name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }

    public List<Citizen> getAllCitizens() throws SQLException {
        List<Citizen> citizens = new ArrayList<>();
        String sql = "SELECT * FROM citizens";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Citizen citizen = createCitizenFromResultSet(rs);
            citizens.add(citizen);
        }

        rs.close();
        stmt.close();
        return citizens;
    }

    public List<Citizen> getCitizensByType(Citizen.CitizenType type) throws SQLException {
        List<Citizen> citizens = new ArrayList<>();
        String sql = "SELECT * FROM citizens WHERE type = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, type.name());
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Citizen citizen = createCitizenFromResultSet(rs);
            citizens.add(citizen);
        }

        rs.close();
        pstmt.close();
        return citizens;
    }

    public Citizen getCitizenById(int id) throws SQLException {
        String sql = "SELECT * FROM citizens WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        Citizen citizen = null;
        if (rs.next()) {
            citizen = createCitizenFromResultSet(rs);
        }

        rs.close();
        pstmt.close();
        return citizen;
    }

    private Citizen createCitizenFromResultSet(ResultSet rs) throws SQLException {
        return new Citizen(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("health_condition"),
                LocalDate.parse(rs.getString("registration_date")),
                Citizen.CitizenType.valueOf(rs.getString("type")));
    }

    // ==================== Assignment CRUD ====================

    public int addAssignment(Assignment assignment) throws SQLException {
        String sql = "INSERT INTO assignments (citizen_id, shelter_id, assignment_date, notes) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, assignment.getCitizenId());
        pstmt.setInt(2, assignment.getShelterId());
        pstmt.setString(3, assignment.getAssignmentDate().toString());
        pstmt.setString(4, assignment.getNotes());
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : -1;
        pstmt.close();
        return id;
    }

    public List<Assignment> getAllAssignments() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Assignment assignment = createAssignmentFromResultSet(rs);
            assignments.add(assignment);
        }

        rs.close();
        stmt.close();
        return assignments;
    }

    public Assignment getAssignmentByCitizenId(int citizenId) throws SQLException {
        String sql = "SELECT * FROM assignments WHERE citizen_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, citizenId);
        ResultSet rs = pstmt.executeQuery();

        Assignment assignment = null;
        if (rs.next()) {
            assignment = createAssignmentFromResultSet(rs);
        }

        rs.close();
        pstmt.close();
        return assignment;
    }

    public List<Assignment> getAssignmentsByShelterId(int shelterId) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE shelter_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, shelterId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Assignment assignment = createAssignmentFromResultSet(rs);
            assignments.add(assignment);
        }

        rs.close();
        pstmt.close();
        return assignments;
    }

    private Assignment createAssignmentFromResultSet(ResultSet rs) throws SQLException {
        return new Assignment(
                rs.getInt("id"),
                rs.getInt("citizen_id"),
                rs.getInt("shelter_id"),
                LocalDate.parse(rs.getString("assignment_date")),
                rs.getString("notes"));
    }

    public boolean isAssigned(int citizenId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM assignments WHERE citizen_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, citizenId);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next() && rs.getInt(1) > 0;
        rs.close();
        pstmt.close();
        return exists;
    }

    // ==================== Utility ====================

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

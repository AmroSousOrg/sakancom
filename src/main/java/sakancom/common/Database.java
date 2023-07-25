package sakancom.common;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*

    This class is made to manage all database functionality such as implementations
    of all queries required by the system.
    And it contains the information about the database

*/
public final class Database {

    /**
     * fields that define properties of the database and load it
     * from the config file using static initializer
     */
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_NAME = "ourConfig.config";

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_NAME)) {
            properties.load(fis);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "config file does not found.\nor not written well.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     method to make a connection to the database
     */
    public static Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + properties.get("DATABASE_NAME"),
                (String)properties.get("DATABASE_USERNAME"), (String)properties.get("DATABASE_PASSWORD"));
    }

    /*
        Method to query a specified user based on name and password
        and role (tenant / owner / admin).
    */
    public static ResultSet getUser(String name, String password, String role, Connection conn)
            throws SQLException {

        String query = "SELECT * FROM `%s` WHERE `name` = ? and `password` = ?";
        query = String.format(query, role);
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, name);
        try {
            stmt.setString(2, Functions.sha256(password));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return stmt.executeQuery();
    }

    /*
        this method to get user from database depending on name.
    */
    private static ResultSet getUser(String username, String role, Connection conn)
            throws SQLException {
        String query = "SELECT * FROM `%s` WHERE `name` = ?";
        query = String.format(query, role);
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, username);
        return stmt.executeQuery();
    }

    /*
     query a specified tenant based on username and password
    */
    public static ResultSet getTenant(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "tenants", conn);
    }

    /***
     query a specified owner based on username and password
     ***/
    public static ResultSet getOwner(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "owners", conn);
    }

    /*
     query the admin based on name and password
    */
    public static ResultSet getAdmin(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "admin", conn);
    }

    /*
        method to switch to test database while testing
    */
    public static void switchTestDatabase() {
        properties.setProperty("DATABASE_NAME", (String)properties.get("TEST_DATABASE_NAME"));
    }

    /*
        method to check if user with given role and name exist in the database or not
    */
    public static boolean isUserExist(String role, String name)
            throws SQLException {
        String table;
        switch (role) {
            case "tenant" -> table = "tenants";
            case "owner" -> table = "owners";
            case "admin" -> table = "admin";
            default -> {
                return false;
            }
        }
        Connection conn = Database.makeConnection();
        ResultSet rs = getUser(name, table, conn);
        boolean result = rs.next();
        conn.close();
        return result;
    }

    /*
        method to add new tenant
    */
    public static void addTenant(HashMap<String, Object> data) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = makeConnection();
            String query = "insert into `tenants` (`name`, `password`, `email`, `phone`," +
                    " `age`, `university_major`) values (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, (String)data.get("name"));
            stmt.setString(2, Functions.sha256((String)data.get("password")));
            stmt.setString(3, (String)data.get("email"));
            stmt.setString(4, (String)data.get("phone"));
            stmt.setInt(5, Integer.parseInt((String)data.get("age")));
            stmt.setString(6, (String)data.get("university_major"));
            stmt.executeUpdate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
        }
    }

    /*
        method to add new owner
    */
    public static void addOwner(HashMap<String, Object> data) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = makeConnection();
            String query = "insert into `owners` (`name`, `password`, `email`, `phone`) " +
                    "values (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, (String)data.get("name"));
            stmt.setString(2, Functions.sha256((String)data.get("password")));
            stmt.setString(3, (String)data.get("email"));
            stmt.setString(4, (String)data.get("phone"));
            stmt.executeUpdate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
        }
    }

    /*
        this method to delete tenant or owner based on role and name
    */
    public static void deleteUser(String table, String name) throws SQLException {
        Connection conn = makeConnection();
        String query = "delete from `%s` where `name` = ?";
        query = String.format(query, table);
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.executeUpdate();
        conn.close();
        stmt.close();
    }

    /**
     *  method to execute passed query as string and return the resultSet.
     */
    public static ResultSet getQuery(String query, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     *  method to add new house in database
     *  pass inputs as Map<Key, Value>
     */
    public static void addHouse(Map<String, String> data) throws SQLException {
        Connection conn = makeConnection();
        String query = "insert into `housing` (`name`, `location`, `owner_id`, `rent`, `water_inclusive`, `electricity_inclusive`," +
                " `services`, `floors`, `apart_per_floor`, `available`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, data.get("name"));
        stmt.setString(2, data.get("location"));
        stmt.setLong(3, Long.parseLong(data.get("owner_id")));
        stmt.setInt(4, Integer.parseInt(data.get("rent")));
        stmt.setBoolean(5, Boolean.parseBoolean(data.get("water_inclusive")));
        stmt.setBoolean(6, Boolean.parseBoolean(data.get("electricity_inclusive")));
        stmt.setString(7, data.get("services"));
        stmt.setInt(8, Integer.parseInt(data.get("floors")));
        stmt.setInt(9, Integer.parseInt(data.get("apart_per_floor")));
        stmt.setBoolean(10, Boolean.parseBoolean(data.get("available")));
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public static long addReservation(HashMap<String, String> data) throws SQLException {
        Connection conn = makeConnection();
        String query = "insert into `reservations` (`tenant_id`, `housing_id`, `floor_num`, `apart_num`) values (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setLong(1, Long.parseLong(data.get("tenant_id")));
        stmt.setLong(2, Long.parseLong(data.get("housing_id")));
        stmt.setInt(3, Integer.parseInt(data.get("floor_num")));
        stmt.setInt(4, Integer.parseInt(data.get("apart_num")));
        int affectedRows = stmt.executeUpdate();
        long last_id = -1;
        if (affectedRows > 0) {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                last_id = rs.getLong(1);
            }
        }
        stmt.close();
        conn.close();
        return last_id;
    }
}

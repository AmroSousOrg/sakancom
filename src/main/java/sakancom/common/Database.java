package sakancom.common;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

    This class is made to manage all database functionality such as implementations
    of all queries required by the system.
    And it contains the information about the database

*/
public final class Database {

    /**
     * private constructor to preventing instantiation
     */
    private Database() {
    }

    /**
     * fields that define properties of the database and load it
     * from the config file using static initializer
     */
    private static final Properties database_name = new Properties();
    private static String databaseName;
    private static final Logger logger = LogManager.getLogger(Database.class);

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream("db.properties");
            properties.load(inputStream);
            database_name.setProperty("database_name", properties.getProperty("db.database-name"));
            database_name.setProperty("test-database", properties.getProperty("db.test-database-name"));
            databaseName = properties.getProperty("db.database-name");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /*
     method to make a connection to the database
     */
    public static Connection makeConnection() throws SQLException {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("db.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "error in reading" +
                    " properties file.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url + databaseName, username, password);
    }

    /*
        Method to query a specified user based on name and password
        and role (tenant / owner / admin).
    */
    public static Map<String, Object> getUser(String name, String password, String role)
            throws SQLException {

        String query = "SELECT * FROM tenants WHERE name = ? and password = ?";
        if (role.equals("owners")) query = "SELECT * FROM owners WHERE name = ? and password = ?";
        else if (role.equals("admin")) query = "SELECT * FROM admin WHERE name = ? and password = ?";

        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, name);
            stmt.setString(2, Functions.sha256(password));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Functions.rsToHashMap(rs);
                else
                    return new HashMap<>();
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return new HashMap<>();
    }

    /*
        this method to get user from database depending on name.
    */
    private static Map<String, Object> getUser(String username, String role)
            throws SQLException {
        String query = "SELECT * FROM tenants WHERE name = ?";
        if (role.equals("owners")) query = "SELECT * FROM owners WHERE name = ?";
        else if (role.equals("admin")) query = "SELECT * FROM admin WHERE name = ?";

        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Functions.rsToHashMap(rs);
                else
                    return new HashMap<>();
            }
        }
    }

    /*
     query a specified tenant based on username and password
    */
    public static Map<String, Object> getTenant(String name, String password) throws SQLException {
        return getUser(name, password, "tenants");
    }

    /***
     query a specified owner based on username and password
     ***/
    public static Map<String, Object> getOwner(String name, String password) throws SQLException {
        return getUser(name, password, "owners");
    }

    /*
     query the admin based on name and password
    */
    public static Map<String, Object> getAdmin(String name, String password) throws SQLException {
        return getUser(name, password, "admin");
    }

    /*
        method to switch to test database while testing
    */
    public static void setTestDatabase(boolean ok) {
        databaseName = database_name.getProperty(ok ? "test-database" : "database-name");
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

        return !getUser(name, table).isEmpty();
    }
    /*
        method to add new tenant
    */
    public static void addTenant(Map<String, Object> data) throws SQLException {
        String query = "insert into `tenants` (`name`, `password`, `email`, `phone`," +
                " `age`, `university_major`) values (?, ?, ?, ?, ?, ?)";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            stmt.setString(1, (String)data.get("name"));
            stmt.setString(2, Functions.sha256((String)data.get("password")));
            stmt.setString(3, (String)data.get("email"));
            stmt.setString(4, (String)data.get("phone"));
            stmt.setInt(5, Integer.parseInt((String)data.get("age")));
            stmt.setString(6, (String)data.get("university_major"));
            stmt.executeUpdate();

        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
    }

    /*
        method to add new owner
    */
    public static void addOwner(Map<String, Object> data) throws SQLException {
        String query = "insert into `owners` (`name`, `password`, `email`, `phone`) " +
                "values (?, ?, ?, ?)";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ){
            stmt.setString(1, (String)data.get("name"));
            stmt.setString(2, Functions.sha256((String)data.get("password")));
            stmt.setString(3, (String)data.get("email"));
            stmt.setString(4, (String)data.get("phone"));
            stmt.executeUpdate();

        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     *  method to add new house in database
     *  pass inputs as Map<Key, Value>
     */
    public static void addHouse(Map<String, String> data) throws SQLException {

        String query = "insert into `housing` (`name`, `location`, `owner_id`, `rent`, `water_inclusive`, `electricity_inclusive`," +
                " `services`, `floors`, `apart_per_floor`, `picture`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, data.get("name"));
            stmt.setString(2, data.get("location"));
            stmt.setLong(3, Long.parseLong(data.get("owner_id")));
            stmt.setInt(4, Integer.parseInt(data.get("rent")));
            stmt.setInt(5, Integer.parseInt(data.get("water_inclusive")));
            stmt.setInt(6, Integer.parseInt(data.get("electricity_inclusive")));
            stmt.setString(7, data.get("services"));
            stmt.setInt(8, Integer.parseInt(data.get("floors")));
            stmt.setInt(9, Integer.parseInt(data.get("apart_per_floor")));
            stmt.setString(10, data.get("picture"));
            stmt.executeUpdate();
        }
    }

    public static long addReservation(Map<String, String> data) throws SQLException {

        String query = "insert into `reservations` (`tenant_id`, `housing_id`, `floor_num`, `apart_num`) values (?, ?, ?, ?)";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setLong(1, Long.parseLong(data.get("tenant_id")));
            stmt.setLong(2, Long.parseLong(data.get("housing_id")));
            stmt.setInt(3, Integer.parseInt(data.get("floor_num")));
            stmt.setInt(4, Integer.parseInt(data.get("apart_num")));
            int affectedRows = stmt.executeUpdate();
            long lastId = -1;
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        lastId = rs.getLong(1);
                    }
                }
            }
            return lastId;
        }
    }

    /**
     * check if any house with the specified name exist except the house
     * with some id
     */
    public static boolean isHouseExist(String name, long exceptId) throws SQLException {

        String query = "select `housing_id` from `housing` where `name` = ? and `housing_id` != ?";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, name);
            stmt.setLong(2, exceptId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * check if any house with the specified name exist in database
     */
    public static boolean isHouseExist(String name) throws SQLException {
        return isHouseExist(name, 0);
    }

    public static void updateHouse(Map<String, String> data) throws SQLException {

        String query = "update `housing` set `name` = ?, `location` = ?, `rent` = ?, `water_inclusive` = ?," +
                " `electricity_inclusive` = ?, `services` = ?, `floors` = ?, `apart_per_floor` = ? where `housing_id` = ?";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, data.get("name"));
            stmt.setString(2, data.get("location"));
            stmt.setInt(3, Integer.parseInt(data.get("rent")));
            stmt.setInt(4, Integer.parseInt(data.get("water_inclusive")));
            stmt.setInt(5, Integer.parseInt(data.get("electricity_inclusive")));
            stmt.setString(6, data.get("services"));
            stmt.setInt(7, Integer.parseInt(data.get("floors")));
            stmt.setInt(8, Integer.parseInt(data.get("apart_per_floor")));
            stmt.setLong(9, Long.parseLong(data.get("housing_id")));
            stmt.executeUpdate();
        }
    }

    public static Map<String, Object> getHouse(long housingId) throws SQLException {

        String query = "select * from housing where housing_id = ?";
        try (
                Connection conn = makeConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setLong(1, housingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Functions.rsToHashMap(rs);
                else
                    return new HashMap<>();
            }
        }
    }
}

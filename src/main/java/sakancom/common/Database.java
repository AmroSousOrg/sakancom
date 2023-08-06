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

/**

    This class is made to manage all database functionality such as implementations
    of all queries required by the system.
    And it contains the information about the database

*/
public final class Database {

    /**
     * fields that define properties of the database and load it
     * from the config file using static initializer
     */
    private static final Properties database_name = new Properties();

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream("db.properties");
            properties.load(inputStream);
            database_name.setProperty("database_name", properties.getProperty("db.database-name"));
            database_name.setProperty("test-database", properties.getProperty("db.test-database-name"));
            database_name.setProperty("database", properties.getProperty("db.database-name"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     method to make a connection to the database
     */
    public static Connection makeConnection() throws SQLException {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream("db.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "error in reading" +
                    " properties file.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url + database_name.getProperty("database"),
                username, password);
    }

    /*
        Method to query a specified user based on name and password
        and role (tenant / owner / admin).
    */
    public static ResultSet getUser(String name, String password, String role, Connection conn)
            throws SQLException {

        String query = role.equals("tenants") ? "SELECT * FROM tenants WHERE name = ? and password = ?" :
                role.equals("owners") ? "SELECT * FROM owners WHERE name = ? and password = ?" :
                        "SELECT * FROM admin WHERE name = ? and password = ?";
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
        String query = role.equals("tenants") ? "SELECT * FROM tenants WHERE name = ?" :
                role.equals("owners") ? "SELECT * FROM owners WHERE name = ?" :
                        "SELECT * FROM admin WHERE name = ?";
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
    public static void setTestDatabase(boolean ok) {
        String name = database_name.getProperty(ok ? "test-database" : "database-name");
        database_name.setProperty("database", name);
    }

    /*
        method to check if user with given role and name exist in the database or not
    */
    public static boolean isUserExist(String role, String name)
            throws SQLException {
        String table;
        switch (role) {
            case "tenant":
                table = "tenants";
                break;
            case "owner":
                table = "owners";
                break;
            case "admin":
                table = "admin";
                break;
            default:
                return false;
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
                " `services`, `floors`, `apart_per_floor`, `picture`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
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

    /**
     * check if any house with the specified name exist except the house
     * with some id
     */
    public static boolean isHouseExist(String name, long exceptId) throws SQLException {
        Connection conn = makeConnection();
        String query = "select `housing_id` from `housing` where `name` = ? and `housing_id` != ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        stmt.setLong(2, exceptId);
        ResultSet rs = stmt.executeQuery();
        boolean res = rs.next();
        rs.close();
        stmt.close();
        conn.close();
        return res;
    }

    /**
     * check if any house with the specified name exist in database
     */
    public static boolean isHouseExist(String name) throws SQLException {
        return isHouseExist(name, 0);
    }

    public static void updateHouse(Map<String, String> data) throws SQLException {
        Connection conn = makeConnection();
        String query = "update `housing` set `name` = ?, `location` = ?, `rent` = ?, `water_inclusive` = ?," +
                " `electricity_inclusive` = ?, `services` = ?, `floors` = ?, `apart_per_floor` = ? where `housing_id` = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
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
        stmt.close();
        conn.close();
    }

    public static ResultSet getHouse(long housing_id) throws SQLException {
        Connection conn = makeConnection();
        String query = "select * from housing where housing_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setLong(1, housing_id);
        return stmt.executeQuery();
    }
}

package sakancom;

import java.sql.*;

/*
    This class is made to manage all database functionality such as implementations
    of all queries required by the system.
    And it contains the information about the database
* */
public final class Database {

    // Database info
    public static final String DATABASE_NAME = "sakancom_db";
    public static final String DATABASE_PASSWORD = "12345";
    public static final String DATABASE_USERNAME = "sw_team";

    // method to make a connection to the database
    public static Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD);
    }

    /*
        Method to query a specified user based on name and password
        and role (tenant / owner / admin).
    * */
    public static ResultSet getUser(String name, String password, String role, Connection conn) throws SQLException {

        String query = "SELECT * FROM `%s` WHERE `name` = ? and `password` = ?";
        query = String.format(query, role);
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, name);
        stmt.setString(2, password);
        return stmt.executeQuery();
    }

    // query a specified tenant based on username and password
    public static ResultSet getTenant(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "tenants", conn);
    }

    // query a specified owner based on username and password
    public static ResultSet getOwner(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "owners", conn);
    }

    // query the admin based on name and password
    public static ResultSet getAdmin(String name, String password, Connection conn) throws SQLException {
        return getUser(name, password, "admin", conn);
    }
}

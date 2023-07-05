package sakancom;

import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.LoginPage;
import sakancom.pages.OwnerPage;
import sakancom.pages.TenantPage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/*

    This is the main class of application that have the pages and
    other attributes as static members.
    This class used in tests to access all the pages and UI elements

*/
public class Application {

    /*
        enumeration represents the page state in application
    */
    public enum PAGE {
        CLOSED, LOGIN, TENANT, OWNER, ADMIN
    }

    /*
        class attributes
    */
    public static JFrame openedPage = null;
    public static PAGE status = PAGE.CLOSED;

    /*
        method to open login page in application
    */
    public static void openLoginPage() {
        openedPage = new LoginPage();
        openedPage.setVisible(true);
        status = PAGE.LOGIN;
    }

    /*
        method to open tenant page
    */
    public static boolean openTenantPage(String name, String password) throws SQLException {
        Connection conn = Database.makeConnection();
        ResultSet rs = Database.getTenant(name, password, conn);
        if (rs.next()) {
            openedPage.setVisible(false);
            openedPage = new TenantPage(name);
            openedPage.setVisible(true);
            status = PAGE.TENANT;
            conn.close();
            return true;
        }
        conn.close();
        return false;
    }

    /*
        method to open owner page
    */
    public static boolean openOwnerPage(String name, String password) throws SQLException {
        Connection conn = Database.makeConnection();
        ResultSet rs = Database.getOwner(name, password, conn);
        if (rs.next()) {
            openedPage.setVisible(false);
            openedPage = new OwnerPage(name);
            openedPage.setVisible(true);
            status = PAGE.OWNER;
            conn.close();
            return true;
        }
        conn.close();
        return false;
    }

    /*
        method to open admin page
    */
    public static boolean openAdminPage(String name, String password) throws SQLException {
        Connection conn = Database.makeConnection();
        ResultSet rs = Database.getAdmin(name, password, conn);
        if (rs.next()) {
            openedPage.setVisible(false);
            openedPage = new AdminPage(name);
            openedPage.setVisible(true);
            status = PAGE.ADMIN;
            conn.close();
            return true;
        }
        conn.close();
        return false;
    }
}

package sakancom;

import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.LoginPage;
import sakancom.pages.OwnerPage;
import sakancom.pages.TenantPage;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashMap;

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
        openPage(new LoginPage());
        status = PAGE.LOGIN;
    }

    /*
        method to open tenant page
    */
    public static boolean openTenantPage(String name, String password) throws SQLException {
        HashMap<String, Object> hm = Database.getTenant(name, password);
        if (hm != null) {
            openPage(new TenantPage(hm));
            status = PAGE.TENANT;
            return true;
        }
        return false;
    }

    /*
        method to open owner page
    */
    public static boolean openOwnerPage(String name, String password) throws SQLException {
        HashMap<String, Object> hm = Database.getOwner(name, password);
        if (hm != null) {
            openPage(new OwnerPage(hm));
            status = PAGE.OWNER;
            return true;
        }
        return false;
    }

    /*
        method to open admin page
    */
    public static boolean openAdminPage(String name, String password) throws SQLException {
        HashMap<String, Object> hm = Database.getAdmin(name, password);
        if (hm != null) {
            openPage(new AdminPage(hm));
            status = PAGE.ADMIN;
            return true;
        }
        return false;
    }

    /*
        private method open page that assert given class instance
        to the openedPage variable, and control visibility.
    */
    private static void openPage(JFrame page) {
        if (page == null) return;
        if (openedPage != null) openedPage.setVisible(false);
        openedPage = page;
        openedPage.setVisible(true);
    }
}

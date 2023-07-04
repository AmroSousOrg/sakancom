package sakancom;

import javax.swing.*;

/*
    This is the main class of application that have the pages and
    other attributes as static members.
    This class used in tests to access all the pages and UI elements
* */
public class Application {

    public static LoginPage loginPage;
    public static JFrame mainPage;
    public static String username;
    public static String password;
    public static boolean status;

    static {
        status = false;
        loginPage = null;
        mainPage = null;
        username = password = "";
    }
    public Application() {
        status = false;
        username = password = "";
        loginPage = new LoginPage();
    }
}

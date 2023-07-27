package sakancom;

import sakancom.pages.LoginPage;

/**
 *  Main application driver
 */

public class Main {
    public static void main(String[] args) {

        Application.openLoginPage();
        LoginPage page = (LoginPage) Application.openedPage;
        page.setCredentials("Admin", "Admin");
        page.setRoleCombo(LoginPage.ADMIN);
        page.pressSubmitButton();
    }
}
package sakancom;

import sakancom.pages.LoginPage;

/**
 *  Main application driver
 */

public class Main {
    public static void main(String[] args) {

        Application.openLoginPage();
        LoginPage page = (LoginPage) Application.openedPage;
        page.setCredentials("Amro Sous", "123");
        page.setRoleCombo(LoginPage.OWNER);
//        page.pressSubmitButton();
    }
}
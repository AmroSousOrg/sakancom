package sakancom;

import sakancom.pages.LoginPage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *  Main application driver
 */

public class Main {
    public static void main(String[] args) throws IOException {

        Application.openLoginPage();
        LoginPage page = (LoginPage) Application.openedPage;
        page.setCredentials("Amro Sous", "123");
        page.setRoleCombo(LoginPage.OWNER);
    }
}
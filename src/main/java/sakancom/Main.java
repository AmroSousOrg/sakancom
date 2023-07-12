package sakancom;

import sakancom.pages.LoginPage;
import sakancom.pages.OwnerPage;
import sakancom.pages.TenantPage;

import java.util.HashMap;

/*

    Main Driver class

*/
public class Main {
    public static void main(String[] args) {

//        Application.openLoginPage();
//        LoginPage page = (LoginPage)Application.openedPage;
//        page.usernameField.setText("Amro");
//        page.passwordField.setText("123");
//        page.roleCombo.setSelectedIndex(1);
//        page.submitButton.doClick();

        new OwnerPage(new HashMap<>()).setVisible(true);
    }
}

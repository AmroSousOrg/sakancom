package sakancom.test;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.LoginPage;
import sakancom.pages.OwnerPage;
import sakancom.pages.TenantPage;

public class LoginStepDefinition {

    @Before
    public void setupTestDB() {
        Database.switchToTestDatabase();
    }

    @Given("the user is on login page")
    public void the_user_is_on_login_page() {
        Application.openLoginPage();
        Assert.assertTrue(Application.openedPage instanceof LoginPage);
        Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
    }
    @And("he choose sign in as {string}")
    public void heChooseSignInAs(String role) {
        ((LoginPage)Application.openedPage).roleCombo.setSelectedIndex(
                role.equals("tenant") ? LoginPage.TENANT :
                        role.equals("owner") ? LoginPage.OWNER : LoginPage.ADMIN
        );
    }
    @When("he fill username as {string} and password as {string}")
    public void he_fill_username_as_and_password_as(String name, String pass) {
        LoginPage page = (LoginPage)Application.openedPage;
        page.usernameField.setText(name);
        page.passwordField.setText(pass);
    }
    @When("press submit button")
    public void press_submit_button() {
        ((LoginPage)Application.openedPage).submitButton.doClick();
    }
    @Then("give the correct status as {string} and message as {string}")
    public void give_the_correct_status_as_and_message_as(String status, String message) {
        if (status.equals("success")) {
            Assert.assertNotEquals(Application.PAGE.LOGIN, Application.status);
        }
        else {
            Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
            Assert.assertEquals(((LoginPage)Application.openedPage).errorLabel.getText(), message);
        }
    }
    @And("navigate to the correct page depending on {string} and {string}")
    public void navigateToTheCorrectPageDependingOnAnd(String status, String role) {
        if (status.equals("success")) {
            if (role.equals("tenant"))
                Assert.assertTrue(Application.openedPage instanceof TenantPage);
            else if (role.equals("owner"))
                Assert.assertTrue(Application.openedPage instanceof OwnerPage);
            else
                Assert.assertTrue(Application.openedPage instanceof AdminPage);

            Assert.assertTrue(Application.openedPage.isVisible());
        }
        else {
            Assert.assertTrue(Application.openedPage instanceof LoginPage);
            Assert.assertTrue(Application.openedPage.isVisible());
        }
    }
}

package sakancom.test;

import com.mysql.cj.log.Log;
import io.cucumber.java.en.*;
import org.junit.Assert;
import sakancom.Application;
import sakancom.pages.LoginPage;
import sakancom.pages.TenantPage;

public class LoginStepDefinition {

    Application app;

    public LoginStepDefinition(Application app) {
        super();
        this.app = app;
    }

    @Given("the user is on login page")
    public void the_user_is_on_login_page() {
        Application.openLoginPage();
        Assert.assertTrue(Application.openedPage instanceof LoginPage);
        Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
    }
    @Given("he choose sign in as tenant")
    public void he_choose_sign_in_as_tenant() {
        ((LoginPage)Application.openedPage).roleCombo.setSelectedIndex(LoginPage.TENANT);
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
            Assert.assertEquals(Application.PAGE.TENANT, Application.status);
        }
        else {
            Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
            Assert.assertEquals(((LoginPage)Application.openedPage).errorLabel.getText(), message);
        }
    }
    @And("navigate to the tenant page if {string} is success")
    public void navigateToTheTenantPageIfIsSuccess(String status) {
        if (status.equals("success")) {
            Assert.assertTrue(Application.openedPage instanceof TenantPage);
            Assert.assertTrue(Application.openedPage.isVisible());
        }
        else {
            Assert.assertTrue(Application.openedPage instanceof LoginPage);
            Assert.assertTrue(Application.openedPage.isVisible());
        }
    }
}

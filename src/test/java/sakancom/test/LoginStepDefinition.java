package sakancom.test;

import io.cucumber.java.en.*;
import org.junit.Assert;
import sakancom.Application;
import sakancom.LoginPage;

public class LoginStepDefinition {

    Application app;

    public LoginStepDefinition(Application app) {
        super();
        this.app = app;
    }

    @Given("the user is on login page")
    public void the_user_is_on_login_page() {
        Application.loginPage.setVisible(true);
        Assert.assertTrue(Application.loginPage.isVisible());
    }
    @Given("he choose sign in as tenant")
    public void he_choose_sign_in_as_tenant() {
        Application.loginPage.roleCombo.setSelectedIndex(LoginPage.TENANT);
        Assert.assertEquals("Tenant", Application.loginPage.roleCombo.getSelectedItem());
    }
    @When("he fill username as {string} and password as {string}")
    public void he_fill_username_as_and_password_as(String name, String pass) {
        Application.loginPage.usernameField.setText(name);
        Application.loginPage.passwordField.setText(pass);
    }
    @When("press submit button")
    public void press_submit_button() {
        Application.loginPage.submitButton.doClick();
    }
    @Then("give the correct status as {string} and message as {string}")
    public void give_the_correct_status_as_and_message_as(String status, String message) {
        boolean expectedStatus = (status.equals("success"));
        Assert.assertEquals(expectedStatus, Application.status);
        if (!expectedStatus) {
            Assert.assertEquals(message, Application.loginPage.errorLabel.getText());
        }
    }
    @And("navigate to the tenant page if {string} is success")
    public void navigateToTheTenantPageIfIsSuccess(String status) {
        if (status.equals("success")) {
            Assert.assertNotNull(Application.mainPage);
            Assert.assertFalse(Application.username.isEmpty());
            Assert.assertFalse(Application.password.isEmpty());
        }
    }
}

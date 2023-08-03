package sakancom.test;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.xml.sax.SAXException;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.LoginPage;
import sakancom.pages.OwnerPage;
import sakancom.pages.TenantPage;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class LoginPageDefinitions {

    @Before
    public void setupTestDB() {
        Database.setTestDatabase(true);
    }

    @After
    public void databaseTearDown() {
        try {
            TestDatabaseSeeder.deleteAllData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Given("the user is on login page")
    public void the_user_is_on_login_page() {
        Application.openLoginPage();
        Assert.assertTrue(Application.openedPage instanceof LoginPage);
        Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
    }

    @And("he choose sign in as {string}")
    public void heChooseSignInAs(String role) {
        ((LoginPage)Application.openedPage).setRoleCombo(
                role.equals("tenant") ? LoginPage.TENANT :
                        role.equals("owner") ? LoginPage.OWNER : LoginPage.ADMIN
        );
    }

    @When("he fill username as {string} and password as {string}")
    public void he_fill_username_as_and_password_as(String name, String pass) {
        LoginPage page = (LoginPage)Application.openedPage;
        page.setCredentials(name, pass);
    }

    @When("press sign in button")
    public void press_sign_in_button() {
        ((LoginPage)Application.openedPage).pressSubmitButton();
    }

    @Then("give the correct status as {string} and message as {string}")
    public void give_the_correct_status_as_and_message_as(String status, String message) {
        if (status.equals("success")) {
            Assert.assertNotEquals(Application.PAGE.LOGIN, Application.status);
        }
        else {
            Assert.assertEquals(Application.status, Application.PAGE.LOGIN);
            Assert.assertEquals(((LoginPage)Application.openedPage).getErrorLabel(), message);
        }
    }

    @And("navigate to the correct user page depending on {string} and {string}")
    public void navigateToTheCorrectUserPageDependingOnAnd(String status, String role) {
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

    @When("press I don't have account button")
    public void press_i_don_t_have_account_button() {
        LoginPage page = (LoginPage)Application.openedPage;
        page.pressCreateAccountButton();
    }

    @Then("create account panel appears")
    public void createAccountPanelAppears() {
        LoginPage page = (LoginPage)Application.openedPage;
        Assert.assertFalse(page.isLoginPanelOpen);
    }

    @Given("user choose create {string} role account")
    public void userChooseCreateRoleAccount(String role) {
        LoginPage page = (LoginPage)Application.openedPage;
        int selectIndex = role.equals("tenant") ? 0 : 1;
        page.setRegisterCombo(selectIndex);
        Assert.assertEquals(selectIndex, page.getRegisterCombo());
    }

    @Given("he fill the following details in create {string} account panel:")
    public void he_fill_the_following_details_in_create_panel(
            String rolePanel, io.cucumber.datatable.DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        LoginPage page = (LoginPage)Application.openedPage;

        if (rolePanel.equals("tenant")) {
            page.fillTenantRegisterInfo(rows);
        }
        else {
            page.fillOwnerRegisterInfo(rows);
        }
    }

    @When("he clicks create account button")
    public void he_clicks_create_account_button() {
        ((LoginPage)Application.openedPage).pressRegisterSubmit();
    }

    @Then("a new {string} account with name {string} will be added to the database")
    public void a_new_account_with_name_will_be_added_to_the_database(String role, String name) {

        boolean actual = false;
        try {
            actual = Database.isUserExist(role, name);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(Application.openedPage,
                    "Error in database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        Assert.assertTrue(actual);
    }

    @Then("{string} should see an {string} message in create account")
    public void shouldSeeAnMessageInCreateAccount(String role, String message) {
        LoginPage page = (LoginPage)Application.openedPage;
        String error_msg = (role.equals("tenant") ? page.getTenantErrorLabel() : page.getOwnerErrorLabel());
        Assert.assertEquals(message, error_msg);
    }

    @Given("there is an {string} account with name {string} already exist")
    public void thereIsAnAccountWithNameAlreadyExist(String role, String username) {
        boolean actual = false;
        try {
            actual = Database.isUserExist(role, username);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(Application.openedPage,
                    "Error in database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        Assert.assertTrue(actual);
    }

    @Given("the database consist of data in the file {string}")
    public void theDatabaseConsistOfDataInTheFile(String file) {
        try {
            TestDatabaseSeeder.deleteAllData();
            TestDatabaseSeeder.fillDatabase(file);

        } catch (SQLException | ParserConfigurationException | IOException | NoSuchAlgorithmException
                 | ParseException | SAXException e) {

            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

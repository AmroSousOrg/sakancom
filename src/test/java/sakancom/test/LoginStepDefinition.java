package sakancom.test;

import io.cucumber.java.en.*;
import sakancom.Application;

public class LoginStepDefinition {

    Application app;

    public LoginStepDefinition(Application app) {
        super();
        this.app = app;
    }

    @Given("the user is on login page")
    public void the_user_is_on_login_page() {
        //
    }
    @When("he fill username as {string} and password as {string}")
    public void he_fill_username_as_and_password_as(String string, String string2) {
        //
    }
    @When("press submit button")
    public void press_submit_button() {
        //
    }
    @Then("search for user in the database")
    public void search_for_user_in_the_database() {
        //
    }
    @Then("give the correct status as {string} and message as {string}")
    public void give_the_correct_status_as_and_message_as(String string, String string2) {
        //
    }
}

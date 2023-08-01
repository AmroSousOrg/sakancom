package sakancom.test;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.OwnerPage;

import java.sql.SQLException;
import java.util.Map;

public class OwnerPageDefinition {

    @Then("homePanel will appear in the owner page form")
    public void home_panel_will_appear_in_the_owner_page_form() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        Assert.assertEquals(OwnerPage.HOME, page.getSelectedTab());
    }

    @Given("an owner go to {string} tab")
    public void an_owner_go_to_tab(String tab) {
        int selectedTab = switch (tab) {
            case "home" -> OwnerPage.HOME;
            case "account" -> OwnerPage.ACCOUNT;
            case "my housing" -> OwnerPage.HOUSING;
            case "booking requests" -> OwnerPage.REQUESTS;
            case "add housing" -> OwnerPage.ADD_HOUSING;
            default -> 0;
        };
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.setSelectedTab(selectedTab);
    }

    @When("owner fill new housing info as follow:")
    public void owner_fill_new_housing_info_as_follow(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.setHousingNameField(data.get("name"));
        page.setHousingLocationField(data.get("location"));
        page.setHousingRentField(data.get("rent"));
        page.setWater(data.get("water").equals("1"));
        page.setElectricity(data.get("electricity").equals("1"));
        page.setHousingServices(data.get("services"));
        page.setHousingFloors(data.get("floors"));
        page.setHousingApart(data.get("apart"));
        page.setPhotoFile(data.get("picture"));
    }

    @When("owner press on submit button")
    public void owner_press_on_submit_button() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.pressSubmitNewHouseButton();
    }

    @Then("owner should see this message {string}")
    public void owner_should_see_this_message(String msg) {
        OwnerPage page = (OwnerPage) Application.openedPage;
        Assert.assertEquals(msg, page.getAddHouseMessageLabel());
    }

    @And("new housing with name {string} will added to database")
    public void newHousingWithNameWillAddedToDatabase(String name) throws SQLException {
        Assert.assertTrue(Database.isHouseExist(name));
    }
}

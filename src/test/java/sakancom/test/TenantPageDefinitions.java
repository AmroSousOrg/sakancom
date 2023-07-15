package sakancom.test;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.TenantPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TenantPageDefinitions {

    @Given("database contains houses with following data:")
    public void database_contains_houses_with_following_data(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        rows.forEach(row -> {
            try {
                Database.addHouse(row);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database fault.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Then("homePanel will appear in the tenant page form")
    public void home_panel_will_appear_in_the_tenant_page_form() {
        Assert.assertEquals(TenantPage.HOME, ((TenantPage)Application.openedPage).getSelectedTab());
    }

    @Given("is on the available housing panel")
    public void is_on_the_available_housing_panel() {
        TenantPage page = (TenantPage)Application.openedPage;
        page.setSelectedTab(TenantPage.HOUSING);
        Assert.assertEquals(TenantPage.HOUSING, page.getSelectedTab());
    }

    @Then("he should see these houses available:")
    public void he_should_see_these_houses_available(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        TenantPage page = (TenantPage)Application.openedPage;
        DefaultTableModel tm = page.getHousingTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> Assert.assertEquals(row.get("name"), tm.getValueAt(cnt.getAndIncrement(), 1)));
    }

    @When("he click on first row {string} housing")
    public void he_click_on_first_row_housing(String string) {
        TenantPage page = (TenantPage) Application.openedPage;
        page.selectHouseByIndex(0);
        DefaultTableModel dtm = page.getHousingTableModel();
        Assert.assertEquals(dtm.getValueAt(0, 1), string);
    }

    @When("press Show House button")
    public void press_show_house_button() {
        TenantPage page = (TenantPage) Application.openedPage;
        page.pressShowHouseButton();
    }

    @Then("he should see the following house information:")
    public void he_should_see_the_following_house_information(io.cucumber.datatable.DataTable dataTable) {
        TenantPage page = (TenantPage) Application.openedPage;
        Map<String, String> list = dataTable.asMaps(String.class, String.class).get(0);
        Assert.assertEquals(list.get("name"), page.getHouseName());
        Assert.assertEquals(list.get("location"), page.getHouseLocation());
        Assert.assertEquals(list.get("owner_name"), page.getHouseOwnerName());
        Assert.assertEquals(list.get("owner_phone"), page.getHouseOwnerPhone());
        Assert.assertEquals(list.get("rent"), page.getHouseRent());
        Assert.assertEquals(list.get("water_inclusive"), page.getHouseWaterInclusive() ? "1" : "0");
        Assert.assertEquals(list.get("electricity_inclusive"), page.getHouseElectricityInclusive() ? "1" : "0");
        Assert.assertEquals(list.get("services"), page.getHouseServices());
        Assert.assertEquals(list.get("floors"), page.getHouseFloors());
        Assert.assertEquals(list.get("apart_per_floor"), page.getHouseAparts());
    }

    @Given("he click on the Book button")
    public void he_click_on_the_book_button() {
        ((TenantPage)Application.openedPage).pressBookButton();
    }

    @Given("he choose floor {int} and apartment {int}")
    public void he_choose_floor_and_apartment(Integer floor, Integer apart) {
        ((TenantPage)Application.openedPage).selectBookRoom(floor, apart);
    }

    @Then("Then a new reservation should be added to the database with the accepted field set to {string}")
    public void then_a_new_reservation_should_be_added_to_the_database_with_the_accepted_field_set_to(String string) {

    }

    @Then("the booking details should be displayed on the panel")
    public void the_booking_details_should_be_displayed_on_the_panel() {
        // TODO: complete this step
    }

    @And("should see a housing picture")
    public void shouldSeeAHousingPicture() {
        Assert.assertNotNull(((TenantPage)Application.openedPage).getHousePicture());
    }
}

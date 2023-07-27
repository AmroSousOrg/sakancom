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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TenantPageDefinitions {

    private long lastReservationId;

    @Then("homePanel will appear in the tenant page form")
    public void home_panel_will_appear_in_the_tenant_page_form() {
        Assert.assertEquals(TenantPage.HOME, ((TenantPage)Application.openedPage).getSelectedTab());
    }

    @Given("he is on the available housing panel")
    public void he_is_on_the_available_housing_panel() {
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
    public void then_a_new_reservation_should_be_added_to_the_database_with_the_accepted_field_set_to(String state) {
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select `reservation_id`, `accepted` from `invoice`", conn);
            for (int i = 0; i < 3; i++) Assert.assertTrue(rs.next());
            lastReservationId = rs.getLong("reservation_id");
            Assert.assertEquals(Integer.parseInt(state), rs.getInt("accepted"));
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Then("the booking details should be displayed on the panel")
    public void the_booking_details_should_be_displayed_on_the_panel() {
        TenantPage page = (TenantPage) Application.openedPage;
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select * from `invoice` where `reservation_id` = " +
                    lastReservationId, conn);
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString("reservation_id"), page.getInvoiceReservationIdField());
            Assert.assertEquals(rs.getString("tenant_id"), page.getInvoiceTenantIdField());
            Assert.assertEquals(rs.getString("housing_id"), page.getInvoiceHouseIdField());
            Assert.assertEquals(rs.getString("owner_id"), page.getInvoiceOwnerIdField());
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @And("should see a housing picture")
    public void shouldSeeAHousingPicture() {
        Assert.assertNotNull(((TenantPage)Application.openedPage).getHousePicture());
    }

    @When("he go to available furniture tap")
    public void he_go_to_available_furniture_tap() {
        TenantPage page = (TenantPage) Application.openedPage;
        page.setSelectedTab(TenantPage.FURNITURE);
    }

    @Then("he should see all available furniture:")
    public void he_should_see_all_available_furniture(io.cucumber.datatable.DataTable dataTable) {
        TenantPage page = (TenantPage) Application.openedPage;
        DefaultTableModel dtm = page.getFurnitureTableModel();
        List<Map<String, String>> desiredData = dataTable.asMaps(String.class, String.class);
        int cnt = desiredData.size();
        for (int i = 0; i < cnt; i++) {
            Assert.assertEquals(Long.parseLong(desiredData.get(i).get("furniture_id")), dtm.getValueAt(i, 1));
            Assert.assertEquals(desiredData.get(i).get("name"), dtm.getValueAt(i, 2));
            Assert.assertEquals(Integer.parseInt(desiredData.get(i).get("price")), dtm.getValueAt(i, 3));
        }
    }

    @When("he click on {int} row index in the table")
    public void he_click_on_row_index_in_the_table(Integer ind) {
        TenantPage page = (TenantPage) Application.openedPage;
        page.selectFurnitureByIndex(ind);
    }

    @Then("he should see this furniture info:")
    public void he_should_see_this_furniture_info(io.cucumber.datatable.DataTable dataTable) {
        TenantPage page = (TenantPage) Application.openedPage;
        Map<String, String> desiredData = dataTable.asMaps(String.class, String.class).get(0);

        // data not show in fields because select row not simulate the real mouse click on row

        Assert.assertEquals(desiredData.get("furniture_id"), page.getFurnitureIdField());
        Assert.assertEquals(desiredData.get("tenant_name"), page.getFurnitureOwnerName());
        Assert.assertEquals(desiredData.get("name"), page.getFurnitureNameField());
        Assert.assertEquals(desiredData.get("description"), page.getFurnitureDescField());
        Assert.assertEquals(desiredData.get("tenant_phone"), page.getFurnitureOwnerPhone());
    }

    @Given("he click on add new furniture button")
    public void he_click_on_add_new_furniture_button() {
        TenantPage page = (TenantPage) Application.openedPage;
        page.pressAddFurnitureButton();
    }

    @Given("enter these new furniture info in fields:")
    public void enter_these_new_furniture_info_in_fields(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        TenantPage page = (TenantPage) Application.openedPage;
        page.setNewFurnitureNameField(data.get("name"));
        page.setNewFurnitureDescField(data.get("description"));
        page.setNewFurniturePriceField(data.get("price"));
    }

    @Given("press add new furniture submit button")
    public void press_add_new_furniture_submit_button() {
        TenantPage page = (TenantPage) Application.openedPage;
        page.pressAddNewFurnitureSubmitButton();
    }

    @Then("he should redirected to all furniture panel")
    public void he_should_redirected_to_all_furniture_panel() {
        TenantPage page = (TenantPage) Application.openedPage;
        Assert.assertTrue(page.getAllFurniturePanel().isVisible());
    }

    @Then("a new furniture will be added at last of table:")
    public void a_new_furniture_will_be_added_at_last_of_table(io.cucumber.datatable.DataTable dataTable) {
        TenantPage page = (TenantPage) Application.openedPage;
        DefaultTableModel dtm = page.getFurnitureTableModel();
        Map<String, String> desired = dataTable.asMaps(String.class, String.class).get(0);
        int lastRowIndex = dtm.getRowCount() - 1;
        Assert.assertNotEquals(1, dtm.getValueAt(lastRowIndex, 1));
        Assert.assertNotEquals(2, dtm.getValueAt(lastRowIndex, 1));
        Assert.assertEquals(dtm.getValueAt(lastRowIndex, 2), desired.get("name"));
        Assert.assertEquals(dtm.getValueAt(lastRowIndex, 3), Integer.parseInt(desired.get("price")));
    }

    @Then("error message will displayed {string}")
    public void errorMessageWillDisplayed(String msg) {
        TenantPage page = (TenantPage) Application.openedPage;
        Assert.assertEquals(msg, page.getAddNewFurnitureMessageLabel());
    }
}

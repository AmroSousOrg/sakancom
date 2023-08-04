package sakancom.test;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.OwnerPage;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OwnerPageDefinition {

    @Then("homePanel will appear in the owner page form")
    public void home_panel_will_appear_in_the_owner_page_form() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        Assert.assertEquals(OwnerPage.HOME, page.getSelectedTab());
    }

    @Given("an owner go to {string} tab")
    public void an_owner_go_to_tab(String tab) {
        int selectedTab;
        switch (tab) {
            case "home":
                selectedTab = OwnerPage.HOME;
                break;
            case "account":
                selectedTab = OwnerPage.ACCOUNT;
                break;
            case "my housing":
                selectedTab = OwnerPage.HOUSING;
                break;
            case "booking requests":
                selectedTab = OwnerPage.REQUESTS;
                break;
            case "add housing":
                selectedTab = OwnerPage.ADD_HOUSING;
                break;
            default:
                selectedTab = 0;
                break;
        }
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

    @Then("owner should see all requests to his housing")
    public void owner_should_see_all_requests_to_his_housing(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        OwnerPage page = (OwnerPage) Application.openedPage;
        DefaultTableModel tm = page.getRequestsTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> {
            Assert.assertEquals(row.get("reservation_id"), String.valueOf(tm.getValueAt(cnt.get(), 1)));
            Assert.assertEquals(row.get("housing_id"), String.valueOf(tm.getValueAt(cnt.get(), 2)));
            Assert.assertEquals(row.get("tenant_id"), String.valueOf(tm.getValueAt(cnt.get(), 3)));
            Assert.assertEquals(row.get("reservation_date"), String.valueOf(tm.getValueAt(cnt.get(), 4)));
            Assert.assertEquals(row.get("floor"), String.valueOf(tm.getValueAt(cnt.get(), 5)));
            Assert.assertEquals(row.get("apart"), String.valueOf(tm.getValueAt(cnt.getAndIncrement(), 6)));
        });
    }

    @When("owner choose {int} row in requests table")
    public void owner_choose_row_in_requests_table(Integer int1) {
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.setSelectedRequestRow(int1);
    }

    @When("owner press request details button")
    public void owner_press_request_details_button() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.pressRequestDetailsButton();
    }

    @Then("owner should see reservation details")
    public void owner_should_see_reservation_details() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        Assert.assertNotEquals("", page.getRequestId());
        Assert.assertNotEquals("", page.getRequestTenantId());
        Assert.assertNotEquals("", page.getRequestHousingId());
        Assert.assertNotEquals("", page.getRequestOwnerName());
    }

    @And("owner press on accept button")
    public void ownerPressOnAcceptButton() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.pressRequestAcceptButton();
    }

    @Then("reservation with id {int} will accepted")
    public void reservationWithIdWillAccepted(int id) {
        try {
            Connection conn = Database.makeConnection();
            String query = "select accepted from reservations where reservation_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(1, rs.getInt("accepted"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @And("owner press on reject button")
    public void ownerPressOnRejectButton() {
        OwnerPage page = (OwnerPage) Application.openedPage;
        page.pressRequestRejectButton();
    }

    @Then("reservation with id {int} will deleted")
    public void reservationWithIdWillDeleted(int id) {
        try {
            Connection conn = Database.makeConnection();
            String query = "select accepted from reservations where reservation_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            Assert.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

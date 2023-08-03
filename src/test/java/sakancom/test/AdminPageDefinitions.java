package sakancom.test;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.AdminPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminPageDefinitions {

    @Then("homePanel will appear in the admin page form")
    public void home_panel_will_appear_in_the_admin_page_form() {
        Assert.assertEquals(AdminPage.HOME, ((AdminPage)Application.openedPage).getSelectedTab());
    }

    @When("an admin go to {string} tab")
    public void an_admin_go_to_tab(String str) {
        AdminPage page = (AdminPage)Application.openedPage;
        int tab;
        switch (str) {
            case "account":
                tab = AdminPage.ACCOUNT;
                break;
            case "housing":
                tab = AdminPage.HOUSING;
                break;
            case "reservations":
                tab = AdminPage.RESERVATIONS;
                break;
            case "furniture":
                tab = AdminPage.FURNITURE;
                break;
            case "requests":
                tab = AdminPage.REQUESTS;
                break;
            case "tenants":
                tab = AdminPage.TENANTS;
                break;
            case "owners":
                tab = AdminPage.OWNERS;
                break;
            default:
                tab = 0;
                break;
        }
        page.setSelectedTab(tab);
        Assert.assertEquals(tab, page.getSelectedTab());
    }

    @Then("he should see all reservations:")
    public void he_should_see_all_reservations(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        AdminPage page = (AdminPage)Application.openedPage;
        DefaultTableModel tm = page.getReservationsTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> {
            Assert.assertEquals(row.get("reservation_id"), String.valueOf(tm.getValueAt(cnt.get(), 1)));
            Assert.assertEquals(row.get("tenant_id"), String.valueOf(tm.getValueAt(cnt.get(), 3)));
            Assert.assertEquals(row.get("housing_id"), String.valueOf(tm.getValueAt(cnt.get(), 2)));
            Assert.assertEquals(row.get("floor_num"), String.valueOf(tm.getValueAt(cnt.get(), 5)));
            Assert.assertEquals(row.get("apart_num"), String.valueOf(tm.getValueAt(cnt.get(), 6)));
            Assert.assertEquals(row.get("accepted"), String.valueOf(tm.getValueAt(cnt.getAndIncrement(), 7)));
        });
    }

    @When("he select {int} row index in the reservations table")
    public void he_select_row_index_in_the_reservations_table(Integer int1) {
        AdminPage page = (AdminPage) Application.openedPage;
        page.setSelectedReservationRow(int1);
    }

    @When("press reservation details button")
    public void press_reservation_details_button() {
        AdminPage page = (AdminPage) Application.openedPage;
        page.pressReservationDetailsButton();
    }

    @Then("he should see this reservation info:")
    public void he_should_see_this_reservation_info(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> list = dataTable.asMaps(String.class, String.class).get(0);
        AdminPage page = (AdminPage) Application.openedPage;
        Assert.assertEquals(list.get("reservation_id"), page.getVBookingId());
        Assert.assertEquals(list.get("housing_id"), page.getVHousingId());
        Assert.assertEquals(list.get("owner_id"), page.getVOwnerId());
        Assert.assertEquals(list.get("tenant_id"), page.getVTenantId());
    }

    @Then("he should see all advertisements:")
    public void heShouldSeeAllAdvertisements(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        AdminPage page = (AdminPage)Application.openedPage;
        DefaultTableModel tm = page.getRequestsTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> {
            Assert.assertEquals(row.get("name"), String.valueOf(tm.getValueAt(cnt.get(), 1)));
            Assert.assertEquals(row.get("location"), String.valueOf(tm.getValueAt(cnt.get(), 2)));
            Assert.assertEquals(row.get("rent"), String.valueOf(tm.getValueAt(cnt.get(), 3)));
            Assert.assertEquals(row.get("owner"), String.valueOf(tm.getValueAt(cnt.getAndIncrement(), 4)));
        });
    }

    @When("he select {int} row index in the requests table")
    public void heSelectRowIndexInTheRequestsTable(Integer int1) {
        AdminPage page = (AdminPage)Application.openedPage;
        page.setSelectedRequestsRow(int1);
    }

    @When("press request house details button")
    public void pressRequestHouseDetailsButton() {
        AdminPage page = (AdminPage)Application.openedPage;
        page.pressRequestHouseDetailsButton();
    }

    @Then("he should see advertisement info:")
    public void heShouldSeeAdvertisementInfo(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> list = dataTable.asMaps(String.class, String.class).get(0);
        AdminPage page = (AdminPage) Application.openedPage;
        Assert.assertEquals(list.get("housing_id"), page.getRequestHouseId());
        Assert.assertEquals(list.get("owner"), page.getRequestOwnerName());
        Assert.assertEquals(list.get("services"), page.getRequestHouseServices());
    }

    @When("admin press on accept advertisement button")
    public void adminPressOnAcceptAdvertisementButton() {
        AdminPage page = (AdminPage)Application.openedPage;
        page.pressAcceptAdvertisementButton();
    }

    @Then("new housing with this info will be added to database:")
    public void newHousingWithThisInfoWillBeAddedToDatabase(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        String name = data.get("name");
        String ownerId = data.get("owner_id");
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select `housing_id` from `housing` where name = '" + name + "' and " +
                    "`owner_id` = " + ownerId, conn);
            Assert.assertTrue(rs.next());
            rs.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @When("admin press on reject advertisement button")
    public void adminPressOnRejectAdvertisementButton() {
        AdminPage page = (AdminPage)Application.openedPage;
        page.pressRejectAdvertisementButton();
    }

    @Then("housing advertisement with id {int} will be deleted from database")
    public void housingAdvertisementWithIdWillBeDeletedFromDatabase(int del_id) {
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select `name` from `housing` where `housing_id` = " + del_id, conn);
            Assert.assertFalse(rs.next());
            rs.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Then("admin should see all furniture:")
    public void admin_should_see_all_furniture(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        AdminPage page = (AdminPage)Application.openedPage;
        DefaultTableModel tm = page.getFurnitureTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> {
            Assert.assertEquals(row.get("furniture_id"), String.valueOf(tm.getValueAt(cnt.get(), 1)));
            Assert.assertEquals(row.get("name"), String.valueOf(tm.getValueAt(cnt.get(), 2)));
            Assert.assertEquals(row.get("price"), String.valueOf(tm.getValueAt(cnt.getAndIncrement(), 3)));
        });
    }

    @When("admin select {int} row index in the furniture table")
    public void admin_select_row_index_in_the_furniture_table(Integer ind) {
        AdminPage page = (AdminPage)Application.openedPage;
        page.setSelectedFurnitureRow(ind);
    }

    @Then("admin should see furniture info:")
    public void admin_should_see_furniture_info(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> list = dataTable.asMaps(String.class, String.class).get(0);
        AdminPage page = (AdminPage) Application.openedPage;
        Assert.assertEquals(list.get("furniture_id"), page.getFurnitureId());
        Assert.assertEquals(list.get("tenant"), page.getFurnitureOwner());
        Assert.assertEquals(list.get("name"), page.getFurnitureName());
        Assert.assertEquals(list.get("description"), page.getFurnitureDesc());
        Assert.assertEquals(list.get("tenant_phone"), page.getFurniturePhone());
    }

    @Then("admin should see all houses:")
    public void admin_should_see_all_houses(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> list = dataTable.asMaps(String.class, String.class);
        AdminPage page = (AdminPage)Application.openedPage;
        DefaultTableModel tm = page.getHousesTableModel();
        AtomicInteger cnt = new AtomicInteger();
        list.forEach(row -> {
            Assert.assertEquals(row.get("name"), String.valueOf(tm.getValueAt(cnt.get(), 1)));
            Assert.assertEquals(row.get("location"), String.valueOf(tm.getValueAt(cnt.get(), 2)));
            Assert.assertEquals(row.get("rent"), String.valueOf(tm.getValueAt(cnt.get(), 3)));
            Assert.assertEquals(row.get("owner"), String.valueOf(tm.getValueAt(cnt.getAndIncrement(), 4)));
        });
    }

    @When("he select {int} row index in the houses table")
    public void he_select_row_index_in_the_houses_table(Integer int1) {
        AdminPage page = (AdminPage)Application.openedPage;
        page.setSelectedHousesRow(int1);
    }

    @When("admin press house details button")
    public void admin_press_house_details_button() {
        AdminPage page = (AdminPage)Application.openedPage;
        page.pressHouseDetailsButton();
    }

    @Then("he should see all house info:")
    public void he_should_see_all_house_info(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> list = dataTable.asMaps(String.class, String.class).get(0);
        AdminPage page = (AdminPage) Application.openedPage;
        Assert.assertEquals(list.get("housing_id"), page.getHouseId());
        Assert.assertEquals(list.get("owner_name"), page.getHouseOwnerName());
        Assert.assertEquals(list.get("services"), page.getHouseServices());
        Assert.assertEquals(list.get("location"), page.getHouseLocation());
        Assert.assertEquals(list.get("name"), page.getHouseName());
    }

    @When("admin press edit house button")
    public void admin_press_edit_house_button() {
        AdminPage page = (AdminPage) Application.openedPage;
        page.pressEditHouseButton();
    }

    @When("admin put these info in the fields:")
    public void admin_put_these_info_in_the_fields(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        AdminPage page = (AdminPage) Application.openedPage;
        page.setHouseName(data.get("name"));
        page.setHouseLocation(data.get("location"));
        page.setHouseServices(data.get("services"));
        page.setHouseRent(data.get("rent"));
        page.setElectricity(data.get("electricity_inclusive").equals("1"));
        page.setWater(data.get("water_inclusive").equals("1"));
        page.setHouseFloors(data.get("floors"));
        page.setHouseApart(data.get("apart_per_floor"));
    }

    @When("admin press save house edit button")
    public void admin_press_save_house_edit_button() {
        AdminPage page = (AdminPage) Application.openedPage;
        page.pressSaveHouseEditButton();
    }

    @Then("admin should see this message {string}")
    public void admin_should_see_this_message(String string) {
        AdminPage page = (AdminPage) Application.openedPage;
        Assert.assertEquals(string, page.getOneHouseMessageLabel());
    }

    @And("house with these info exist in database")
    public void houseWithTheseInfoExistInDatabase(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).get(0);
        try {
            ResultSet rs = Database.getHouse(Long.parseLong(data.get("housing_id")));
            Assert.assertTrue(rs.next());
            Assert.assertEquals(data.get("name"), rs.getString("name"));
            Assert.assertEquals(data.get("location"), rs.getString("location"));
            Assert.assertEquals(data.get("rent"), String.valueOf(rs.getInt("rent")));
            Assert.assertEquals(data.get("water_inclusive"), String.valueOf(rs.getInt("water_inclusive")));
            Assert.assertEquals(data.get("electricity_inclusive"), String.valueOf(rs.getString("electricity_inclusive")));
            Assert.assertEquals(data.get("services"), rs.getString("services"));
            Assert.assertEquals(data.get("floors"), String.valueOf(rs.getInt("floors")));
            Assert.assertEquals(data.get("apart_per_floor"), String.valueOf(rs.getInt("apart_per_floor")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
package sakancom.test;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import sakancom.Application;
import sakancom.common.Database;
import sakancom.pages.AdminPage;
import sakancom.pages.TenantPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.desktop.AppForegroundListener;
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
        int tab = 0;
        if (str.equals("account")) tab = AdminPage.REQUESTS;
        else if (str.equals("housing")) tab = AdminPage.HOUSING;
        else if (str.equals("reservations")) tab = AdminPage.RESERVATIONS;
        else if (str.equals("furniture")) tab = AdminPage.FURNITURE;
        else if (str.equals("requests")) tab = AdminPage.REQUESTS;
        else if (str.equals("tenants")) tab = AdminPage.TENANTS;
        else if (str.equals("owners")) tab = AdminPage.OWNERS;
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
            Assert.assertEquals(row.get("owner"), String.valueOf(tm.getValueAt(cnt.get(), 4)));
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
}
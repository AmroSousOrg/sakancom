/*
 * Created by JFormDesigner on Tue Jul 11 23:19:26 IDT 2023
 */

package sakancom.pages;

import java.awt.event.*;
import sakancom.common.Database;
import sakancom.common.Functions;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @author amroo
 */
@SuppressWarnings("FieldCanBeLocal")
public class AdminPage extends JFrame {

    private final HashMap<String, Object> adminData;
    public AdminPage(HashMap<String, Object> adminData) {
        this.adminData = adminData;
        initComponents();
        setTitle("Admin Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        customInitComponent();
    }

    private void customInitComponent() {
        fillHousesTable();
        fillPersonalInfo();
        fillReservationsTable();
        fillFurnitureTable();
        fillRequestsTable();

        housesTable.getTableHeader().setReorderingAllowed(false);
        furnitureTable.getTableHeader().setReorderingAllowed(false);
        reservationsTable.getTableHeader().setReorderingAllowed(false);
        requestsTable.getTableHeader().setReorderingAllowed(false);

        furnitureTable.getSelectionModel().addListSelectionListener(e -> furnitureTableSelectionChanged());
    }

    public void fillHousesTable() {
        Functions.fillTable("SELECT `housing`.`NAME`, `housing`.`LOCATION`, `housing`.`RENT`, `owners`.`name` " +
                "from `housing`, `owners` WHERE `housing`.`AVAILABLE` =  '1' and `housing`.`owner_id` = `owners`.`owner_id`", housesTable);
    }

    public void fillRequestsTable() {
        Functions.fillTable("SELECT `housing`.`NAME`, `housing`.`LOCATION`, `housing`.`RENT`, `owners`.`name` " +
                "from `housing`, `owners` WHERE `housing`.`AVAILABLE` =  '0' and `housing`.`owner_id` = `owners`.`owner_id`", requestsTable);
    }

    public void fillReservationsTable() {
        Functions.fillTable("SELECT `reservation_id`, `housing_id`, `tenant_id`, `reservation_date`, " +
                "`floor_num`, `apart_num`, `accepted` from `invoice` order by `reservation_id`", reservationsTable);
    }

    public void fillFurnitureTable() {
        Functions.fillTable("SELECT `furniture_id`, `name`, `price` from `furniture`", furnitureTable);
    }

    private void fillPersonalInfo() {
        newPasswordField.setText("");
        oldPasswordField.setText("");
        retypeField.setText("");
        accountPanelMessageLabel.setText("");
        accountPanelMessageLabel.setForeground(Color.red);
        idField.setText(String.valueOf((long)adminData.get("admin_id")));
        nameField.setText((String)adminData.get("name"));
        emailField.setText((String)adminData.get("email"));
        phoneField.setText((String)adminData.get("phone"));
    }

    private void showHouse() {
        int selected = housesTable.getSelectedRow();
        if (selected == -1) return;
        String name = (String)housesTable.getValueAt(selected, 1);
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery(
                    "SELECT * from `housing` where `name` = '"+name+"'",
                    conn
            );
            rs.next();
            HashMap<String, Object> houseData = Functions.rsToHashMap(rs);
            long id = (long)houseData.get("owner_id");
            rs = Database.getQuery("SELECT `name`, `phone` FROM `owners` WHERE `owner_id` = "+id , conn);
            if (rs.next())
            {
                houseData.put("owner_name", rs.getString("name"));
                houseData.put("owner_phone", rs.getString("phone"));
            }
            showHouseInfoPanel(houseData);
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHouseInfoPanel(HashMap<String, Object> houseData) {
        housingPanel.removeAll();
        housingPanel.add(oneHousePanel);
        houseId.setText(String.valueOf((long)houseData.get("housing_id")));
        houseName.setText((String)houseData.get("name"));
        houseLocation.setText((String)houseData.get("location"));
        houseRent.setText(String.valueOf((int)houseData.get("rent")));
        if ((Integer)houseData.get("water_inclusive") == 1) {
            waterYes.setSelected(true);
            waterNo.setSelected(false);
        }
        else {
            waterYes.setSelected(false);
            waterNo.setSelected(true);
        }
        if ((Integer)houseData.get("electricity_inclusive") == 1) {
            electricityYes.setSelected(true);
            electricityNo.setSelected(false);
        }
        else {
            electricityYes.setSelected(false);
            electricityNo.setSelected(true);
        }
        houseServices.setText((String)houseData.get("services"));
        int floors = (int)houseData.get("floors");
        floorsNumber.setText(String.valueOf(floors));
        apartPerFloor.setText(String.valueOf(((int)houseData.get("apart_per_floor"))));
        ownerName.setText((String)houseData.get("owner_name"));
        ownerPhone.setText((String)houseData.get("owner_phone"));
        housePicture.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/housingPhoto/" + houseData.get("picture")))));
        housingPanel.repaint();
        housingPanel.revalidate();
    }

    private void closeOneHouseMouseClicked() {
        Functions.switchChildPanel(housingPanel, allHousesPanel);
    }

    private void reservationDetails() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) return;
        Functions.switchChildPanel(reservationsPanel, invoicePanel);
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select * from `invoice` where `reservation_id` = "+
                    reservationsTable.getValueAt(selectedRow, 1), conn);
            rs.next();
            HashMap<String, Object> invoice_data = Functions.rsToHashMap(rs);
            conn.close();
            fillInvoiceInfo(invoice_data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillInvoiceInfo(HashMap<String, Object> data) {
        vBookingId.setText(String.valueOf((long)data.get("reservation_id")));
        var date = data.get("reservation_date");
        vBookingDate.setText(date.toString());
        vBookingFloor.setText(String.valueOf((int)data.get("floor_num")));
        vBookingApart.setText(String.valueOf((int)data.get("apart_num")));
        vBookingRent.setText(String.valueOf((int)data.get("rent")));
        if ((Integer)data.get("water_inclusive") == 1) {
            vWaterYes.setSelected(true);
            vWaterNo.setSelected(false);
        }
        else {
            vWaterYes.setSelected(false);
            vWaterNo.setSelected(true);
        }
        if ((Integer)data.get("electricity_inclusive") == 1) {
            vElectricityYes.setSelected(true);
            vElectricityNo.setSelected(false);
        }
        else {
            vElectricityYes.setSelected(false);
            vElectricityNo.setSelected(true);
        }
        vHousingId.setText(String.valueOf((long)data.get("housing_id")));
        vHousingName.setText((String)data.get("housing_name"));
        vHousingLocation.setText((String)data.get("location"));
        vTenantId.setText(String.valueOf((long)data.get("tenant_id")));
        vTenantName.setText((String)data.get("tenant_name"));
        vTenantAge.setText(String.valueOf((int)data.get("tenant_age")));
        vTenantPhone.setText((String)data.get("tenant_phone"));
        vTenantEmail.setText((String)data.get("tenant_email"));
        vTenantMajor.setText((String)data.get("university_major"));
        vOwnerId.setText(String.valueOf((long)data.get("owner_id")));
        vOwnerName.setText((String)data.get("owner_name"));
        vOwnerEmail.setText((String)data.get("owner_email"));
        vOwnerPhone.setText((String)data.get("owner_phone"));
    }

    private void closeInvoicePanelMouseClicked() {
        Functions.switchChildPanel(reservationsPanel, houseReservationPanel);
    }

    private void acceptReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) return;
        if ((int)reservationsTable.getValueAt(selectedRow, 7) == 1) {
            JOptionPane.showMessageDialog(this, "This reservation is already accepted.",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection conn = Database.makeConnection();
            String query = "update `reservations` set `accepted` = '1' where reservation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            long rid = (long)reservationsTable.getValueAt(selectedRow, 1);
            pstmt.setLong(1, rid);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            reservationsTable.setValueAt(1, selectedRow, 7);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void rejectReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) return;
        if ((int)reservationsTable.getValueAt(selectedRow, 7) == 1) {
            JOptionPane.showMessageDialog(this, "You cannot reject accepted request.",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection conn = Database.makeConnection();
            String query = "delete from `reservations` where reservation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            long rid = (long)reservationsTable.getValueAt(selectedRow, 1);
            pstmt.setLong(1, rid);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            fillReservationsTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void furnitureTableSelectionChanged() {
        Connection conn;
        int selectedRow = furnitureTable.getSelectedRow();
        if (selectedRow == -1) return;
        try {
            conn = Database.makeConnection();
            String query = "SELECT `furniture`.`furniture_id` as 'furniture_id', `furniture`.`name` as 'furniture_name', `furniture`.`description` as 'description', `tenants`.`name` as 'owner_name', " +
                    "`tenants`.`phone` as 'phone' from `furniture`, `tenants` where `furniture`.`tenant_id` = `tenants`.`tenant_id` and `furniture`.`furniture_id` = ?";
            long id = (long) furnitureTable.getValueAt(selectedRow, 1);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                furnitureId.setText(rs.getString("furniture_id"));
                furnitureName.setText(rs.getString("furniture_name"));
                furnitureDesc.setText(rs.getString("description"));
                furnitureOwner.setText(rs.getString("owner_name"));
                furniturePhone.setText(rs.getString("phone"));
            }
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void requestHouseDetails() {
        int selected = requestsTable.getSelectedRow();
        if (selected == -1) return;
        String name = (String)requestsTable.getValueAt(selected, 1);
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery(
                    "SELECT * from `housing` where `name` = '"+name+"'",
                    conn
            );
            rs.next();
            HashMap<String, Object> houseData = Functions.rsToHashMap(rs);
            long id = (long)houseData.get("owner_id");
            rs = Database.getQuery("SELECT `name`, `phone` FROM `owners` WHERE `owner_id` = "+id , conn);
            if (rs.next())
            {
                houseData.put("owner_name", rs.getString("name"));
                houseData.put("owner_phone", rs.getString("phone"));
            }
            showRequestHouseInfoPanel(houseData);
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRequestHouseInfoPanel(HashMap<String, Object> houseData) {
        requestsPanel.removeAll();
        requestsPanel.add(requestHouseInfoPanel);
        requestHouseId.setText(String.valueOf((long)houseData.get("housing_id")));
        requestHouseName.setText((String)houseData.get("name"));
        requestHouseLocation.setText((String)houseData.get("location"));
        requestHouseRent.setText(String.valueOf((int)houseData.get("rent")));
        if ((Integer)houseData.get("water_inclusive") == 1) {
            requestWaterYes.setSelected(true);
            requestWaterNo.setSelected(false);
        }
        else {
            requestWaterYes.setSelected(false);
            requestWaterNo.setSelected(true);
        }
        if ((Integer)houseData.get("electricity_inclusive") == 1) {
            requestElecYes.setSelected(true);
            requestElecNo.setSelected(false);
        }
        else {
            requestElecYes.setSelected(false);
            requestElecNo.setSelected(true);
        }
        requestHouseServices.setText((String)houseData.get("services"));
        int floors = (int)houseData.get("floors");
        requestHouseFloor.setText(String.valueOf(floors));
        requestHouseApart.setText(String.valueOf(((int)houseData.get("apart_per_floor"))));
        requestOwnerName.setText((String)houseData.get("owner_name"));
        requestOwnerPhone.setText((String)houseData.get("owner_phone"));
        requestHousePicture.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/housingPhoto/" + houseData.get("picture")))));
        requestsPanel.repaint();
        requestsPanel.revalidate();
    }

    private void closeOneHouse2MouseClicked() {
        Functions.switchChildPanel(requestsPanel, requestsTablePanel);
    }

    private void acceptRequest() {
        long id = Long.parseLong(requestHouseId.getText());
        String query = "update `housing` set `available` = '1' where `housing_id` = ?";
        try {
            Connection conn = Database.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            Functions.switchChildPanel(requestsPanel, requestsTablePanel);
            fillRequestsTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectRequest() {
        long id = Long.parseLong(requestHouseId.getText());
        String query = "delete from `housing` where `housing_id` = ?";
        try {
            Connection conn = Database.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            Functions.switchChildPanel(requestsPanel, requestsTablePanel);
            fillRequestsTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteHouse() {
        long id = Long.parseLong(houseId.getText());
        Connection conn;
        PreparedStatement pstmt;
        ResultSet rs;
        String query, error = "";
        
        try {
            query = "select `reservation_id` from `invoice` where `housing_id` = ?";
            conn = Database.makeConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                error = "You can't delete this house, because there are reservations on it.";
            }
            else {
                query = "delete from `housing` where `housing_id` = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
                Functions.switchChildPanel(housingPanel, allHousesPanel);
                fillHousesTable();
            }
            pstmt.close();
            conn.close();
            if (!error.equals("")) {
                oneHouseMessageLabel.setText(error);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassowrd() {
        accountPanelMessageLabel.setText("");
        accountPanelMessageLabel.setForeground(Color.red);
        String oldPass = String.valueOf(oldPasswordField.getPassword());
        String retype = String.valueOf(retypeField.getPassword());
        String newPass = String.valueOf(newPasswordField.getPassword());
        String error = "";

        if (oldPass.isEmpty()) error = "Old password field is empty.";
        else if (retype.isEmpty()) error = "Retype pass field is empty.";
        else if (newPass.isEmpty()) error = "New password field is empty.";
        else if (!newPass.equals(retype)) error = "Mismatch passwords.";
        else {
            try {
                Connection conn = Database.makeConnection();
                ResultSet rs = Database.getQuery("select `name` from `admin` where `admin_id` = " +
                        adminData.get("admin_id") + " and `password` = '" + Functions.sha256(oldPass) + "'", conn);
                if (rs.next()) {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("update `admin` set `password` = '" + Functions.sha256(newPass) +
                            "' where `admin_id` = " + adminData.get("admin_id"));
                    stmt.close();
                    accountPanelMessageLabel.setForeground(Color.green);
                    accountPanelMessageLabel.setText("password updated.");
                    newPasswordField.setText("");
                    oldPasswordField.setText("");
                    retypeField.setText("");
                }
                else {
                    error = "Incorrect password.";
                }
                conn.close();
            } catch (SQLException | NoSuchAlgorithmException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (!error.isEmpty()) {
            accountPanelMessageLabel.setText(error);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro
        mainPanel = new JTabbedPane();
        homePanel = new JPanel();
        accountPanel = new JPanel();
        adminAccountPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        separator1 = new JSeparator();
        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        editProfileButton = new JButton();
        saveProfileButton = new JButton();
        accountPanelMessageLabel = new JLabel();
        changePassowrdButton = new JButton();
        separator2 = new JSeparator();
        label11 = new JLabel();
        label12 = new JLabel();
        label13 = new JLabel();
        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        retypeField = new JPasswordField();
        housingPanel = new JPanel();
        allHousesPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        housesTable = new JTable();
        textField7 = new JTextField();
        label9 = new JLabel();
        showHouse = new JButton();
        oneHousePanel = new JPanel();
        label14 = new JLabel();
        label15 = new JLabel();
        label16 = new JLabel();
        label17 = new JLabel();
        houseId = new JTextField();
        houseName = new JTextField();
        houseLocation = new JTextField();
        houseRent = new JTextField();
        housePicture = new JLabel();
        label19 = new JLabel();
        label21 = new JLabel();
        waterYes = new JRadioButton();
        waterNo = new JRadioButton();
        electricityYes = new JRadioButton();
        electricityNo = new JRadioButton();
        label20 = new JLabel();
        scrollPane2 = new JScrollPane();
        houseServices = new JTextArea();
        separator3 = new JSeparator();
        label18 = new JLabel();
        label22 = new JLabel();
        floorsNumber = new JTextField();
        apartPerFloor = new JTextField();
        label23 = new JLabel();
        label24 = new JLabel();
        ownerName = new JTextField();
        ownerPhone = new JTextField();
        separator4 = new JSeparator();
        editHouseInfo = new JButton();
        oneHouseMessageLabel = new JLabel();
        closeOneHouse = new JLabel();
        saveHouseInfo = new JButton();
        deleteHouseButton = new JButton();
        reservationsPanel = new JPanel();
        houseReservationPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        reservationsTable = new JTable();
        acceptReservationButton = new JButton();
        rejectReservationButton = new JButton();
        reservationDetailsButton = new JButton();
        textField8 = new JTextField();
        label10 = new JLabel();
        invoicePanel = new JPanel();
        label33 = new JLabel();
        label34 = new JLabel();
        separator5 = new JSeparator();
        separator6 = new JSeparator();
        vBookingId = new JTextField();
        label35 = new JLabel();
        vBookingDate = new JTextField();
        label36 = new JLabel();
        separator7 = new JSeparator();
        label37 = new JLabel();
        label38 = new JLabel();
        label39 = new JLabel();
        vHousingId = new JTextField();
        vHousingName = new JTextField();
        vHousingLocation = new JTextField();
        label40 = new JLabel();
        vBookingFloor = new JTextField();
        label41 = new JLabel();
        vBookingApart = new JTextField();
        separator8 = new JSeparator();
        label42 = new JLabel();
        label43 = new JLabel();
        separator9 = new JSeparator();
        vTenantId = new JTextField();
        label44 = new JLabel();
        vTenantName = new JTextField();
        label45 = new JLabel();
        separator11 = new JSeparator();
        label46 = new JLabel();
        label47 = new JLabel();
        label48 = new JLabel();
        vOwnerId = new JTextField();
        vOwnerName = new JTextField();
        vOwnerPhone = new JTextField();
        label49 = new JLabel();
        vTenantAge = new JTextField();
        label50 = new JLabel();
        vTenantPhone = new JTextField();
        separator12 = new JSeparator();
        closeInvoicePanel = new JLabel();
        label52 = new JLabel();
        vTenantEmail = new JTextField();
        label53 = new JLabel();
        vTenantMajor = new JTextField();
        label54 = new JLabel();
        vOwnerEmail = new JTextField();
        label51 = new JLabel();
        vBookingRent = new JTextField();
        label55 = new JLabel();
        vWaterYes = new JRadioButton();
        vWaterNo = new JRadioButton();
        vElectricityYes = new JRadioButton();
        vElectricityNo = new JRadioButton();
        label56 = new JLabel();
        furniturePanel = new JPanel();
        showFurnituresPanel = new JPanel();
        label28 = new JLabel();
        scrollPane4 = new JScrollPane();
        furnitureTable = new JTable();
        label29 = new JLabel();
        label30 = new JLabel();
        label31 = new JLabel();
        label32 = new JLabel();
        furnitureName = new JTextField();
        furnitureOwner = new JTextField();
        furniturePhone = new JTextField();
        scrollPane5 = new JScrollPane();
        furnitureDesc = new JTextPane();
        searchFurnitureField = new JTextField();
        searchFurnitureButton = new JLabel();
        label57 = new JLabel();
        furnitureId = new JTextField();
        requestsPanel = new JPanel();
        requestsTablePanel = new JPanel();
        label6 = new JLabel();
        scrollPane6 = new JScrollPane();
        requestsTable = new JTable();
        requestHouseDetails = new JButton();
        requestHouseInfoPanel = new JPanel();
        label25 = new JLabel();
        label26 = new JLabel();
        label27 = new JLabel();
        label58 = new JLabel();
        requestHouseId = new JTextField();
        requestHouseName = new JTextField();
        requestHouseLocation = new JTextField();
        requestHouseRent = new JTextField();
        requestHousePicture = new JLabel();
        label59 = new JLabel();
        label60 = new JLabel();
        requestWaterYes = new JRadioButton();
        requestWaterNo = new JRadioButton();
        requestElecYes = new JRadioButton();
        requestElecNo = new JRadioButton();
        label61 = new JLabel();
        scrollPane7 = new JScrollPane();
        requestHouseServices = new JTextArea();
        separator10 = new JSeparator();
        label62 = new JLabel();
        label63 = new JLabel();
        requestHouseFloor = new JTextField();
        requestHouseApart = new JTextField();
        label64 = new JLabel();
        label65 = new JLabel();
        requestOwnerName = new JTextField();
        requestOwnerPhone = new JTextField();
        separator13 = new JSeparator();
        acceptRequestButton = new JButton();
        houseRequestMessageLabel = new JLabel();
        closeOneHouse2 = new JLabel();
        rejectRequestButton = new JButton();
        tenantsPanel = new JPanel();
        ownersPanel = new JPanel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {

            //======== homePanel ========
            {
                homePanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border.
                EmptyBorder( 0, 0, 0, 0) , "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e", javax. swing. border. TitledBorder. CENTER, javax. swing
                . border. TitledBorder. BOTTOM, new java .awt .Font ("Dialo\u0067" ,java .awt .Font .BOLD ,12 ),
                java. awt. Color. red) ,homePanel. getBorder( )) ); homePanel. addPropertyChangeListener (new java. beans. PropertyChangeListener( )
                { @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("borde\u0072" .equals (e .getPropertyName () ))
                throw new RuntimeException( ); }} );
                homePanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < homePanel.getComponentCount(); i++) {
                        Rectangle bounds = homePanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = homePanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    homePanel.setMinimumSize(preferredSize);
                    homePanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("HOME", homePanel);

            //======== accountPanel ========
            {
                accountPanel.setLayout(null);

                //======== adminAccountPanel ========
                {
                    adminAccountPanel.setLayout(null);

                    //---- label1 ----
                    label1.setText("Admin Account");
                    label1.setFont(new Font("Bodoni MT", label1.getFont().getStyle(), label1.getFont().getSize() + 15));
                    label1.setForeground(Color.blue);
                    adminAccountPanel.add(label1);
                    label1.setBounds(25, 20, 220, 50);

                    //---- label2 ----
                    label2.setText("ID:");
                    label2.setFont(new Font("SimSun", Font.BOLD, 20));
                    adminAccountPanel.add(label2);
                    label2.setBounds(85, 130, 80, 30);

                    //---- label3 ----
                    label3.setText("Name:");
                    label3.setFont(new Font("SimSun", Font.BOLD, 20));
                    adminAccountPanel.add(label3);
                    label3.setBounds(85, 175, 80, 30);

                    //---- label4 ----
                    label4.setText("Email:");
                    label4.setFont(new Font("SimSun", Font.BOLD, 20));
                    adminAccountPanel.add(label4);
                    label4.setBounds(85, 220, 80, 30);

                    //---- label5 ----
                    label5.setText("Phone:");
                    label5.setFont(new Font("SimSun", Font.BOLD, 20));
                    adminAccountPanel.add(label5);
                    label5.setBounds(85, 265, 80, 30);
                    adminAccountPanel.add(separator1);
                    separator1.setBounds(30, 75, 925, 10);

                    //---- idField ----
                    idField.setFont(new Font("SimSun", Font.PLAIN, 20));
                    idField.setEnabled(false);
                    idField.setDisabledTextColor(new Color(0x666666));
                    adminAccountPanel.add(idField);
                    idField.setBounds(175, 135, 220, idField.getPreferredSize().height);

                    //---- nameField ----
                    nameField.setFont(new Font("SimSun", Font.PLAIN, 20));
                    nameField.setEnabled(false);
                    nameField.setDisabledTextColor(new Color(0x666666));
                    adminAccountPanel.add(nameField);
                    nameField.setBounds(175, 180, 220, 30);

                    //---- emailField ----
                    emailField.setFont(new Font("SimSun", Font.PLAIN, 20));
                    emailField.setEnabled(false);
                    emailField.setDisabledTextColor(new Color(0x666666));
                    adminAccountPanel.add(emailField);
                    emailField.setBounds(175, 225, 220, 30);

                    //---- phoneField ----
                    phoneField.setFont(new Font("SimSun", Font.PLAIN, 20));
                    phoneField.setEnabled(false);
                    phoneField.setDisabledTextColor(new Color(0x666666));
                    adminAccountPanel.add(phoneField);
                    phoneField.setBounds(175, 270, 220, 30);

                    //---- editProfileButton ----
                    editProfileButton.setText("Edit");
                    editProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                    adminAccountPanel.add(editProfileButton);
                    editProfileButton.setBounds(new Rectangle(new Point(120, 380), editProfileButton.getPreferredSize()));

                    //---- saveProfileButton ----
                    saveProfileButton.setText("Save");
                    saveProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                    adminAccountPanel.add(saveProfileButton);
                    saveProfileButton.setBounds(255, 380, 100, 30);

                    //---- accountPanelMessageLabel ----
                    accountPanelMessageLabel.setForeground(Color.red);
                    accountPanelMessageLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
                    adminAccountPanel.add(accountPanelMessageLabel);
                    accountPanelMessageLabel.setBounds(415, 400, 540, 30);

                    //---- changePassowrdButton ----
                    changePassowrdButton.setText("Change your password");
                    changePassowrdButton.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                    changePassowrdButton.addActionListener(e -> changePassowrd());
                    adminAccountPanel.add(changePassowrdButton);
                    changePassowrdButton.setBounds(660, 320, 205, 30);

                    //---- separator2 ----
                    separator2.setOrientation(SwingConstants.VERTICAL);
                    adminAccountPanel.add(separator2);
                    separator2.setBounds(505, 110, 15, 240);

                    //---- label11 ----
                    label11.setText("Old Password:");
                    label11.setFont(new Font("SimSun", Font.PLAIN, 18));
                    adminAccountPanel.add(label11);
                    label11.setBounds(525, 155, 135, 30);

                    //---- label12 ----
                    label12.setText("New Password:");
                    label12.setFont(new Font("SimSun", Font.PLAIN, 18));
                    adminAccountPanel.add(label12);
                    label12.setBounds(525, 210, 135, 30);

                    //---- label13 ----
                    label13.setText("Retype:");
                    label13.setFont(new Font("SimSun", Font.PLAIN, 18));
                    adminAccountPanel.add(label13);
                    label13.setBounds(525, 260, 135, 30);
                    adminAccountPanel.add(oldPasswordField);
                    oldPasswordField.setBounds(660, 155, 205, 30);
                    adminAccountPanel.add(newPasswordField);
                    newPasswordField.setBounds(660, 210, 205, 30);
                    adminAccountPanel.add(retypeField);
                    retypeField.setBounds(660, 265, 205, 30);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < adminAccountPanel.getComponentCount(); i++) {
                            Rectangle bounds = adminAccountPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = adminAccountPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        adminAccountPanel.setMinimumSize(preferredSize);
                        adminAccountPanel.setPreferredSize(preferredSize);
                    }
                }
                accountPanel.add(adminAccountPanel);
                adminAccountPanel.setBounds(0, 0, 990, 470);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < accountPanel.getComponentCount(); i++) {
                        Rectangle bounds = accountPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = accountPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    accountPanel.setMinimumSize(preferredSize);
                    accountPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("ACCOUNT", accountPanel);

            //======== housingPanel ========
            {
                housingPanel.setLayout(new CardLayout());

                //======== allHousesPanel ========
                {
                    allHousesPanel.setEnabled(false);
                    allHousesPanel.setLayout(null);

                    //======== scrollPane1 ========
                    {

                        //---- housesTable ----
                        housesTable.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                        housesTable.setFillsViewportHeight(true);
                        housesTable.setUpdateSelectionOnSort(false);
                        housesTable.setRowMargin(2);
                        housesTable.setFont(new Font("SimSun", Font.PLAIN, 18));
                        housesTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null, null},
                            },
                            new String[] {
                                "#", "Name", "Location", "Rent", "Owner"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, String.class, String.class, Integer.class, String.class
                            };
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false, false
                            };
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return columnTypes[columnIndex];
                            }
                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return columnEditable[columnIndex];
                            }
                        });
                        {
                            TableColumnModel cm = housesTable.getColumnModel();
                            cm.getColumn(0).setResizable(false);
                            cm.getColumn(0).setMinWidth(50);
                            cm.getColumn(0).setMaxWidth(50);
                            cm.getColumn(1).setResizable(false);
                            cm.getColumn(1).setMinWidth(160);
                            cm.getColumn(1).setMaxWidth(160);
                            cm.getColumn(2).setResizable(false);
                            cm.getColumn(3).setResizable(false);
                            cm.getColumn(3).setMinWidth(80);
                            cm.getColumn(3).setMaxWidth(80);
                            cm.getColumn(4).setResizable(false);
                            cm.getColumn(4).setMinWidth(180);
                            cm.getColumn(4).setMaxWidth(180);
                        }
                        housesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        scrollPane1.setViewportView(housesTable);
                    }
                    allHousesPanel.add(scrollPane1);
                    scrollPane1.setBounds(35, 110, 770, 325);

                    //---- textField7 ----
                    textField7.setToolTipText("search by name");
                    textField7.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    allHousesPanel.add(textField7);
                    textField7.setBounds(50, 35, 280, textField7.getPreferredSize().height);

                    //---- label9 ----
                    label9.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    allHousesPanel.add(label9);
                    label9.setBounds(360, 35, 30, 30);

                    //---- showHouse ----
                    showHouse.setText("House Details");
                    showHouse.setToolTipText("view selected house");
                    showHouse.addActionListener(e -> showHouse());
                    allHousesPanel.add(showHouse);
                    showHouse.setBounds(835, 185, 125, 40);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < allHousesPanel.getComponentCount(); i++) {
                            Rectangle bounds = allHousesPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = allHousesPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        allHousesPanel.setMinimumSize(preferredSize);
                        allHousesPanel.setPreferredSize(preferredSize);
                    }
                }
                housingPanel.add(allHousesPanel, "card1");

                //======== oneHousePanel ========
                {
                    oneHousePanel.setLayout(null);

                    //---- label14 ----
                    label14.setText("ID:");
                    label14.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label14);
                    label14.setBounds(25, 25, 115, 30);

                    //---- label15 ----
                    label15.setText("Name:");
                    label15.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label15);
                    label15.setBounds(25, 70, 115, 30);

                    //---- label16 ----
                    label16.setText("Location:");
                    label16.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label16);
                    label16.setBounds(25, 115, 115, 30);

                    //---- label17 ----
                    label17.setText("Rent:");
                    label17.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label17);
                    label17.setBounds(25, 160, 115, 30);

                    //---- houseId ----
                    houseId.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseId.setBackground(new Color(0xededed));
                    houseId.setDisabledTextColor(new Color(0x333333));
                    houseId.setEnabled(false);
                    oneHousePanel.add(houseId);
                    houseId.setBounds(145, 25, 200, 30);

                    //---- houseName ----
                    houseName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseName.setEnabled(false);
                    houseName.setDisabledTextColor(new Color(0x333333));
                    oneHousePanel.add(houseName);
                    houseName.setBounds(145, 70, 200, 30);

                    //---- houseLocation ----
                    houseLocation.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseLocation.setEnabled(false);
                    houseLocation.setBackground(new Color(0xededed));
                    houseLocation.setDisabledTextColor(new Color(0x333333));
                    oneHousePanel.add(houseLocation);
                    houseLocation.setBounds(145, 115, 200, houseLocation.getPreferredSize().height);

                    //---- houseRent ----
                    houseRent.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseRent.setEnabled(false);
                    houseRent.setBackground(new Color(0xededed));
                    houseRent.setDisabledTextColor(new Color(0x333333));
                    oneHousePanel.add(houseRent);
                    houseRent.setBounds(145, 160, 200, houseRent.getPreferredSize().height);

                    //---- housePicture ----
                    housePicture.setBackground(new Color(0xcccccc));
                    housePicture.setIcon(null);
                    oneHousePanel.add(housePicture);
                    housePicture.setBounds(710, 60, 245, 220);

                    //---- label19 ----
                    label19.setText("-include water:");
                    label19.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label19);
                    label19.setBounds(20, 200, 220, 30);

                    //---- label21 ----
                    label21.setText("-include electricity:");
                    label21.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label21);
                    label21.setBounds(20, 245, 230, 30);

                    //---- waterYes ----
                    waterYes.setText("YES");
                    waterYes.setEnabled(false);
                    oneHousePanel.add(waterYes);
                    waterYes.setBounds(250, 210, 50, waterYes.getPreferredSize().height);

                    //---- waterNo ----
                    waterNo.setText("NO");
                    waterNo.setEnabled(false);
                    oneHousePanel.add(waterNo);
                    waterNo.setBounds(320, 210, 50, 22);

                    //---- electricityYes ----
                    electricityYes.setText("YES");
                    electricityYes.setEnabled(false);
                    oneHousePanel.add(electricityYes);
                    electricityYes.setBounds(250, 250, 50, 22);

                    //---- electricityNo ----
                    electricityNo.setText("NO");
                    electricityNo.setEnabled(false);
                    oneHousePanel.add(electricityNo);
                    electricityNo.setBounds(320, 250, 50, 22);

                    //---- label20 ----
                    label20.setText("Services:");
                    label20.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label20);
                    label20.setBounds(25, 285, 105, 30);

                    //======== scrollPane2 ========
                    {

                        //---- houseServices ----
                        houseServices.setEnabled(false);
                        houseServices.setDisabledTextColor(new Color(0x333333));
                        houseServices.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                        houseServices.setWrapStyleWord(true);
                        houseServices.setLineWrap(true);
                        scrollPane2.setViewportView(houseServices);
                    }
                    oneHousePanel.add(scrollPane2);
                    scrollPane2.setBounds(130, 290, 240, 145);

                    //---- separator3 ----
                    separator3.setOrientation(SwingConstants.VERTICAL);
                    separator3.setForeground(new Color(0x999999));
                    oneHousePanel.add(separator3);
                    separator3.setBounds(390, 35, 10, 390);

                    //---- label18 ----
                    label18.setText("# Floors:");
                    label18.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label18);
                    label18.setBounds(410, 65, 110, 30);

                    //---- label22 ----
                    label22.setText("# Apartment/Floor:");
                    label22.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label22);
                    label22.setBounds(410, 110, 200, 35);

                    //---- floorsNumber ----
                    floorsNumber.setFont(new Font("SimSun", Font.PLAIN, 18));
                    floorsNumber.setBackground(new Color(0xededed));
                    floorsNumber.setDisabledTextColor(new Color(0x333333));
                    floorsNumber.setEnabled(false);
                    floorsNumber.setForeground(new Color(0x666666));
                    oneHousePanel.add(floorsNumber);
                    floorsNumber.setBounds(530, 65, 105, 30);

                    //---- apartPerFloor ----
                    apartPerFloor.setFont(new Font("SimSun", Font.PLAIN, 18));
                    apartPerFloor.setBackground(new Color(0xededed));
                    apartPerFloor.setDisabledTextColor(new Color(0x333333));
                    apartPerFloor.setEnabled(false);
                    apartPerFloor.setForeground(new Color(0x666666));
                    oneHousePanel.add(apartPerFloor);
                    apartPerFloor.setBounds(620, 115, 55, 30);

                    //---- label23 ----
                    label23.setText("Owner:");
                    label23.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label23);
                    label23.setBounds(410, 170, 85, 30);

                    //---- label24 ----
                    label24.setText("Phone:");
                    label24.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label24);
                    label24.setBounds(410, 225, 85, 30);

                    //---- ownerName ----
                    ownerName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    ownerName.setBackground(new Color(0xededed));
                    ownerName.setDisabledTextColor(new Color(0x333333));
                    ownerName.setEnabled(false);
                    ownerName.setForeground(new Color(0x666666));
                    oneHousePanel.add(ownerName);
                    ownerName.setBounds(505, 170, 165, 30);

                    //---- ownerPhone ----
                    ownerPhone.setFont(new Font("SimSun", Font.PLAIN, 18));
                    ownerPhone.setBackground(new Color(0xededed));
                    ownerPhone.setDisabledTextColor(new Color(0x333333));
                    ownerPhone.setEnabled(false);
                    ownerPhone.setForeground(new Color(0x666666));
                    oneHousePanel.add(ownerPhone);
                    ownerPhone.setBounds(505, 225, 165, 30);

                    //---- separator4 ----
                    separator4.setForeground(new Color(0x999999));
                    oneHousePanel.add(separator4);
                    separator4.setBounds(405, 315, 565, 10);

                    //---- editHouseInfo ----
                    editHouseInfo.setText("EDIT");
                    oneHousePanel.add(editHouseInfo);
                    editHouseInfo.setBounds(495, 370, 117, 35);

                    //---- oneHouseMessageLabel ----
                    oneHouseMessageLabel.setForeground(Color.red);
                    oneHouseMessageLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
                    oneHousePanel.add(oneHouseMessageLabel);
                    oneHouseMessageLabel.setBounds(430, 420, 510, 25);

                    //---- closeOneHouse ----
                    closeOneHouse.setIcon(null);
                    closeOneHouse.setText("X");
                    closeOneHouse.setHorizontalAlignment(SwingConstants.CENTER);
                    closeOneHouse.setFont(new Font("Snap ITC", Font.PLAIN, 28));
                    closeOneHouse.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            closeOneHouseMouseClicked();
                        }
                    });
                    oneHousePanel.add(closeOneHouse);
                    closeOneHouse.setBounds(910, 10, 40, 35);

                    //---- saveHouseInfo ----
                    saveHouseInfo.setText("SAVE");
                    oneHousePanel.add(saveHouseInfo);
                    saveHouseInfo.setBounds(665, 370, 117, 35);

                    //---- deleteHouseButton ----
                    deleteHouseButton.setText("DELETE");
                    deleteHouseButton.addActionListener(e -> deleteHouse());
                    oneHousePanel.add(deleteHouseButton);
                    deleteHouseButton.setBounds(820, 370, 117, 35);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < oneHousePanel.getComponentCount(); i++) {
                            Rectangle bounds = oneHousePanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = oneHousePanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        oneHousePanel.setMinimumSize(preferredSize);
                        oneHousePanel.setPreferredSize(preferredSize);
                    }
                }
                housingPanel.add(oneHousePanel, "card2");
            }
            mainPanel.addTab("HOUSING", housingPanel);

            //======== reservationsPanel ========
            {
                reservationsPanel.setLayout(new CardLayout());

                //======== houseReservationPanel ========
                {
                    houseReservationPanel.setLayout(null);

                    //======== scrollPane3 ========
                    {

                        //---- reservationsTable ----
                        reservationsTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null, null, null, null, null},
                                {null, null, null, null, null, null, null, null},
                            },
                            new String[] {
                                "#", "Reservation_ID", "House_ID", "Tenant_ID", "Reservation_date", "floor", "apartment", "Accepted"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, Long.class, Long.class, Long.class, String.class, Integer.class, Integer.class, Integer.class
                            };
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false, false, false, false, false
                            };
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return columnTypes[columnIndex];
                            }
                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return columnEditable[columnIndex];
                            }
                        });
                        {
                            TableColumnModel cm = reservationsTable.getColumnModel();
                            cm.getColumn(0).setMinWidth(60);
                            cm.getColumn(0).setMaxWidth(60);
                            cm.getColumn(5).setMinWidth(60);
                            cm.getColumn(5).setMaxWidth(60);
                            cm.getColumn(6).setMinWidth(80);
                            cm.getColumn(6).setMaxWidth(80);
                            cm.getColumn(7).setMinWidth(80);
                            cm.getColumn(7).setMaxWidth(80);
                        }
                        scrollPane3.setViewportView(reservationsTable);
                    }
                    houseReservationPanel.add(scrollPane3);
                    scrollPane3.setBounds(20, 115, 840, 310);

                    //---- acceptReservationButton ----
                    acceptReservationButton.setText("Accept");
                    acceptReservationButton.setToolTipText("accept selected request");
                    acceptReservationButton.addActionListener(e -> acceptReservation());
                    houseReservationPanel.add(acceptReservationButton);
                    acceptReservationButton.setBounds(880, 200, 92, 30);

                    //---- rejectReservationButton ----
                    rejectReservationButton.setText("Reject");
                    rejectReservationButton.setToolTipText("reject selected request");
                    rejectReservationButton.addActionListener(e -> rejectReservation());
                    houseReservationPanel.add(rejectReservationButton);
                    rejectReservationButton.setBounds(880, 250, 92, 30);

                    //---- reservationDetailsButton ----
                    reservationDetailsButton.setText("Details");
                    reservationDetailsButton.addActionListener(e -> reservationDetails());
                    houseReservationPanel.add(reservationDetailsButton);
                    reservationDetailsButton.setBounds(880, 150, 92, 30);

                    //---- textField8 ----
                    textField8.setToolTipText("search by name");
                    textField8.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    houseReservationPanel.add(textField8);
                    textField8.setBounds(45, 45, 280, 33);

                    //---- label10 ----
                    label10.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    houseReservationPanel.add(label10);
                    label10.setBounds(355, 45, 30, 30);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < houseReservationPanel.getComponentCount(); i++) {
                            Rectangle bounds = houseReservationPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = houseReservationPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        houseReservationPanel.setMinimumSize(preferredSize);
                        houseReservationPanel.setPreferredSize(preferredSize);
                    }
                }
                reservationsPanel.add(houseReservationPanel, "card3");

                //======== invoicePanel ========
                {
                    invoicePanel.setLayout(null);

                    //---- label33 ----
                    label33.setText("Booking Details");
                    label33.setHorizontalAlignment(SwingConstants.CENTER);
                    label33.setFont(new Font("SimSun", Font.PLAIN, 20));
                    invoicePanel.add(label33);
                    label33.setBounds(20, 45, 185, 40);

                    //---- label34 ----
                    label34.setText("ID:");
                    label34.setHorizontalAlignment(SwingConstants.CENTER);
                    label34.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label34);
                    label34.setBounds(30, 95, 55, 25);

                    //---- separator5 ----
                    separator5.setForeground(new Color(0x999999));
                    invoicePanel.add(separator5);
                    separator5.setBounds(205, 65, 265, 15);

                    //---- separator6 ----
                    separator6.setForeground(new Color(0x999999));
                    separator6.setOrientation(SwingConstants.VERTICAL);
                    invoicePanel.add(separator6);
                    separator6.setBounds(15, 65, 10, 380);

                    //---- vBookingId ----
                    vBookingId.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vBookingId.setEnabled(false);
                    vBookingId.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vBookingId);
                    vBookingId.setBounds(80, 95, 90, 25);

                    //---- label35 ----
                    label35.setText("Date:");
                    label35.setHorizontalAlignment(SwingConstants.CENTER);
                    label35.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label35);
                    label35.setBounds(180, 95, 65, 25);

                    //---- vBookingDate ----
                    vBookingDate.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vBookingDate.setEnabled(false);
                    vBookingDate.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vBookingDate);
                    vBookingDate.setBounds(240, 95, 220, 25);

                    //---- label36 ----
                    label36.setText("Housing Details");
                    label36.setHorizontalAlignment(SwingConstants.CENTER);
                    label36.setFont(new Font("SimSun", Font.PLAIN, 20));
                    invoicePanel.add(label36);
                    label36.setBounds(20, 290, 185, 40);

                    //---- separator7 ----
                    separator7.setForeground(new Color(0x999999));
                    invoicePanel.add(separator7);
                    separator7.setBounds(205, 310, 265, 15);

                    //---- label37 ----
                    label37.setText("ID:");
                    label37.setHorizontalAlignment(SwingConstants.CENTER);
                    label37.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label37);
                    label37.setBounds(30, 345, 50, 30);

                    //---- label38 ----
                    label38.setText("Name:");
                    label38.setHorizontalAlignment(SwingConstants.CENTER);
                    label38.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label38);
                    label38.setBounds(175, 345, 65, 30);

                    //---- label39 ----
                    label39.setText("Location:");
                    label39.setHorizontalAlignment(SwingConstants.CENTER);
                    label39.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label39);
                    label39.setBounds(45, 395, 105, 30);

                    //---- vHousingId ----
                    vHousingId.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vHousingId.setEnabled(false);
                    vHousingId.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vHousingId);
                    vHousingId.setBounds(80, 350, 80, 25);

                    //---- vHousingName ----
                    vHousingName.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vHousingName.setEnabled(false);
                    vHousingName.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vHousingName);
                    vHousingName.setBounds(235, 345, 225, 30);

                    //---- vHousingLocation ----
                    vHousingLocation.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vHousingLocation.setEnabled(false);
                    vHousingLocation.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vHousingLocation);
                    vHousingLocation.setBounds(150, 395, 290, 30);

                    //---- label40 ----
                    label40.setText("Floor:");
                    label40.setHorizontalAlignment(SwingConstants.CENTER);
                    label40.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label40);
                    label40.setBounds(35, 135, 80, 30);

                    //---- vBookingFloor ----
                    vBookingFloor.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vBookingFloor.setEnabled(false);
                    vBookingFloor.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vBookingFloor);
                    vBookingFloor.setBounds(115, 140, 60, 25);

                    //---- label41 ----
                    label41.setText("Apartment:");
                    label41.setHorizontalAlignment(SwingConstants.CENTER);
                    label41.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label41);
                    label41.setBounds(185, 135, 120, 30);

                    //---- vBookingApart ----
                    vBookingApart.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vBookingApart.setEnabled(false);
                    vBookingApart.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vBookingApart);
                    vBookingApart.setBounds(310, 140, 60, 25);

                    //---- separator8 ----
                    separator8.setForeground(new Color(0x999999));
                    separator8.setOrientation(SwingConstants.VERTICAL);
                    invoicePanel.add(separator8);
                    separator8.setBounds(470, 65, 10, 380);

                    //---- label42 ----
                    label42.setText("Tenant Details");
                    label42.setHorizontalAlignment(SwingConstants.CENTER);
                    label42.setFont(new Font("SimSun", Font.PLAIN, 20));
                    invoicePanel.add(label42);
                    label42.setBounds(480, 45, 185, 40);

                    //---- label43 ----
                    label43.setText("ID:");
                    label43.setHorizontalAlignment(SwingConstants.CENTER);
                    label43.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label43);
                    label43.setBounds(485, 90, 60, 25);

                    //---- separator9 ----
                    separator9.setForeground(new Color(0x999999));
                    invoicePanel.add(separator9);
                    separator9.setBounds(665, 65, 265, 15);

                    //---- vTenantId ----
                    vTenantId.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantId.setEnabled(false);
                    vTenantId.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantId);
                    vTenantId.setBounds(540, 90, 80, 25);

                    //---- label44 ----
                    label44.setText("Name:");
                    label44.setHorizontalAlignment(SwingConstants.CENTER);
                    label44.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label44);
                    label44.setBounds(640, 90, 75, 25);

                    //---- vTenantName ----
                    vTenantName.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantName.setEnabled(false);
                    vTenantName.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantName);
                    vTenantName.setBounds(715, 90, 200, 25);

                    //---- label45 ----
                    label45.setText("Owner Details");
                    label45.setHorizontalAlignment(SwingConstants.CENTER);
                    label45.setFont(new Font("SimSun", Font.PLAIN, 20));
                    invoicePanel.add(label45);
                    label45.setBounds(480, 245, 185, 30);

                    //---- separator11 ----
                    separator11.setForeground(new Color(0x999999));
                    invoicePanel.add(separator11);
                    separator11.setBounds(665, 260, 265, 15);

                    //---- label46 ----
                    label46.setText("ID:");
                    label46.setHorizontalAlignment(SwingConstants.CENTER);
                    label46.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label46);
                    label46.setBounds(495, 295, 50, 25);

                    //---- label47 ----
                    label47.setText("Name:");
                    label47.setHorizontalAlignment(SwingConstants.CENTER);
                    label47.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label47);
                    label47.setBounds(630, 295, 70, 25);

                    //---- label48 ----
                    label48.setText("Phone:");
                    label48.setHorizontalAlignment(SwingConstants.CENTER);
                    label48.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label48);
                    label48.setBounds(495, 330, 75, 30);

                    //---- vOwnerId ----
                    vOwnerId.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vOwnerId.setEnabled(false);
                    vOwnerId.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vOwnerId);
                    vOwnerId.setBounds(540, 295, 80, 25);

                    //---- vOwnerName ----
                    vOwnerName.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vOwnerName.setEnabled(false);
                    vOwnerName.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vOwnerName);
                    vOwnerName.setBounds(690, 295, 230, 25);

                    //---- vOwnerPhone ----
                    vOwnerPhone.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vOwnerPhone.setEnabled(false);
                    vOwnerPhone.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vOwnerPhone);
                    vOwnerPhone.setBounds(565, 335, 245, 25);

                    //---- label49 ----
                    label49.setText("Age:");
                    label49.setHorizontalAlignment(SwingConstants.CENTER);
                    label49.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label49);
                    label49.setBounds(490, 135, 55, 25);

                    //---- vTenantAge ----
                    vTenantAge.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantAge.setEnabled(false);
                    vTenantAge.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantAge);
                    vTenantAge.setBounds(545, 135, 65, 25);

                    //---- label50 ----
                    label50.setText("Phone:");
                    label50.setHorizontalAlignment(SwingConstants.CENTER);
                    label50.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label50);
                    label50.setBounds(630, 135, 75, 25);

                    //---- vTenantPhone ----
                    vTenantPhone.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantPhone.setEnabled(false);
                    vTenantPhone.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantPhone);
                    vTenantPhone.setBounds(700, 135, 210, 30);

                    //---- separator12 ----
                    separator12.setForeground(new Color(0x999999));
                    separator12.setOrientation(SwingConstants.VERTICAL);
                    invoicePanel.add(separator12);
                    separator12.setBounds(930, 65, 10, 380);

                    //---- closeInvoicePanel ----
                    closeInvoicePanel.setIcon(null);
                    closeInvoicePanel.setText("X");
                    closeInvoicePanel.setHorizontalAlignment(SwingConstants.CENTER);
                    closeInvoicePanel.setFont(new Font("Snap ITC", Font.PLAIN, 28));
                    closeInvoicePanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            closeInvoicePanelMouseClicked();
                        }
                    });
                    invoicePanel.add(closeInvoicePanel);
                    closeInvoicePanel.setBounds(925, 5, 40, 35);

                    //---- label52 ----
                    label52.setText("Email:");
                    label52.setHorizontalAlignment(SwingConstants.CENTER);
                    label52.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label52);
                    label52.setBounds(495, 175, 70, 25);

                    //---- vTenantEmail ----
                    vTenantEmail.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantEmail.setEnabled(false);
                    vTenantEmail.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantEmail);
                    vTenantEmail.setBounds(560, 175, 290, 25);

                    //---- label53 ----
                    label53.setText("Major:");
                    label53.setHorizontalAlignment(SwingConstants.CENTER);
                    label53.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label53);
                    label53.setBounds(495, 210, 70, 25);

                    //---- vTenantMajor ----
                    vTenantMajor.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vTenantMajor.setEnabled(false);
                    vTenantMajor.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vTenantMajor);
                    vTenantMajor.setBounds(565, 210, 235, 25);

                    //---- label54 ----
                    label54.setText("Email:");
                    label54.setHorizontalAlignment(SwingConstants.CENTER);
                    label54.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label54);
                    label54.setBounds(495, 370, 75, 30);

                    //---- vOwnerEmail ----
                    vOwnerEmail.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vOwnerEmail.setEnabled(false);
                    vOwnerEmail.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vOwnerEmail);
                    vOwnerEmail.setBounds(565, 375, 245, 25);

                    //---- label51 ----
                    label51.setText("Rent:");
                    label51.setHorizontalAlignment(SwingConstants.CENTER);
                    label51.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label51);
                    label51.setBounds(40, 180, 75, 30);

                    //---- vBookingRent ----
                    vBookingRent.setFont(new Font("SimSun", Font.PLAIN, 16));
                    vBookingRent.setEnabled(false);
                    vBookingRent.setDisabledTextColor(new Color(0x333333));
                    invoicePanel.add(vBookingRent);
                    vBookingRent.setBounds(110, 185, 110, 25);

                    //---- label55 ----
                    label55.setText("Include Water:");
                    label55.setHorizontalAlignment(SwingConstants.CENTER);
                    label55.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label55);
                    label55.setBounds(80, 220, 160, 30);

                    //---- vWaterYes ----
                    vWaterYes.setText("YES");
                    vWaterYes.setEnabled(false);
                    invoicePanel.add(vWaterYes);
                    vWaterYes.setBounds(270, 225, 50, 22);

                    //---- vWaterNo ----
                    vWaterNo.setText("NO");
                    vWaterNo.setEnabled(false);
                    invoicePanel.add(vWaterNo);
                    vWaterNo.setBounds(340, 225, 50, 22);

                    //---- vElectricityYes ----
                    vElectricityYes.setText("YES");
                    vElectricityYes.setEnabled(false);
                    invoicePanel.add(vElectricityYes);
                    vElectricityYes.setBounds(270, 265, 50, 22);

                    //---- vElectricityNo ----
                    vElectricityNo.setText("NO");
                    vElectricityNo.setEnabled(false);
                    invoicePanel.add(vElectricityNo);
                    vElectricityNo.setBounds(340, 265, 50, 22);

                    //---- label56 ----
                    label56.setText("Include Electricity:");
                    label56.setHorizontalAlignment(SwingConstants.CENTER);
                    label56.setFont(new Font("SimSun", Font.PLAIN, 18));
                    invoicePanel.add(label56);
                    label56.setBounds(25, 255, 220, 30);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < invoicePanel.getComponentCount(); i++) {
                            Rectangle bounds = invoicePanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = invoicePanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        invoicePanel.setMinimumSize(preferredSize);
                        invoicePanel.setPreferredSize(preferredSize);
                    }
                }
                reservationsPanel.add(invoicePanel, "card4");
            }
            mainPanel.addTab("RESERVATIONS", reservationsPanel);

            //======== furniturePanel ========
            {
                furniturePanel.setLayout(null);

                //======== showFurnituresPanel ========
                {
                    showFurnituresPanel.setLayout(null);

                    //---- label28 ----
                    label28.setText("Furnitures Shop");
                    label28.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 26));
                    label28.setForeground(Color.blue);
                    showFurnituresPanel.add(label28);
                    label28.setBounds(30, 15, 200, 50);

                    //======== scrollPane4 ========
                    {

                        //---- furnitureTable ----
                        furnitureTable.setFillsViewportHeight(true);
                        furnitureTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null},
                            },
                            new String[] {
                                "#", "ID", "Name", "Price"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, Integer.class, String.class, Integer.class
                            };
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false
                            };
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return columnTypes[columnIndex];
                            }
                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return columnEditable[columnIndex];
                            }
                        });
                        {
                            TableColumnModel cm = furnitureTable.getColumnModel();
                            cm.getColumn(0).setMinWidth(45);
                            cm.getColumn(0).setMaxWidth(45);
                            cm.getColumn(1).setMinWidth(70);
                            cm.getColumn(1).setMaxWidth(70);
                            cm.getColumn(3).setMinWidth(70);
                            cm.getColumn(3).setMaxWidth(70);
                        }
                        scrollPane4.setViewportView(furnitureTable);
                    }
                    showFurnituresPanel.add(scrollPane4);
                    scrollPane4.setBounds(25, 130, 355, 310);

                    //---- label29 ----
                    label29.setText("Name:");
                    label29.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label29);
                    label29.setBounds(445, 90, 95, 35);

                    //---- label30 ----
                    label30.setText("Owner:");
                    label30.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label30);
                    label30.setBounds(445, 145, 95, 35);

                    //---- label31 ----
                    label31.setText("Phone:");
                    label31.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label31);
                    label31.setBounds(445, 195, 95, 35);

                    //---- label32 ----
                    label32.setText("Description:");
                    label32.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label32);
                    label32.setBounds(445, 250, 150, 35);

                    //---- furnitureName ----
                    furnitureName.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureName.setEnabled(false);
                    furnitureName.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureName);
                    furnitureName.setBounds(565, 95, 205, furnitureName.getPreferredSize().height);

                    //---- furnitureOwner ----
                    furnitureOwner.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureOwner.setEnabled(false);
                    furnitureOwner.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureOwner);
                    furnitureOwner.setBounds(565, 150, 205, 33);

                    //---- furniturePhone ----
                    furniturePhone.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furniturePhone.setEnabled(false);
                    furniturePhone.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furniturePhone);
                    furniturePhone.setBounds(565, 195, 205, 33);

                    //======== scrollPane5 ========
                    {

                        //---- furnitureDesc ----
                        furnitureDesc.setEnabled(false);
                        furnitureDesc.setDisabledTextColor(new Color(0x333333));
                        furnitureDesc.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                        scrollPane5.setViewportView(furnitureDesc);
                    }
                    showFurnituresPanel.add(scrollPane5);
                    scrollPane5.setBounds(590, 260, 310, 145);

                    //---- searchFurnitureField ----
                    searchFurnitureField.setToolTipText("search by name");
                    searchFurnitureField.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    showFurnituresPanel.add(searchFurnitureField);
                    searchFurnitureField.setBounds(30, 80, 205, 33);

                    //---- searchFurnitureButton ----
                    searchFurnitureButton.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    showFurnituresPanel.add(searchFurnitureButton);
                    searchFurnitureButton.setBounds(250, 80, 30, 30);

                    //---- label57 ----
                    label57.setText("ID:");
                    label57.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label57);
                    label57.setBounds(445, 40, 95, 35);

                    //---- furnitureId ----
                    furnitureId.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureId.setEnabled(false);
                    furnitureId.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureId);
                    furnitureId.setBounds(565, 45, 205, 33);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < showFurnituresPanel.getComponentCount(); i++) {
                            Rectangle bounds = showFurnituresPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = showFurnituresPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        showFurnituresPanel.setMinimumSize(preferredSize);
                        showFurnituresPanel.setPreferredSize(preferredSize);
                    }
                }
                furniturePanel.add(showFurnituresPanel);
                showFurnituresPanel.setBounds(0, 0, 990, 465);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < furniturePanel.getComponentCount(); i++) {
                        Rectangle bounds = furniturePanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = furniturePanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    furniturePanel.setMinimumSize(preferredSize);
                    furniturePanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("FURNITURE", furniturePanel);

            //======== requestsPanel ========
            {
                requestsPanel.setLayout(new CardLayout());

                //======== requestsTablePanel ========
                {
                    requestsTablePanel.setLayout(null);

                    //---- label6 ----
                    label6.setText("( To accept request for advertisement of housing or regect it select the request row from the table and click details button  )");
                    label6.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    requestsTablePanel.add(label6);
                    label6.setBounds(20, 25, 935, 30);

                    //======== scrollPane6 ========
                    {

                        //---- requestsTable ----
                        requestsTable.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                        requestsTable.setFillsViewportHeight(true);
                        requestsTable.setUpdateSelectionOnSort(false);
                        requestsTable.setRowMargin(2);
                        requestsTable.setFont(new Font("SimSun", Font.PLAIN, 18));
                        requestsTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null, null},
                            },
                            new String[] {
                                "#", "Name", "Location", "Rent", "Owner"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, String.class, String.class, Integer.class, String.class
                            };
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false, false
                            };
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                return columnTypes[columnIndex];
                            }
                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                return columnEditable[columnIndex];
                            }
                        });
                        {
                            TableColumnModel cm = requestsTable.getColumnModel();
                            cm.getColumn(0).setResizable(false);
                            cm.getColumn(0).setMinWidth(50);
                            cm.getColumn(0).setMaxWidth(50);
                            cm.getColumn(1).setResizable(false);
                            cm.getColumn(2).setResizable(false);
                            cm.getColumn(3).setResizable(false);
                            cm.getColumn(3).setMinWidth(80);
                            cm.getColumn(3).setMaxWidth(80);
                            cm.getColumn(4).setResizable(false);
                            cm.getColumn(4).setMinWidth(200);
                            cm.getColumn(4).setMaxWidth(200);
                        }
                        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        scrollPane6.setViewportView(requestsTable);
                    }
                    requestsTablePanel.add(scrollPane6);
                    scrollPane6.setBounds(45, 105, 730, 325);

                    //---- requestHouseDetails ----
                    requestHouseDetails.setText("House Details");
                    requestHouseDetails.setToolTipText("view selected house");
                    requestHouseDetails.addActionListener(e -> requestHouseDetails());
                    requestsTablePanel.add(requestHouseDetails);
                    requestHouseDetails.setBounds(820, 170, 125, 40);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < requestsTablePanel.getComponentCount(); i++) {
                            Rectangle bounds = requestsTablePanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = requestsTablePanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        requestsTablePanel.setMinimumSize(preferredSize);
                        requestsTablePanel.setPreferredSize(preferredSize);
                    }
                }
                requestsPanel.add(requestsTablePanel, "card1");

                //======== requestHouseInfoPanel ========
                {
                    requestHouseInfoPanel.setLayout(null);

                    //---- label25 ----
                    label25.setText("ID:");
                    label25.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label25);
                    label25.setBounds(25, 25, 115, 30);

                    //---- label26 ----
                    label26.setText("Name:");
                    label26.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label26);
                    label26.setBounds(25, 70, 115, 30);

                    //---- label27 ----
                    label27.setText("Location:");
                    label27.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label27);
                    label27.setBounds(25, 115, 115, 30);

                    //---- label58 ----
                    label58.setText("Rent:");
                    label58.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label58);
                    label58.setBounds(25, 160, 115, 30);

                    //---- requestHouseId ----
                    requestHouseId.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseId.setBackground(new Color(0xededed));
                    requestHouseId.setDisabledTextColor(new Color(0x333333));
                    requestHouseId.setEnabled(false);
                    requestHouseInfoPanel.add(requestHouseId);
                    requestHouseId.setBounds(145, 25, 200, 30);

                    //---- requestHouseName ----
                    requestHouseName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseName.setEnabled(false);
                    requestHouseName.setDisabledTextColor(new Color(0x333333));
                    requestHouseInfoPanel.add(requestHouseName);
                    requestHouseName.setBounds(145, 70, 200, 30);

                    //---- requestHouseLocation ----
                    requestHouseLocation.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseLocation.setEnabled(false);
                    requestHouseLocation.setBackground(new Color(0xededed));
                    requestHouseLocation.setDisabledTextColor(new Color(0x333333));
                    requestHouseInfoPanel.add(requestHouseLocation);
                    requestHouseLocation.setBounds(145, 115, 200, requestHouseLocation.getPreferredSize().height);

                    //---- requestHouseRent ----
                    requestHouseRent.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseRent.setEnabled(false);
                    requestHouseRent.setBackground(new Color(0xededed));
                    requestHouseRent.setDisabledTextColor(new Color(0x333333));
                    requestHouseInfoPanel.add(requestHouseRent);
                    requestHouseRent.setBounds(145, 160, 200, requestHouseRent.getPreferredSize().height);

                    //---- requestHousePicture ----
                    requestHousePicture.setBackground(new Color(0xcccccc));
                    requestHousePicture.setIcon(null);
                    requestHouseInfoPanel.add(requestHousePicture);
                    requestHousePicture.setBounds(710, 60, 245, 220);

                    //---- label59 ----
                    label59.setText("-include water:");
                    label59.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label59);
                    label59.setBounds(20, 200, 220, 30);

                    //---- label60 ----
                    label60.setText("-include electricity:");
                    label60.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label60);
                    label60.setBounds(20, 245, 230, 30);

                    //---- requestWaterYes ----
                    requestWaterYes.setText("YES");
                    requestWaterYes.setEnabled(false);
                    requestHouseInfoPanel.add(requestWaterYes);
                    requestWaterYes.setBounds(250, 210, 50, requestWaterYes.getPreferredSize().height);

                    //---- requestWaterNo ----
                    requestWaterNo.setText("NO");
                    requestWaterNo.setEnabled(false);
                    requestHouseInfoPanel.add(requestWaterNo);
                    requestWaterNo.setBounds(320, 210, 50, 22);

                    //---- requestElecYes ----
                    requestElecYes.setText("YES");
                    requestElecYes.setEnabled(false);
                    requestHouseInfoPanel.add(requestElecYes);
                    requestElecYes.setBounds(250, 250, 50, 22);

                    //---- requestElecNo ----
                    requestElecNo.setText("NO");
                    requestElecNo.setEnabled(false);
                    requestHouseInfoPanel.add(requestElecNo);
                    requestElecNo.setBounds(320, 250, 50, 22);

                    //---- label61 ----
                    label61.setText("Services:");
                    label61.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label61);
                    label61.setBounds(25, 285, 105, 30);

                    //======== scrollPane7 ========
                    {

                        //---- requestHouseServices ----
                        requestHouseServices.setEnabled(false);
                        requestHouseServices.setDisabledTextColor(new Color(0x333333));
                        requestHouseServices.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                        requestHouseServices.setWrapStyleWord(true);
                        requestHouseServices.setLineWrap(true);
                        scrollPane7.setViewportView(requestHouseServices);
                    }
                    requestHouseInfoPanel.add(scrollPane7);
                    scrollPane7.setBounds(130, 290, 240, 145);

                    //---- separator10 ----
                    separator10.setOrientation(SwingConstants.VERTICAL);
                    separator10.setForeground(new Color(0x999999));
                    requestHouseInfoPanel.add(separator10);
                    separator10.setBounds(390, 35, 10, 390);

                    //---- label62 ----
                    label62.setText("# Floors:");
                    label62.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label62);
                    label62.setBounds(410, 65, 110, 30);

                    //---- label63 ----
                    label63.setText("# Apartment/Floor:");
                    label63.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label63);
                    label63.setBounds(410, 110, 200, 35);

                    //---- requestHouseFloor ----
                    requestHouseFloor.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseFloor.setBackground(new Color(0xededed));
                    requestHouseFloor.setDisabledTextColor(new Color(0x333333));
                    requestHouseFloor.setEnabled(false);
                    requestHouseFloor.setForeground(new Color(0x666666));
                    requestHouseInfoPanel.add(requestHouseFloor);
                    requestHouseFloor.setBounds(530, 65, 105, 30);

                    //---- requestHouseApart ----
                    requestHouseApart.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestHouseApart.setBackground(new Color(0xededed));
                    requestHouseApart.setDisabledTextColor(new Color(0x333333));
                    requestHouseApart.setEnabled(false);
                    requestHouseApart.setForeground(new Color(0x666666));
                    requestHouseInfoPanel.add(requestHouseApart);
                    requestHouseApart.setBounds(620, 115, 55, 30);

                    //---- label64 ----
                    label64.setText("Owner:");
                    label64.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label64);
                    label64.setBounds(410, 170, 85, 30);

                    //---- label65 ----
                    label65.setText("Phone:");
                    label65.setFont(new Font("SimSun", Font.PLAIN, 20));
                    requestHouseInfoPanel.add(label65);
                    label65.setBounds(410, 225, 85, 30);

                    //---- requestOwnerName ----
                    requestOwnerName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestOwnerName.setBackground(new Color(0xededed));
                    requestOwnerName.setDisabledTextColor(new Color(0x333333));
                    requestOwnerName.setEnabled(false);
                    requestOwnerName.setForeground(new Color(0x666666));
                    requestHouseInfoPanel.add(requestOwnerName);
                    requestOwnerName.setBounds(505, 170, 165, 30);

                    //---- requestOwnerPhone ----
                    requestOwnerPhone.setFont(new Font("SimSun", Font.PLAIN, 18));
                    requestOwnerPhone.setBackground(new Color(0xededed));
                    requestOwnerPhone.setDisabledTextColor(new Color(0x333333));
                    requestOwnerPhone.setEnabled(false);
                    requestOwnerPhone.setForeground(new Color(0x666666));
                    requestHouseInfoPanel.add(requestOwnerPhone);
                    requestOwnerPhone.setBounds(505, 225, 165, 30);

                    //---- separator13 ----
                    separator13.setForeground(new Color(0x999999));
                    requestHouseInfoPanel.add(separator13);
                    separator13.setBounds(405, 315, 565, 10);

                    //---- acceptRequestButton ----
                    acceptRequestButton.setText("ACCEPT");
                    acceptRequestButton.addActionListener(e -> acceptRequest());
                    requestHouseInfoPanel.add(acceptRequestButton);
                    acceptRequestButton.setBounds(495, 370, 117, 35);

                    //---- houseRequestMessageLabel ----
                    houseRequestMessageLabel.setForeground(Color.red);
                    houseRequestMessageLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
                    requestHouseInfoPanel.add(houseRequestMessageLabel);
                    houseRequestMessageLabel.setBounds(430, 420, 510, 25);

                    //---- closeOneHouse2 ----
                    closeOneHouse2.setIcon(null);
                    closeOneHouse2.setText("X");
                    closeOneHouse2.setHorizontalAlignment(SwingConstants.CENTER);
                    closeOneHouse2.setFont(new Font("Snap ITC", Font.PLAIN, 28));
                    closeOneHouse2.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            closeOneHouse2MouseClicked();
                        }
                    });
                    requestHouseInfoPanel.add(closeOneHouse2);
                    closeOneHouse2.setBounds(910, 10, 40, 35);

                    //---- rejectRequestButton ----
                    rejectRequestButton.setText("REGECT");
                    rejectRequestButton.addActionListener(e -> rejectRequest());
                    requestHouseInfoPanel.add(rejectRequestButton);
                    rejectRequestButton.setBounds(700, 370, 117, 35);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < requestHouseInfoPanel.getComponentCount(); i++) {
                            Rectangle bounds = requestHouseInfoPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = requestHouseInfoPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        requestHouseInfoPanel.setMinimumSize(preferredSize);
                        requestHouseInfoPanel.setPreferredSize(preferredSize);
                    }
                }
                requestsPanel.add(requestHouseInfoPanel, "card2");
            }
            mainPanel.addTab("REQUESTS", requestsPanel);

            //======== tenantsPanel ========
            {
                tenantsPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < tenantsPanel.getComponentCount(); i++) {
                        Rectangle bounds = tenantsPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = tenantsPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    tenantsPanel.setMinimumSize(preferredSize);
                    tenantsPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("TENANTS", tenantsPanel);

            //======== ownersPanel ========
            {
                ownersPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < ownersPanel.getComponentCount(); i++) {
                        Rectangle bounds = ownersPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = ownersPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    ownersPanel.setMinimumSize(preferredSize);
                    ownersPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("OWNERS", ownersPanel);
        }
        contentPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 990, 510);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Amro
    private JTabbedPane mainPanel;
    private JPanel homePanel;
    private JPanel accountPanel;
    private JPanel adminAccountPanel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JSeparator separator1;
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton editProfileButton;
    private JButton saveProfileButton;
    private JLabel accountPanelMessageLabel;
    private JButton changePassowrdButton;
    private JSeparator separator2;
    private JLabel label11;
    private JLabel label12;
    private JLabel label13;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField retypeField;
    private JPanel housingPanel;
    private JPanel allHousesPanel;
    private JScrollPane scrollPane1;
    private JTable housesTable;
    private JTextField textField7;
    private JLabel label9;
    private JButton showHouse;
    private JPanel oneHousePanel;
    private JLabel label14;
    private JLabel label15;
    private JLabel label16;
    private JLabel label17;
    private JTextField houseId;
    private JTextField houseName;
    private JTextField houseLocation;
    private JTextField houseRent;
    private JLabel housePicture;
    private JLabel label19;
    private JLabel label21;
    private JRadioButton waterYes;
    private JRadioButton waterNo;
    private JRadioButton electricityYes;
    private JRadioButton electricityNo;
    private JLabel label20;
    private JScrollPane scrollPane2;
    private JTextArea houseServices;
    private JSeparator separator3;
    private JLabel label18;
    private JLabel label22;
    private JTextField floorsNumber;
    private JTextField apartPerFloor;
    private JLabel label23;
    private JLabel label24;
    private JTextField ownerName;
    private JTextField ownerPhone;
    private JSeparator separator4;
    private JButton editHouseInfo;
    private JLabel oneHouseMessageLabel;
    private JLabel closeOneHouse;
    private JButton saveHouseInfo;
    private JButton deleteHouseButton;
    private JPanel reservationsPanel;
    private JPanel houseReservationPanel;
    private JScrollPane scrollPane3;
    private JTable reservationsTable;
    private JButton acceptReservationButton;
    private JButton rejectReservationButton;
    private JButton reservationDetailsButton;
    private JTextField textField8;
    private JLabel label10;
    private JPanel invoicePanel;
    private JLabel label33;
    private JLabel label34;
    private JSeparator separator5;
    private JSeparator separator6;
    private JTextField vBookingId;
    private JLabel label35;
    private JTextField vBookingDate;
    private JLabel label36;
    private JSeparator separator7;
    private JLabel label37;
    private JLabel label38;
    private JLabel label39;
    private JTextField vHousingId;
    private JTextField vHousingName;
    private JTextField vHousingLocation;
    private JLabel label40;
    private JTextField vBookingFloor;
    private JLabel label41;
    private JTextField vBookingApart;
    private JSeparator separator8;
    private JLabel label42;
    private JLabel label43;
    private JSeparator separator9;
    private JTextField vTenantId;
    private JLabel label44;
    private JTextField vTenantName;
    private JLabel label45;
    private JSeparator separator11;
    private JLabel label46;
    private JLabel label47;
    private JLabel label48;
    private JTextField vOwnerId;
    private JTextField vOwnerName;
    private JTextField vOwnerPhone;
    private JLabel label49;
    private JTextField vTenantAge;
    private JLabel label50;
    private JTextField vTenantPhone;
    private JSeparator separator12;
    private JLabel closeInvoicePanel;
    private JLabel label52;
    private JTextField vTenantEmail;
    private JLabel label53;
    private JTextField vTenantMajor;
    private JLabel label54;
    private JTextField vOwnerEmail;
    private JLabel label51;
    private JTextField vBookingRent;
    private JLabel label55;
    private JRadioButton vWaterYes;
    private JRadioButton vWaterNo;
    private JRadioButton vElectricityYes;
    private JRadioButton vElectricityNo;
    private JLabel label56;
    private JPanel furniturePanel;
    private JPanel showFurnituresPanel;
    private JLabel label28;
    private JScrollPane scrollPane4;
    private JTable furnitureTable;
    private JLabel label29;
    private JLabel label30;
    private JLabel label31;
    private JLabel label32;
    private JTextField furnitureName;
    private JTextField furnitureOwner;
    private JTextField furniturePhone;
    private JScrollPane scrollPane5;
    private JTextPane furnitureDesc;
    private JTextField searchFurnitureField;
    private JLabel searchFurnitureButton;
    private JLabel label57;
    private JTextField furnitureId;
    private JPanel requestsPanel;
    private JPanel requestsTablePanel;
    private JLabel label6;
    private JScrollPane scrollPane6;
    private JTable requestsTable;
    private JButton requestHouseDetails;
    private JPanel requestHouseInfoPanel;
    private JLabel label25;
    private JLabel label26;
    private JLabel label27;
    private JLabel label58;
    private JTextField requestHouseId;
    private JTextField requestHouseName;
    private JTextField requestHouseLocation;
    private JTextField requestHouseRent;
    private JLabel requestHousePicture;
    private JLabel label59;
    private JLabel label60;
    private JRadioButton requestWaterYes;
    private JRadioButton requestWaterNo;
    private JRadioButton requestElecYes;
    private JRadioButton requestElecNo;
    private JLabel label61;
    private JScrollPane scrollPane7;
    private JTextArea requestHouseServices;
    private JSeparator separator10;
    private JLabel label62;
    private JLabel label63;
    private JTextField requestHouseFloor;
    private JTextField requestHouseApart;
    private JLabel label64;
    private JLabel label65;
    private JTextField requestOwnerName;
    private JTextField requestOwnerPhone;
    private JSeparator separator13;
    private JButton acceptRequestButton;
    private JLabel houseRequestMessageLabel;
    private JLabel closeOneHouse2;
    private JButton rejectRequestButton;
    private JPanel tenantsPanel;
    private JPanel ownersPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

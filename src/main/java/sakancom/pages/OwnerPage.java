/*
 * Created by JFormDesigner on Sun Jul 30 23:14:57 EEST 2023
 */

package sakancom.pages;

import java.awt.event.*;
import javax.swing.table.*;

import sakancom.common.Database;
import sakancom.common.Functions;
import sakancom.common.Validation;
import sakancom.exceptions.InputValidationException;

import java.awt.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import javax.swing.*;

@SuppressWarnings("FieldCanBeLocal")
public class OwnerPage extends JFrame {

    private final HashMap<String, Object> ownerData;
    public final static int HOME = 0, ACCOUNT = 1, HOUSING = 2, REQUESTS = 3, ADD_HOUSING = 4;
    private File chosenFile;

    public OwnerPage(HashMap<String, Object> data) {

        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.ownerData = data;
        initComponents();
        customInitComponent();
    }

    private void customInitComponent() {
    }

    private void mainPanelStateChanged() {
        int selectedIndex = mainPanel.getSelectedIndex();
        if (selectedIndex == HOUSING) initHousingPanel();
        else if (selectedIndex == REQUESTS) initRequestsPanel();
        else if (selectedIndex == ACCOUNT) initAccountPanel();
        else if (selectedIndex == ADD_HOUSING) initAddHousingPanel();
    }

    private void initAddHousingPanel() {
        addHousingMessageLabel.setForeground(Color.red);
        addHousingMessageLabel.setText("");
        Functions.clearAllChildren(addHousingPanel);
        housingServices.setText("");
        houseImageLabel.setIcon(null);
        chosenFileName.setText("No chosen file");
        chosenFile = null;
        waterNo.setSelected(true);
        elecNo.setSelected(true);
    }

    private void initAccountPanel() {
        accountPanelMessageLabel.setText("");
        accountPanelMessageLabel.setForeground(Color.red);
        idField.setText(String.valueOf((long) ownerData.get("owner_id")));
        nameField.setText((String) ownerData.get("name"));
        phoneField.setText((String) ownerData.get("phone"));
        emailField.setText((String) ownerData.get("email"));
        nameField.setEnabled(false);
        phoneField.setEnabled(false);
        emailField.setEnabled(false);
    }

    private void initRequestsPanel() {
        Functions.fillTable("select reservation_id, housing_id, tenant_id, reservation_date, " +
                "floor_num, apart_num from invoice where accepted = 0 and owner_id = " +
                ownerData.get("owner_id"), requestsTable);
    }

    private void initHousingPanel() {
        Functions.fillTable("select name, location, rent from housing where " +
                "owner_id = " + ownerData.get("owner_id"), housesTable);
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(OwnerPage.this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                String filename = selectedFile.getName();
                chosenFileName.setText(filename);
                this.chosenFile = selectedFile;
                houseImageLabel.setIcon(new ImageIcon(Objects.requireNonNull(
                        getClass().getResource("/housingPhoto/" + filename))));
            }
        }
    }

    private void submitAddHouse() {
        addHousingMessageLabel.setForeground(Color.red);
        String name = housingNameField.getText();
        String location = housingLocationField.getText();
        String rent = housingRentField.getText();
        int water = waterYes.isSelected() ? 1 : 0;
        int electricity = elecYes.isSelected() ? 1 : 0;
        String services = housingServices.getText();
        String floors = floorsNumber.getText();
        String apart = apartPerFloor.getText();
        
        try {
            Validation.checkHouseName(name, 0);
            Validation.validateEmpty(location);
            Validation.checkHouseRent(rent);
            Validation.validateEmpty(services);
            Validation.checkHouseFloor(floors);
            Validation.checkHouseApart(apart);
            Validation.checkImage(chosenFile);

        } catch (SQLException | InputValidationException e) {
            addHousingMessageLabel.setText(e.getMessage());
            return;
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("location", location);
        data.put("owner_id", String.valueOf((long) ownerData.get("owner_id")));
        data.put("services", services);
        data.put("rent", rent);
        data.put("floors", floors);
        data.put("apart_per_floor", apart);
        data.put("water_inclusive", String.valueOf(water));
        data.put("electricity_inclusive", String.valueOf(electricity));
        data.put("picture", chosenFileName.getText());

        try {
            Database.addHouse(data);
        } catch (SQLException e) {
            addHousingMessageLabel.setText(e.getMessage());
            return;
        }

        initAddHousingPanel();
        addHousingMessageLabel.setForeground(Color.green);
        addHousingMessageLabel.setText("Add Housing request was sent to admin.");
    }

    private void changePassword() {
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
                ResultSet rs = Database.getQuery("select `name` from `owners` where `owner_id` = " +
                        ownerData.get("owner_id") + " and `password` = '" + Functions.sha256(oldPass) + "'", conn);
                if (rs.next()) {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("update `owners` set `password` = '" + Functions.sha256(newPass) +
                            "' where `owner_id` = " + ownerData.get("owner_id"));
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

    private void editProfile() {
        nameField.setEnabled(true);
        phoneField.setEnabled(true);
        emailField.setEnabled(true);
    }

    private void saveProfile() {
        accountPanelMessageLabel.setForeground(Color.red);
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        try {
            Validation.validatePhone(phone);
            Validation.validateEmail(email);
            Validation.validateOwnerName(name, (long) ownerData.get("owner_id"));

        } catch (InputValidationException | SQLException e) {
            accountPanelMessageLabel.setText(e.getMessage());
            return;
        }

        try {
            Connection conn = Database.makeConnection();
            String query = "update owners set name = ?, phone = ?, email = ? where owner_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.setLong(4, (long) ownerData.get("owner_id"));
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            ownerData.put("name", name);
            ownerData.put("phone", phone);
            ownerData.put("email", email);
            initAccountPanel();
            accountPanelMessageLabel.setForeground(Color.green);
            accountPanelMessageLabel.setText("Your profile updated successfully.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSelectedTab() {
        return mainPanel.getSelectedIndex();
    }

    public void setSelectedTab(int tab) {
        mainPanel.setSelectedIndex(tab);
    }

    public void setHousingNameField(String name) {
        housingNameField.setText(name);
    }

    public void setHousingLocationField(String location) {
        housingLocationField.setText(location);
    }

    public void setHousingRentField(String rent) {
        housingRentField.setText(rent);
    }

    public void setWater(boolean yes) {
        waterYes.setSelected(yes);
    }

    public void setElectricity(boolean yes) {
        elecYes.setSelected(yes);
    }

    public void setHousingServices(String services) {
        housingServices.setText(services);
    }

    public void setHousingFloors(String f) {
        floorsNumber.setText(f);
    }

    public void setHousingApart(String apart) {
        apartPerFloor.setText(apart);
    }

    public void setPhotoFile(String filename) {
        chosenFile = new File("src/main/resources/housingPhoto/" + filename);
        chosenFileName.setText(filename);
    }

    public void pressSubmitNewHouseButton() {
        submitAddHouseButton.doClick();
    }

    public String getAddHouseMessageLabel() {
        return addHousingMessageLabel.getText();
    }

    private void editHouseInfo() {
        oneHouseMessageLabel.setForeground(Color.red);
        setEditHouseMode(true);
    }

    private void closeOneHouseMouseClicked() {
        Functions.switchChildPanel(myHousingPanel, allHousesPanel);
    }

    private void saveHouseInfo() {
        oneHouseMessageLabel.setText("");
        oneHouseMessageLabel.setForeground(Color.red);

        String name = houseName.getText();
        String location = houseLocation.getText();
        String rent = houseRent.getText();
        String services = houseServices.getText();
        int electricity = !electricityYes2.isSelected() ? 0 : 1;
        int water = waterYes2.isSelected() ? 1 : 0;
        String floors = floorsNumber2.getText();
        String apart = apartPerFloor2.getText();
        String id = houseId.getText();

        try {
            Validation.checkHouseName(name, Long.parseLong(id));
            Validation.validateEmpty(location);
            Validation.checkHouseRent(rent);
            Validation.validateEmpty(services);
            Validation.checkHouseFloor(floors);
            Validation.checkHouseApart(apart);
        } catch (SQLException | InputValidationException e) {
            oneHouseMessageLabel.setText(e.getMessage());
            return;
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("location", location);
        data.put("rent", rent);
        data.put("services", services);
        data.put("electricity_inclusive", String.valueOf(electricity));
        data.put("water_inclusive", String.valueOf(water));
        data.put("floors", floors);
        data.put("apart_per_floor", apart);
        data.put("housing_id", id);
        try {
            Database.updateHouse(data);
        } catch (SQLException e) {
            oneHouseMessageLabel.setText("sorry, database fault.");
            return;
        }

        setEditHouseMode(false);
        oneHouseMessageLabel.setForeground(Color.green);
        oneHouseMessageLabel.setText("House updated successfully.");
    }

    private void deleteHouse() {
        oneHouseMessageLabel.setForeground(Color.red);
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
                Functions.switchChildPanel(myHousingPanel, allHousesPanel);
                initHousingPanel();
            }
            pstmt.close();
            conn.close();
            if (!error.isEmpty()) {
                oneHouseMessageLabel.setText(error);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHouse() {
        setEditHouseMode(false);
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
        myHousingPanel.removeAll();
        myHousingPanel.add(oneHousePanel);
        houseId.setText(String.valueOf((long)houseData.get("housing_id")));
        houseName.setText((String)houseData.get("name"));
        houseLocation.setText((String)houseData.get("location"));
        houseRent.setText(String.valueOf((int)houseData.get("rent")));
        if ((Integer)houseData.get("water_inclusive") == 1) {
            waterYes2.setSelected(true);
            waterNo2.setSelected(false);
        }
        else {
            waterYes2.setSelected(false);
            waterNo2.setSelected(true);
        }
        if ((Integer)houseData.get("electricity_inclusive") == 1) {
            electricityYes2.setSelected(true);
            electricityNo2.setSelected(false);
        }
        else {
            electricityYes2.setSelected(false);
            electricityNo2.setSelected(true);
        }
        houseServices.setText((String)houseData.get("services"));
        int floors = (int)houseData.get("floors");
        floorsNumber2.setText(String.valueOf(floors));
        apartPerFloor2.setText(String.valueOf(((int)houseData.get("apart_per_floor"))));
        ownerName.setText((String)houseData.get("owner_name"));
        ownerPhone.setText((String)houseData.get("owner_phone"));
        housePicture.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/housingPhoto/" + houseData.get("picture")))));
        myHousingPanel.repaint();
        myHousingPanel.revalidate();
    }

    private void setEditHouseMode(boolean enable) {
        houseName.setEnabled(enable);
        houseLocation.setEnabled(enable);
        houseRent.setEnabled(enable);
        houseServices.setEnabled(enable);
        electricityYes2.setEnabled(enable);
        electricityNo2.setEnabled(enable);
        waterYes2.setEnabled(enable);
        waterNo2.setEnabled(enable);
        floorsNumber2.setEnabled(enable);
        apartPerFloor2.setEnabled(enable);
    }

    private void acceptReservation() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            Connection conn = Database.makeConnection();
            String query = "update `reservations` set `accepted` = '1' where reservation_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            long rid = (long)requestsTable.getValueAt(selectedRow, 1);
            stmt.setLong(1, rid);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void rejectReservation() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) return;
        try {
            Connection conn = Database.makeConnection();
            String query = "delete from `reservations` where reservation_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            long rid = (long)requestsTable.getValueAt(selectedRow, 1);
            stmt.setLong(1, rid);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            initRequestsPanel();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void reservationDetails() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) return;
        Functions.switchChildPanel(requestsPanel, invoicePanel);
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select * from `invoice` where `reservation_id` = "+
                    requestsTable.getValueAt(selectedRow, 1), conn);
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
        Functions.switchChildPanel(requestsPanel, allRequestsPanel);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro Sous
        mainPanel = new JTabbedPane();
        homePanel = new JPanel();
        accountPanel = new JPanel();
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
        changePasswordButton = new JButton();
        separator2 = new JSeparator();
        label10 = new JLabel();
        label11 = new JLabel();
        label12 = new JLabel();
        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        retypeField = new JPasswordField();
        editProfileButton = new JButton();
        saveProfileButton = new JButton();
        accountPanelMessageLabel = new JLabel();
        myHousingPanel = new JPanel();
        allHousesPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        housesTable = new JTable();
        textField7 = new JTextField();
        label29 = new JLabel();
        showHouse = new JButton();
        oneHousePanel = new JPanel();
        label18 = new JLabel();
        label19 = new JLabel();
        label20 = new JLabel();
        label21 = new JLabel();
        houseId = new JTextField();
        houseName = new JTextField();
        houseLocation = new JTextField();
        houseRent = new JTextField();
        housePicture = new JLabel();
        label22 = new JLabel();
        label23 = new JLabel();
        waterYes2 = new JRadioButton();
        waterNo2 = new JRadioButton();
        electricityYes2 = new JRadioButton();
        electricityNo2 = new JRadioButton();
        label24 = new JLabel();
        scrollPane2 = new JScrollPane();
        houseServices = new JTextArea();
        separator4 = new JSeparator();
        label25 = new JLabel();
        label26 = new JLabel();
        floorsNumber2 = new JTextField();
        apartPerFloor2 = new JTextField();
        label27 = new JLabel();
        label28 = new JLabel();
        ownerName = new JTextField();
        ownerPhone = new JTextField();
        separator5 = new JSeparator();
        editHouseInfo = new JButton();
        oneHouseMessageLabel = new JLabel();
        closeOneHouse = new JLabel();
        saveHouseInfo = new JButton();
        deleteHouseButton = new JButton();
        requestsPanel = new JPanel();
        allRequestsPanel = new JPanel();
        scrollPane4 = new JScrollPane();
        requestsTable = new JTable();
        acceptReservationButton = new JButton();
        rejectReservationButton = new JButton();
        reservationDetailsButton = new JButton();
        textField8 = new JTextField();
        label30 = new JLabel();
        invoicePanel = new JPanel();
        label33 = new JLabel();
        label34 = new JLabel();
        separator6 = new JSeparator();
        separator7 = new JSeparator();
        vBookingId = new JTextField();
        label35 = new JLabel();
        vBookingDate = new JTextField();
        label36 = new JLabel();
        separator8 = new JSeparator();
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
        separator9 = new JSeparator();
        label42 = new JLabel();
        label43 = new JLabel();
        separator10 = new JSeparator();
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
        addHousingPanel = new JPanel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();
        label13 = new JLabel();
        label14 = new JLabel();
        housingNameField = new JTextField();
        housingLocationField = new JTextField();
        housingRentField = new JTextField();
        waterYes = new JRadioButton();
        waterNo = new JRadioButton();
        elecYes = new JRadioButton();
        elecNo = new JRadioButton();
        scrollPane1 = new JScrollPane();
        housingServices = new JTextArea();
        label15 = new JLabel();
        separator3 = new JSeparator();
        label16 = new JLabel();
        floorsNumber = new JTextField();
        apartPerFloor = new JTextField();
        label17 = new JLabel();
        submitAddHouseButton = new JButton();
        addHousingMessageLabel = new JLabel();
        browseImageButton = new JButton();
        houseImageLabel = new JLabel();
        chosenFileName = new JLabel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {
            mainPanel.addChangeListener(e -> mainPanelStateChanged());

            //======== homePanel ========
            {
                homePanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing.
                border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border. TitledBorder. CENTER
                , javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font
                .BOLD ,12 ), java. awt. Color. red) ,homePanel. getBorder( )) ); homePanel. addPropertyChangeListener (
                new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r"
                .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
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

                //---- label1 ----
                label1.setText("Your Account");
                label1.setFont(new Font("Bodoni MT", label1.getFont().getStyle(), label1.getFont().getSize() + 15));
                label1.setForeground(Color.blue);
                accountPanel.add(label1);
                label1.setBounds(10, 20, 190, 50);

                //---- label2 ----
                label2.setText("ID:");
                label2.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label2);
                label2.setBounds(50, 115, 80, 30);

                //---- label3 ----
                label3.setText("Name:");
                label3.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label3);
                label3.setBounds(50, 160, 80, 30);

                //---- label4 ----
                label4.setText("Email:");
                label4.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label4);
                label4.setBounds(50, 205, 80, 30);

                //---- label5 ----
                label5.setText("Phone:");
                label5.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label5);
                label5.setBounds(50, 250, 80, 30);
                accountPanel.add(separator1);
                separator1.setBounds(15, 75, 880, 10);

                //---- idField ----
                idField.setFont(new Font("SimSun", Font.PLAIN, 20));
                idField.setEnabled(false);
                idField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(idField);
                idField.setBounds(140, 120, 220, 31);

                //---- nameField ----
                nameField.setFont(new Font("SimSun", Font.PLAIN, 20));
                nameField.setEnabled(false);
                nameField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(nameField);
                nameField.setBounds(140, 165, 220, 30);

                //---- emailField ----
                emailField.setFont(new Font("SimSun", Font.PLAIN, 20));
                emailField.setEnabled(false);
                emailField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(emailField);
                emailField.setBounds(140, 210, 220, 30);

                //---- phoneField ----
                phoneField.setFont(new Font("SimSun", Font.PLAIN, 20));
                phoneField.setEnabled(false);
                phoneField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(phoneField);
                phoneField.setBounds(140, 255, 220, 30);

                //---- changePasswordButton ----
                changePasswordButton.setText("Change your password");
                changePasswordButton.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                changePasswordButton.addActionListener(e -> changePassword());
                accountPanel.add(changePasswordButton);
                changePasswordButton.setBounds(645, 320, 205, 30);

                //---- separator2 ----
                separator2.setOrientation(SwingConstants.VERTICAL);
                accountPanel.add(separator2);
                separator2.setBounds(490, 110, 15, 240);

                //---- label10 ----
                label10.setText("Old Password:");
                label10.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label10);
                label10.setBounds(510, 155, 135, 30);

                //---- label11 ----
                label11.setText("New Password:");
                label11.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label11);
                label11.setBounds(510, 210, 135, 30);

                //---- label12 ----
                label12.setText("Retype:");
                label12.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label12);
                label12.setBounds(510, 260, 135, 30);
                accountPanel.add(oldPasswordField);
                oldPasswordField.setBounds(645, 155, 205, 30);
                accountPanel.add(newPasswordField);
                newPasswordField.setBounds(645, 210, 205, 30);
                accountPanel.add(retypeField);
                retypeField.setBounds(645, 265, 205, 30);

                //---- editProfileButton ----
                editProfileButton.setText("Edit");
                editProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                editProfileButton.addActionListener(e -> editProfile());
                accountPanel.add(editProfileButton);
                editProfileButton.setBounds(100, 335, 78, 30);

                //---- saveProfileButton ----
                saveProfileButton.setText("Save");
                saveProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                saveProfileButton.addActionListener(e -> saveProfile());
                accountPanel.add(saveProfileButton);
                saveProfileButton.setBounds(235, 335, 100, 30);

                //---- accountPanelMessageLabel ----
                accountPanelMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                accountPanelMessageLabel.setForeground(Color.red);
                accountPanelMessageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                accountPanel.add(accountPanelMessageLabel);
                accountPanelMessageLabel.setBounds(415, 395, 480, 20);

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

            //======== myHousingPanel ========
            {
                myHousingPanel.setLayout(new CardLayout());

                //======== allHousesPanel ========
                {
                    allHousesPanel.setEnabled(false);
                    allHousesPanel.setLayout(null);

                    //======== scrollPane3 ========
                    {

                        //---- housesTable ----
                        housesTable.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                        housesTable.setFillsViewportHeight(true);
                        housesTable.setUpdateSelectionOnSort(false);
                        housesTable.setRowMargin(2);
                        housesTable.setFont(new Font("SimSun", Font.PLAIN, 18));
                        housesTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null},
                            },
                            new String[] {
                                "#", "Name", "Location", "Rent"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, String.class, String.class, Integer.class
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
                            TableColumnModel cm = housesTable.getColumnModel();
                            cm.getColumn(0).setResizable(false);
                            cm.getColumn(0).setMinWidth(50);
                            cm.getColumn(0).setMaxWidth(50);
                            cm.getColumn(1).setResizable(false);
                            cm.getColumn(2).setResizable(false);
                            cm.getColumn(3).setResizable(false);
                            cm.getColumn(3).setMinWidth(80);
                            cm.getColumn(3).setMaxWidth(80);
                        }
                        housesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        scrollPane3.setViewportView(housesTable);
                    }
                    allHousesPanel.add(scrollPane3);
                    scrollPane3.setBounds(35, 110, 635, 275);

                    //---- textField7 ----
                    textField7.setToolTipText("search by name");
                    textField7.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    allHousesPanel.add(textField7);
                    textField7.setBounds(50, 35, 280, textField7.getPreferredSize().height);

                    //---- label29 ----
                    label29.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    allHousesPanel.add(label29);
                    label29.setBounds(360, 35, 30, 30);

                    //---- showHouse ----
                    showHouse.setText("House Details");
                    showHouse.setToolTipText("view selected house");
                    showHouse.addActionListener(e -> showHouse());
                    allHousesPanel.add(showHouse);
                    showHouse.setBounds(740, 170, 125, 40);

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
                myHousingPanel.add(allHousesPanel, "card3");

                //======== oneHousePanel ========
                {
                    oneHousePanel.setLayout(null);

                    //---- label18 ----
                    label18.setText("ID:");
                    label18.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label18);
                    label18.setBounds(25, 25, 115, 30);

                    //---- label19 ----
                    label19.setText("Name:");
                    label19.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label19);
                    label19.setBounds(25, 70, 115, 30);

                    //---- label20 ----
                    label20.setText("Location:");
                    label20.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label20);
                    label20.setBounds(25, 115, 115, 30);

                    //---- label21 ----
                    label21.setText("Rent:");
                    label21.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label21);
                    label21.setBounds(25, 160, 115, 30);

                    //---- houseId ----
                    houseId.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseId.setDisabledTextColor(new Color(0x333333));
                    houseId.setEnabled(false);
                    oneHousePanel.add(houseId);
                    houseId.setBounds(145, 25, 200, 30);

                    //---- houseName ----
                    houseName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseName.setEnabled(false);
                    houseName.setDisabledTextColor(new Color(0x333333));
                    houseName.setBackground(new Color(0xededed));
                    oneHousePanel.add(houseName);
                    houseName.setBounds(145, 70, 200, 30);

                    //---- houseLocation ----
                    houseLocation.setFont(new Font("SimSun", Font.PLAIN, 18));
                    houseLocation.setEnabled(false);
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

                    //---- label22 ----
                    label22.setText("-include water:");
                    label22.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label22);
                    label22.setBounds(20, 200, 220, 30);

                    //---- label23 ----
                    label23.setText("-include electricity:");
                    label23.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label23);
                    label23.setBounds(20, 245, 230, 30);

                    //---- waterYes2 ----
                    waterYes2.setText("YES");
                    waterYes2.setEnabled(false);
                    oneHousePanel.add(waterYes2);
                    waterYes2.setBounds(250, 210, 50, waterYes2.getPreferredSize().height);

                    //---- waterNo2 ----
                    waterNo2.setText("NO");
                    waterNo2.setEnabled(false);
                    oneHousePanel.add(waterNo2);
                    waterNo2.setBounds(320, 210, 50, 22);

                    //---- electricityYes2 ----
                    electricityYes2.setText("YES");
                    electricityYes2.setEnabled(false);
                    oneHousePanel.add(electricityYes2);
                    electricityYes2.setBounds(250, 250, 50, 22);

                    //---- electricityNo2 ----
                    electricityNo2.setText("NO");
                    electricityNo2.setEnabled(false);
                    oneHousePanel.add(electricityNo2);
                    electricityNo2.setBounds(320, 250, 50, 22);

                    //---- label24 ----
                    label24.setText("Services:");
                    label24.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label24);
                    label24.setBounds(25, 285, 105, 30);

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

                    //---- separator4 ----
                    separator4.setOrientation(SwingConstants.VERTICAL);
                    separator4.setForeground(new Color(0x999999));
                    oneHousePanel.add(separator4);
                    separator4.setBounds(390, 35, 10, 390);

                    //---- label25 ----
                    label25.setText("# Floors:");
                    label25.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label25);
                    label25.setBounds(410, 65, 110, 30);

                    //---- label26 ----
                    label26.setText("# Apartment/Floor:");
                    label26.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label26);
                    label26.setBounds(410, 110, 200, 35);

                    //---- floorsNumber2 ----
                    floorsNumber2.setFont(new Font("SimSun", Font.PLAIN, 18));
                    floorsNumber2.setBackground(new Color(0xededed));
                    floorsNumber2.setDisabledTextColor(new Color(0x333333));
                    floorsNumber2.setEnabled(false);
                    floorsNumber2.setForeground(new Color(0x666666));
                    oneHousePanel.add(floorsNumber2);
                    floorsNumber2.setBounds(530, 65, 105, 30);

                    //---- apartPerFloor2 ----
                    apartPerFloor2.setFont(new Font("SimSun", Font.PLAIN, 18));
                    apartPerFloor2.setBackground(new Color(0xededed));
                    apartPerFloor2.setDisabledTextColor(new Color(0x333333));
                    apartPerFloor2.setEnabled(false);
                    apartPerFloor2.setForeground(new Color(0x666666));
                    oneHousePanel.add(apartPerFloor2);
                    apartPerFloor2.setBounds(620, 115, 55, 30);

                    //---- label27 ----
                    label27.setText("Owner:");
                    label27.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label27);
                    label27.setBounds(410, 170, 85, 30);

                    //---- label28 ----
                    label28.setText("Phone:");
                    label28.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label28);
                    label28.setBounds(410, 225, 85, 30);

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

                    //---- separator5 ----
                    separator5.setForeground(new Color(0x999999));
                    oneHousePanel.add(separator5);
                    separator5.setBounds(405, 315, 565, 10);

                    //---- editHouseInfo ----
                    editHouseInfo.setText("EDIT");
                    editHouseInfo.addActionListener(e -> editHouseInfo());
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
                    saveHouseInfo.addActionListener(e -> saveHouseInfo());
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
                myHousingPanel.add(oneHousePanel, "card2");
            }
            mainPanel.addTab("MY HOUSING", myHousingPanel);

            //======== requestsPanel ========
            {
                requestsPanel.setLayout(new CardLayout());

                //======== allRequestsPanel ========
                {
                    allRequestsPanel.setLayout(null);

                    //======== scrollPane4 ========
                    {

                        //---- requestsTable ----
                        requestsTable.setModel(new DefaultTableModel(
                            new Object[][] {
                                {null, null, null, null, null, null, null},
                                {null, null, null, null, null, null, null},
                            },
                            new String[] {
                                "#", "Reservation_ID", "House_ID", "Tenant_ID", "Reservation_date", "floor", "apartment"
                            }
                        ) {
                            Class<?>[] columnTypes = new Class<?>[] {
                                Integer.class, Long.class, Long.class, Long.class, String.class, Integer.class, Integer.class
                            };
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false, false, false, false
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
                            cm.getColumn(0).setMinWidth(60);
                            cm.getColumn(0).setMaxWidth(60);
                            cm.getColumn(5).setMinWidth(60);
                            cm.getColumn(5).setMaxWidth(60);
                            cm.getColumn(6).setMinWidth(80);
                            cm.getColumn(6).setMaxWidth(80);
                        }
                        scrollPane4.setViewportView(requestsTable);
                    }
                    allRequestsPanel.add(scrollPane4);
                    scrollPane4.setBounds(20, 115, 810, 295);

                    //---- acceptReservationButton ----
                    acceptReservationButton.setText("Accept");
                    acceptReservationButton.setToolTipText("accept selected request");
                    acceptReservationButton.addActionListener(e -> acceptReservation());
                    allRequestsPanel.add(acceptReservationButton);
                    acceptReservationButton.setBounds(865, 210, 92, 30);

                    //---- rejectReservationButton ----
                    rejectReservationButton.setText("Reject");
                    rejectReservationButton.setToolTipText("reject selected request");
                    rejectReservationButton.addActionListener(e -> rejectReservation());
                    allRequestsPanel.add(rejectReservationButton);
                    rejectReservationButton.setBounds(865, 270, 92, 30);

                    //---- reservationDetailsButton ----
                    reservationDetailsButton.setText("Details");
                    reservationDetailsButton.addActionListener(e -> reservationDetails());
                    allRequestsPanel.add(reservationDetailsButton);
                    reservationDetailsButton.setBounds(865, 150, 92, 30);

                    //---- textField8 ----
                    textField8.setToolTipText("search by name");
                    textField8.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    allRequestsPanel.add(textField8);
                    textField8.setBounds(45, 45, 280, 33);

                    //---- label30 ----
                    label30.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    allRequestsPanel.add(label30);
                    label30.setBounds(355, 45, 30, 30);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < allRequestsPanel.getComponentCount(); i++) {
                            Rectangle bounds = allRequestsPanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = allRequestsPanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        allRequestsPanel.setMinimumSize(preferredSize);
                        allRequestsPanel.setPreferredSize(preferredSize);
                    }
                }
                requestsPanel.add(allRequestsPanel, "card3");

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

                    //---- separator6 ----
                    separator6.setForeground(new Color(0x999999));
                    invoicePanel.add(separator6);
                    separator6.setBounds(205, 65, 265, 15);

                    //---- separator7 ----
                    separator7.setForeground(new Color(0x999999));
                    separator7.setOrientation(SwingConstants.VERTICAL);
                    invoicePanel.add(separator7);
                    separator7.setBounds(15, 65, 10, 380);

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

                    //---- separator8 ----
                    separator8.setForeground(new Color(0x999999));
                    invoicePanel.add(separator8);
                    separator8.setBounds(205, 310, 265, 15);

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

                    //---- separator9 ----
                    separator9.setForeground(new Color(0x999999));
                    separator9.setOrientation(SwingConstants.VERTICAL);
                    invoicePanel.add(separator9);
                    separator9.setBounds(470, 65, 10, 380);

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

                    //---- separator10 ----
                    separator10.setForeground(new Color(0x999999));
                    invoicePanel.add(separator10);
                    separator10.setBounds(665, 65, 265, 15);

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
                requestsPanel.add(invoicePanel, "card4");
            }
            mainPanel.addTab("BOOKING REQUESTS", requestsPanel);

            //======== addHousingPanel ========
            {
                addHousingPanel.setLayout(null);

                //---- label6 ----
                label6.setText("Housing Name: ");
                label6.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label6);
                label6.setBounds(25, 35, 135, 30);

                //---- label7 ----
                label7.setText("Location:");
                label7.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label7);
                label7.setBounds(25, 80, 100, 30);

                //---- label8 ----
                label8.setText("Rent:");
                label8.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label8);
                label8.setBounds(25, 130, 60, 30);

                //---- label9 ----
                label9.setText("Include water:");
                label9.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label9);
                label9.setBounds(40, 170, 185, 30);

                //---- label13 ----
                label13.setText("Include electricity:");
                label13.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label13);
                label13.setBounds(40, 205, 185, 30);

                //---- label14 ----
                label14.setText("Services:");
                label14.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label14);
                label14.setBounds(25, 255, 95, 30);

                //---- housingNameField ----
                housingNameField.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
                addHousingPanel.add(housingNameField);
                housingNameField.setBounds(155, 40, 180, 30);

                //---- housingLocationField ----
                housingLocationField.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
                addHousingPanel.add(housingLocationField);
                housingLocationField.setBounds(130, 85, 240, 30);

                //---- housingRentField ----
                housingRentField.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
                addHousingPanel.add(housingRentField);
                housingRentField.setBounds(90, 130, 85, 30);

                //---- waterYes ----
                waterYes.setText("YES");
                addHousingPanel.add(waterYes);
                waterYes.setBounds(230, 175, 60, waterYes.getPreferredSize().height);

                //---- waterNo ----
                waterNo.setText("NO");
                waterNo.setSelected(true);
                addHousingPanel.add(waterNo);
                waterNo.setBounds(305, 175, 60, 21);

                //---- elecYes ----
                elecYes.setText("YES");
                addHousingPanel.add(elecYes);
                elecYes.setBounds(230, 210, 60, 21);

                //---- elecNo ----
                elecNo.setText("NO");
                elecNo.setSelected(true);
                addHousingPanel.add(elecNo);
                elecNo.setBounds(305, 210, 60, 21);

                //======== scrollPane1 ========
                {

                    //---- housingServices ----
                    housingServices.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                    scrollPane1.setViewportView(housingServices);
                }
                addHousingPanel.add(scrollPane1);
                scrollPane1.setBounds(115, 260, 300, 160);

                //---- label15 ----
                label15.setText("Floors:");
                label15.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label15);
                label15.setBounds(455, 40, 85, 30);

                //---- separator3 ----
                separator3.setOrientation(SwingConstants.VERTICAL);
                addHousingPanel.add(separator3);
                separator3.setBounds(435, 35, 5, 360);

                //---- label16 ----
                label16.setText("Apart/floor:");
                label16.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label16);
                label16.setBounds(455, 90, 125, 30);

                //---- floorsNumber ----
                floorsNumber.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
                addHousingPanel.add(floorsNumber);
                floorsNumber.setBounds(545, 40, 80, 30);

                //---- apartPerFloor ----
                apartPerFloor.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
                addHousingPanel.add(apartPerFloor);
                apartPerFloor.setBounds(580, 90, 80, 30);

                //---- label17 ----
                label17.setText("Photo:");
                label17.setFont(new Font("Consolas", Font.PLAIN, 16));
                addHousingPanel.add(label17);
                label17.setBounds(455, 150, 65, 30);

                //---- submitAddHouseButton ----
                submitAddHouseButton.setText("Submit");
                submitAddHouseButton.addActionListener(e -> submitAddHouse());
                addHousingPanel.add(submitAddHouseButton);
                submitAddHouseButton.setBounds(495, 285, 105, 40);

                //---- addHousingMessageLabel ----
                addHousingMessageLabel.setForeground(Color.red);
                addHousingMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                addHousingMessageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                addHousingPanel.add(addHousingMessageLabel);
                addHousingMessageLabel.setBounds(460, 365, 475, 25);

                //---- browseImageButton ----
                browseImageButton.setText("Browse");
                browseImageButton.addActionListener(e -> browseImage());
                addHousingPanel.add(browseImageButton);
                browseImageButton.setBounds(520, 150, 115, browseImageButton.getPreferredSize().height);
                addHousingPanel.add(houseImageLabel);
                houseImageLabel.setBounds(715, 75, 205, 180);

                //---- chosenFileName ----
                chosenFileName.setText("No chosen file");
                chosenFileName.setEnabled(false);
                addHousingPanel.add(chosenFileName);
                chosenFileName.setBounds(525, 185, 155, chosenFileName.getPreferredSize().height);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < addHousingPanel.getComponentCount(); i++) {
                        Rectangle bounds = addHousingPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = addHousingPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    addHousingPanel.setMinimumSize(preferredSize);
                    addHousingPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("ADD HOUSING", addHousingPanel);
        }
        contentPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 980, 500);

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

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(waterYes2);
        buttonGroup1.add(waterNo2);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(electricityYes2);
        buttonGroup2.add(electricityNo2);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Amro Sous
    private JTabbedPane mainPanel;
    private JPanel homePanel;
    private JPanel accountPanel;
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
    private JButton changePasswordButton;
    private JSeparator separator2;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField retypeField;
    private JButton editProfileButton;
    private JButton saveProfileButton;
    private JLabel accountPanelMessageLabel;
    private JPanel myHousingPanel;
    private JPanel allHousesPanel;
    private JScrollPane scrollPane3;
    private JTable housesTable;
    private JTextField textField7;
    private JLabel label29;
    private JButton showHouse;
    private JPanel oneHousePanel;
    private JLabel label18;
    private JLabel label19;
    private JLabel label20;
    private JLabel label21;
    private JTextField houseId;
    private JTextField houseName;
    private JTextField houseLocation;
    private JTextField houseRent;
    private JLabel housePicture;
    private JLabel label22;
    private JLabel label23;
    private JRadioButton waterYes2;
    private JRadioButton waterNo2;
    private JRadioButton electricityYes2;
    private JRadioButton electricityNo2;
    private JLabel label24;
    private JScrollPane scrollPane2;
    private JTextArea houseServices;
    private JSeparator separator4;
    private JLabel label25;
    private JLabel label26;
    private JTextField floorsNumber2;
    private JTextField apartPerFloor2;
    private JLabel label27;
    private JLabel label28;
    private JTextField ownerName;
    private JTextField ownerPhone;
    private JSeparator separator5;
    private JButton editHouseInfo;
    private JLabel oneHouseMessageLabel;
    private JLabel closeOneHouse;
    private JButton saveHouseInfo;
    private JButton deleteHouseButton;
    private JPanel requestsPanel;
    private JPanel allRequestsPanel;
    private JScrollPane scrollPane4;
    private JTable requestsTable;
    private JButton acceptReservationButton;
    private JButton rejectReservationButton;
    private JButton reservationDetailsButton;
    private JTextField textField8;
    private JLabel label30;
    private JPanel invoicePanel;
    private JLabel label33;
    private JLabel label34;
    private JSeparator separator6;
    private JSeparator separator7;
    private JTextField vBookingId;
    private JLabel label35;
    private JTextField vBookingDate;
    private JLabel label36;
    private JSeparator separator8;
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
    private JSeparator separator9;
    private JLabel label42;
    private JLabel label43;
    private JSeparator separator10;
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
    private JPanel addHousingPanel;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel label13;
    private JLabel label14;
    private JTextField housingNameField;
    private JTextField housingLocationField;
    private JTextField housingRentField;
    private JRadioButton waterYes;
    private JRadioButton waterNo;
    private JRadioButton elecYes;
    private JRadioButton elecNo;
    private JScrollPane scrollPane1;
    private JTextArea housingServices;
    private JLabel label15;
    private JSeparator separator3;
    private JLabel label16;
    private JTextField floorsNumber;
    private JTextField apartPerFloor;
    private JLabel label17;
    private JButton submitAddHouseButton;
    private JLabel addHousingMessageLabel;
    private JButton browseImageButton;
    private JLabel houseImageLabel;
    private JLabel chosenFileName;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

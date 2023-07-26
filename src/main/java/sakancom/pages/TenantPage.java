/*
 * Created by JFormDesigner on Tue Jul 11 19:14:02 IDT 2023
 */

package sakancom.pages;

import java.awt.event.*;
import javax.swing.table.*;
import sakancom.common.Database;
import sakancom.common.Functions;
import sakancom.common.Validation;
import sakancom.exceptions.InputValidationException;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import javax.swing.*;

/**
 *
 */

@SuppressWarnings("FieldCanBeLocal")
public class TenantPage extends JFrame {

    private final HashMap<String, Object> tenantData;
    public final static int HOME = 0, ACCOUNT = 1, HOUSING = 2, FURNITURE = 3, BOOKING = 4;
    public TenantPage(HashMap<String, Object> tenantData) {
        this.tenantData = tenantData;
        initComponents();
        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        housesTable.getTableHeader().setReorderingAllowed(false);
        fillPersonalInfo();
    }

    private void fillFurnitureTable() {
        Connection conn;
        try {
            conn = Database.makeConnection();
            ResultSet rs = Database.getQuery(
                    "SELECT `furniture_id`, `NAME`, `PRICE` FROM `FURNITURE`",
                    conn
            );
            Functions.buildTableModel(rs, furnitureTable);
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHouseInfoPanel(HashMap<String, Object> houseData) {
        housesPanel.removeAll();
        housesPanel.add(oneHousePanel);
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
        chooseFloor.removeAllItems();
        chooseFloor.addItem("");
        for (int i = 1; i <= floors; i++) chooseFloor.addItem(String.valueOf(i));
        chooseApartment.setEnabled(false);
        housesPanel.repaint();
        housesPanel.revalidate();
    }

    private void showHouse() {
        int selected = housesTable.getSelectedRow();
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

    private void chooseFloorItemStateChanged() {
        messageLabel.setText("");
        int selectedIndex = chooseFloor.getSelectedIndex();
        int apart_per_floor = Integer.parseInt(apartPerFloor.getText());
        int house_id = Integer.parseInt(houseId.getText());
        chooseApartment.removeAllItems();
        if (selectedIndex == 0) {
            chooseApartment.setEnabled(false);
            return;
        }
        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select `apart_num` from `reservations` where `floor_num` = "+selectedIndex+" and `housing_id` = "+house_id, conn);
            chooseApartment.addItem("");
            HashSet<Integer> reserved = new HashSet<>();
            while (rs.next()) reserved.add(rs.getInt("apart_num"));
            for (int i = 1; i <= apart_per_floor; i++) {
                if (!reserved.contains(i)) chooseApartment.addItem(""+i);
            }
            chooseApartment.setEnabled(true);
            if (chooseApartment.getItemCount() == 1) {
                messageLabel.setText("No available departments in this floor.");
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeOneHousePanelMouseClicked() {
        Functions.switchChildPanel(housesPanel, allHousesPanel);
    }

    private void book() {
        if (chooseApartment.getSelectedIndex() == 0) return;
        HashMap<String, String> reservation_data = new HashMap<>();
        reservation_data.put("tenant_id", String.valueOf((long)tenantData.get("tenant_id")));
        reservation_data.put("housing_id", houseId.getText());
        reservation_data.put("floor_num", (String)chooseFloor.getSelectedItem());
        reservation_data.put("apart_num", (String)chooseApartment.getSelectedItem());
        long last_id = -1;
        try {
            last_id = Database.addReservation(reservation_data);
        } catch (SQLException ex) {
            messageLabel.setText(ex.getMessage());
        }

        if (last_id == -1) return;

        Functions.switchChildPanel(housesPanel, invoicePanel);

        try {
            Connection conn = Database.makeConnection();
            ResultSet rs = Database.getQuery("select * from `invoice` where `reservation_id` = "+last_id, conn);
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

    public String getHouseId() {
        return houseId.getText();
    }

    public String getHouseName() {
        return houseName.getText();
    }

    public String getHouseLocation() {
        return houseLocation.getText();
    }

    public String getHouseRent() {
        return houseRent.getText();
    }

    public boolean getHouseWaterInclusive() {
        return waterYes.isSelected();
    }

    public boolean getHouseElectricityInclusive() {
        return electricityYes.isSelected();
    }

    public String getHouseServices() {
        return houseServices.getText();
    }

    public String getHouseFloors() {
        return floorsNumber.getText();
    }

    public String getHouseAparts() {
        return apartPerFloor.getText();
    }

    public String getHouseOwnerName() {
        return ownerName.getText();
    }

    public String getHouseOwnerPhone() {
        return ownerPhone.getText();
    }

    public Icon getHousePicture() {
        return housePicture.getIcon();
    }

    public String getInvoiceReservationIdField() {return vBookingId.getText();}

    public String getInvoiceHouseIdField() {return vHousingId.getText();}

    public String getInvoiceTenantIdField() {return vTenantId.getText();}

    public String getInvoiceOwnerIdField() {return vOwnerId.getText();}

    private void furnituresTableMouseClicked() {
        Connection conn;
        try {
            conn = Database.makeConnection();
            String query = "SELECT `furniture`.`furniture_id` as 'furniture_id', `furniture`.`name` as 'furniture_name', `furniture`.`description` as 'description', `tenants`.`name` as 'owner_name', " +
                    "`tenants`.`phone` as 'phone' from `furniture`, `tenants` where `furniture`.`tenant_id` = `tenants`.`tenant_id` and `furniture`.`furniture_id` = ?";
            long id = (long) furnitureTable.getValueAt(furnitureTable.getSelectedRow(), 1);
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

    private void goAddFurniturePanel() {
        Functions.clearAllChildren(addFurniturePanel);
        addFurnitureMessageLabel.setText("");
        Functions.switchChildPanel(furniturePanel, addFurniturePanel);
    }

    private void addFurnitureButton2() {
        Functions.switchChildPanel(furniturePanel, showFurnituresPanel);
        fillFurnitureTable();
    }

    private void addFurniture() {
        addFurnitureMessageLabel.setText("");
        String fName = newFurnitureNameField.getText();
        String fPrice = newFurniturePriceField.getText();
        String fDesc = newFurnitureDescField.getText();
        try {
            Validation.validateName(fName);
            Validation.validateNumeric(fPrice);
            Validation.validateEmpty(fDesc);
        } catch (InputValidationException ex) {
            addFurnitureMessageLabel.setText(ex.getMessage());
            return;
        }
        try {
            Connection conn = Database.makeConnection();
            String query = "insert into `furniture` (`tenant_id`, `name`, `description`, `price`) values " +
                    "(?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, (long)tenantData.get("tenant_id"));
            stmt.setString(2, fName);
            stmt.setString(3, fDesc);
            stmt.setInt(4, Integer.parseInt(fPrice));
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            Functions.switchChildPanel(furniturePanel, showFurnituresPanel);
            fillFurnitureTable();
        } catch (SQLException ex) {
            addFurnitureMessageLabel.setText("Database fault.");
        }
    }

    private void mainPanelStateChanged() {
        int selectedIndex = mainPanel.getSelectedIndex();
        if (selectedIndex == HOUSING) fillHousesTable();
        else if (selectedIndex == FURNITURE) fillFurnitureTable();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro
        mainPanel = new JTabbedPane();
        homePanel = new JPanel();
        accountPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        separator1 = new JSeparator();
        label7 = new JLabel();
        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        ageField = new JTextField();
        majorField = new JTextField();
        editProfileButton = new JButton();
        saveProfileButton = new JButton();
        accountPanelMessageLabel = new JLabel();
        changePassowrdButton = new JButton();
        separator2 = new JSeparator();
        label10 = new JLabel();
        label11 = new JLabel();
        label12 = new JLabel();
        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        retypeField = new JPasswordField();
        housesPanel = new JPanel();
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
        chooseFloor = new JComboBox<>();
        label25 = new JLabel();
        label26 = new JLabel();
        label27 = new JLabel();
        chooseApartment = new JComboBox<>();
        bookButton = new JButton();
        messageLabel = new JLabel();
        closeOneHouse = new JLabel();
        invoicePanel = new JPanel();
        label13 = new JLabel();
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
        scrollPane3 = new JScrollPane();
        furnitureTable = new JTable();
        label29 = new JLabel();
        label30 = new JLabel();
        label31 = new JLabel();
        label32 = new JLabel();
        furnitureName = new JTextField();
        furnitureOwner = new JTextField();
        furniturePhone = new JTextField();
        scrollPane4 = new JScrollPane();
        furnitureDesc = new JTextPane();
        goAddFurnitureButton = new JButton();
        searchFurnitureField = new JTextField();
        searchFurnitureButton = new JLabel();
        label57 = new JLabel();
        furnitureId = new JTextField();
        addFurniturePanel = new JPanel();
        label58 = new JLabel();
        label59 = new JLabel();
        newFurnitureNameField = new JTextField();
        label60 = new JLabel();
        newFurniturePriceField = new JTextField();
        label61 = new JLabel();
        scrollPane5 = new JScrollPane();
        newFurnitureDescField = new JTextArea();
        label62 = new JLabel();
        label63 = new JLabel();
        label64 = new JLabel();
        addFurnitureMessageLabel = new JLabel();
        addFurnitureSubmitButton = new JButton();
        addFurnitureButton2 = new JButton();
        bookingsPanel = new JPanel();
        label65 = new JLabel();
        salesPanel = new JPanel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {
            mainPanel.addChangeListener(e -> mainPanelStateChanged());

            //======== homePanel ========
            {
                homePanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax.
                swing. border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border
                . TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog"
                ,java .awt .Font .BOLD ,12 ), java. awt. Color. red) ,homePanel. getBorder
                ( )) ); homePanel. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java
                .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException
                ( ); }} );
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
                label1.setBounds(25, 20, 190, 50);

                //---- label2 ----
                label2.setText("ID:");
                label2.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label2);
                label2.setBounds(56, 100, 80, 30);

                //---- label3 ----
                label3.setText("Name:");
                label3.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label3);
                label3.setBounds(55, 145, 80, 30);

                //---- label4 ----
                label4.setText("Email:");
                label4.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label4);
                label4.setBounds(55, 190, 80, 30);

                //---- label5 ----
                label5.setText("Phone:");
                label5.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label5);
                label5.setBounds(55, 235, 80, 30);

                //---- label6 ----
                label6.setText("Age:");
                label6.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label6);
                label6.setBounds(55, 280, 80, 30);
                accountPanel.add(separator1);
                separator1.setBounds(30, 75, 925, 10);

                //---- label7 ----
                label7.setText("University Major:");
                label7.setFont(new Font("SimSun", Font.BOLD, 20));
                accountPanel.add(label7);
                label7.setBounds(55, 330, 190, 30);

                //---- idField ----
                idField.setFont(new Font("SimSun", Font.PLAIN, 20));
                idField.setEnabled(false);
                idField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(idField);
                idField.setBounds(145, 105, 220, idField.getPreferredSize().height);

                //---- nameField ----
                nameField.setFont(new Font("SimSun", Font.PLAIN, 20));
                nameField.setEnabled(false);
                nameField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(nameField);
                nameField.setBounds(145, 150, 220, 30);

                //---- emailField ----
                emailField.setFont(new Font("SimSun", Font.PLAIN, 20));
                emailField.setEnabled(false);
                emailField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(emailField);
                emailField.setBounds(145, 195, 220, 30);

                //---- phoneField ----
                phoneField.setFont(new Font("SimSun", Font.PLAIN, 20));
                phoneField.setEnabled(false);
                phoneField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(phoneField);
                phoneField.setBounds(145, 240, 220, 30);

                //---- ageField ----
                ageField.setFont(new Font("SimSun", Font.PLAIN, 20));
                ageField.setEnabled(false);
                ageField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(ageField);
                ageField.setBounds(145, 285, 220, 30);

                //---- majorField ----
                majorField.setFont(new Font("SimSun", Font.PLAIN, 20));
                majorField.setEnabled(false);
                majorField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(majorField);
                majorField.setBounds(245, 330, 220, 30);

                //---- editProfileButton ----
                editProfileButton.setText("Edit");
                editProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                accountPanel.add(editProfileButton);
                editProfileButton.setBounds(new Rectangle(new Point(120, 400), editProfileButton.getPreferredSize()));

                //---- saveProfileButton ----
                saveProfileButton.setText("Save");
                saveProfileButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                accountPanel.add(saveProfileButton);
                saveProfileButton.setBounds(255, 400, 92, 30);

                //---- accountPanelMessageLabel ----
                accountPanelMessageLabel.setForeground(Color.red);
                accountPanelMessageLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
                accountPanel.add(accountPanelMessageLabel);
                accountPanelMessageLabel.setBounds(415, 400, 540, 30);

                //---- changePassowrdButton ----
                changePassowrdButton.setText("Change your password");
                changePassowrdButton.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                accountPanel.add(changePassowrdButton);
                changePassowrdButton.setBounds(660, 320, 205, 30);

                //---- separator2 ----
                separator2.setOrientation(SwingConstants.VERTICAL);
                accountPanel.add(separator2);
                separator2.setBounds(505, 110, 15, 240);

                //---- label10 ----
                label10.setText("Old Password:");
                label10.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label10);
                label10.setBounds(525, 155, 135, 30);

                //---- label11 ----
                label11.setText("New Password:");
                label11.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label11);
                label11.setBounds(525, 210, 135, 30);

                //---- label12 ----
                label12.setText("Retype:");
                label12.setFont(new Font("SimSun", Font.PLAIN, 18));
                accountPanel.add(label12);
                label12.setBounds(525, 260, 135, 30);
                accountPanel.add(oldPasswordField);
                oldPasswordField.setBounds(660, 155, 205, 30);
                accountPanel.add(newPasswordField);
                newPasswordField.setBounds(660, 210, 205, 30);
                accountPanel.add(retypeField);
                retypeField.setBounds(660, 265, 205, 30);

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

            //======== housesPanel ========
            {
                housesPanel.setLayout(new CardLayout());

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
                                {"", null, null, null, null, ""},
                            },
                            new String[] {
                                "#", "Name", "Location", "Rent", "Water Inclusive", "Electricity Inclusive"
                            }
                        ) {
                            boolean[] columnEditable = new boolean[] {
                                false, false, false, false, false, false
                            };
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
                            cm.getColumn(3).setMinWidth(70);
                            cm.getColumn(3).setMaxWidth(70);
                            cm.getColumn(4).setResizable(false);
                            cm.getColumn(4).setMinWidth(145);
                            cm.getColumn(4).setMaxWidth(145);
                            cm.getColumn(5).setResizable(false);
                            cm.getColumn(5).setMinWidth(180);
                            cm.getColumn(5).setMaxWidth(180);
                        }
                        housesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        scrollPane1.setViewportView(housesTable);
                    }
                    allHousesPanel.add(scrollPane1);
                    scrollPane1.setBounds(50, 70, 765, 380);

                    //---- textField7 ----
                    textField7.setToolTipText("search by name");
                    textField7.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    allHousesPanel.add(textField7);
                    textField7.setBounds(50, 30, 305, textField7.getPreferredSize().height);

                    //---- label9 ----
                    label9.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    allHousesPanel.add(label9);
                    label9.setBounds(370, 30, 30, 30);

                    //---- showHouse ----
                    showHouse.setText("VIEW");
                    showHouse.setToolTipText("view selected house");
                    showHouse.addActionListener(e -> showHouse());
                    allHousesPanel.add(showHouse);
                    showHouse.setBounds(840, 155, 125, showHouse.getPreferredSize().height);

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
                housesPanel.add(allHousesPanel, "card1");

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
                    housePicture.setBounds(710, 55, 245, 220);

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
                    label18.setBounds(410, 40, 110, 30);

                    //---- label22 ----
                    label22.setText("# Apartment/Floor:");
                    label22.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label22);
                    label22.setBounds(410, 85, 200, 35);

                    //---- floorsNumber ----
                    floorsNumber.setFont(new Font("SimSun", Font.PLAIN, 18));
                    floorsNumber.setBackground(new Color(0xededed));
                    floorsNumber.setDisabledTextColor(new Color(0x333333));
                    floorsNumber.setEnabled(false);
                    floorsNumber.setForeground(new Color(0x666666));
                    oneHousePanel.add(floorsNumber);
                    floorsNumber.setBounds(530, 40, 105, 30);

                    //---- apartPerFloor ----
                    apartPerFloor.setFont(new Font("SimSun", Font.PLAIN, 18));
                    apartPerFloor.setBackground(new Color(0xededed));
                    apartPerFloor.setDisabledTextColor(new Color(0x333333));
                    apartPerFloor.setEnabled(false);
                    apartPerFloor.setForeground(new Color(0x666666));
                    oneHousePanel.add(apartPerFloor);
                    apartPerFloor.setBounds(620, 90, 55, 30);

                    //---- label23 ----
                    label23.setText("Owner:");
                    label23.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label23);
                    label23.setBounds(410, 145, 85, 30);

                    //---- label24 ----
                    label24.setText("Phone:");
                    label24.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label24);
                    label24.setBounds(410, 200, 85, 30);

                    //---- ownerName ----
                    ownerName.setFont(new Font("SimSun", Font.PLAIN, 18));
                    ownerName.setBackground(new Color(0xededed));
                    ownerName.setDisabledTextColor(new Color(0x333333));
                    ownerName.setEnabled(false);
                    ownerName.setForeground(new Color(0x666666));
                    oneHousePanel.add(ownerName);
                    ownerName.setBounds(505, 145, 165, 30);

                    //---- ownerPhone ----
                    ownerPhone.setFont(new Font("SimSun", Font.PLAIN, 18));
                    ownerPhone.setBackground(new Color(0xededed));
                    ownerPhone.setDisabledTextColor(new Color(0x333333));
                    ownerPhone.setEnabled(false);
                    ownerPhone.setForeground(new Color(0x666666));
                    oneHousePanel.add(ownerPhone);
                    ownerPhone.setBounds(505, 200, 165, 30);

                    //---- separator4 ----
                    separator4.setForeground(new Color(0x999999));
                    oneHousePanel.add(separator4);
                    separator4.setBounds(405, 280, 565, 10);

                    //---- chooseFloor ----
                    chooseFloor.addItemListener(e -> chooseFloorItemStateChanged());
                    oneHousePanel.add(chooseFloor);
                    chooseFloor.setBounds(new Rectangle(new Point(505, 340), chooseFloor.getPreferredSize()));

                    //---- label25 ----
                    label25.setText("Booking Accommodation");
                    label25.setFont(new Font("Sitka Text", Font.PLAIN, 20));
                    oneHousePanel.add(label25);
                    label25.setBounds(415, 290, 265, 30);

                    //---- label26 ----
                    label26.setText("Floor:");
                    label26.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label26);
                    label26.setBounds(420, 340, 75, 30);

                    //---- label27 ----
                    label27.setText("Apartment:");
                    label27.setFont(new Font("SimSun", Font.PLAIN, 20));
                    oneHousePanel.add(label27);
                    label27.setBounds(640, 340, 135, 30);

                    //---- chooseApartment ----
                    chooseApartment.setEnabled(false);
                    oneHousePanel.add(chooseApartment);
                    chooseApartment.setBounds(775, 340, 89, 30);

                    //---- bookButton ----
                    bookButton.setText("Book");
                    bookButton.addActionListener(e -> book());
                    oneHousePanel.add(bookButton);
                    bookButton.setBounds(425, 405, 117, 35);

                    //---- messageLabel ----
                    messageLabel.setForeground(Color.red);
                    messageLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
                    oneHousePanel.add(messageLabel);
                    messageLabel.setBounds(605, 410, 335, 25);

                    //---- closeOneHouse ----
                    closeOneHouse.setIcon(null);
                    closeOneHouse.setText("X");
                    closeOneHouse.setHorizontalAlignment(SwingConstants.CENTER);
                    closeOneHouse.setFont(new Font("Snap ITC", Font.PLAIN, 28));
                    closeOneHouse.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            closeOneHousePanelMouseClicked();
                        }
                    });
                    oneHousePanel.add(closeOneHouse);
                    closeOneHouse.setBounds(910, 10, 40, 35);

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
                housesPanel.add(oneHousePanel, "card2");

                //======== invoicePanel ========
                {
                    invoicePanel.setLayout(null);

                    //---- label13 ----
                    label13.setText("Your booking request was sent to the owner ");
                    label13.setForeground(Color.green);
                    label13.setFont(new Font("Sitka Text", Font.PLAIN, 24));
                    label13.setHorizontalAlignment(SwingConstants.CENTER);
                    invoicePanel.add(label13);
                    label13.setBounds(10, 5, 545, 35);

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
                            closeOneHousePanelMouseClicked();
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
                housesPanel.add(invoicePanel, "card3");
            }
            mainPanel.addTab("HOUSING", housesPanel);

            //======== furniturePanel ========
            {
                furniturePanel.setLayout(new CardLayout());

                //======== showFurnituresPanel ========
                {
                    showFurnituresPanel.setLayout(null);

                    //---- label28 ----
                    label28.setText("Furnitures Shop");
                    label28.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 26));
                    label28.setForeground(Color.blue);
                    showFurnituresPanel.add(label28);
                    label28.setBounds(30, 15, 200, 50);

                    //======== scrollPane3 ========
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
                        furnitureTable.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                furnituresTableMouseClicked();
                            }
                        });
                        scrollPane3.setViewportView(furnitureTable);
                    }
                    showFurnituresPanel.add(scrollPane3);
                    scrollPane3.setBounds(25, 130, 355, 310);

                    //---- label29 ----
                    label29.setText("Name:");
                    label29.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label29);
                    label29.setBounds(440, 65, 95, 35);

                    //---- label30 ----
                    label30.setText("Owner:");
                    label30.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label30);
                    label30.setBounds(440, 120, 95, 35);

                    //---- label31 ----
                    label31.setText("Phone:");
                    label31.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label31);
                    label31.setBounds(440, 170, 95, 35);

                    //---- label32 ----
                    label32.setText("Description:");
                    label32.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
                    showFurnituresPanel.add(label32);
                    label32.setBounds(440, 225, 150, 35);

                    //---- furnitureName ----
                    furnitureName.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureName.setEnabled(false);
                    furnitureName.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureName);
                    furnitureName.setBounds(560, 70, 205, furnitureName.getPreferredSize().height);

                    //---- furnitureOwner ----
                    furnitureOwner.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureOwner.setEnabled(false);
                    furnitureOwner.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureOwner);
                    furnitureOwner.setBounds(560, 125, 205, 33);

                    //---- furniturePhone ----
                    furniturePhone.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furniturePhone.setEnabled(false);
                    furniturePhone.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furniturePhone);
                    furniturePhone.setBounds(560, 170, 205, 33);

                    //======== scrollPane4 ========
                    {

                        //---- furnitureDesc ----
                        furnitureDesc.setEnabled(false);
                        furnitureDesc.setDisabledTextColor(new Color(0x333333));
                        furnitureDesc.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                        scrollPane4.setViewportView(furnitureDesc);
                    }
                    showFurnituresPanel.add(scrollPane4);
                    scrollPane4.setBounds(585, 235, 310, 145);

                    //---- goAddFurnitureButton ----
                    goAddFurnitureButton.setText("ADD Furniture");
                    goAddFurnitureButton.addActionListener(e -> goAddFurniturePanel());
                    showFurnituresPanel.add(goAddFurnitureButton);
                    goAddFurnitureButton.setBounds(435, 405, 155, 35);

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
                    label57.setBounds(440, 15, 95, 35);

                    //---- furnitureId ----
                    furnitureId.setFont(new Font("Segoe UI Light", Font.PLAIN, 18));
                    furnitureId.setEnabled(false);
                    furnitureId.setDisabledTextColor(new Color(0x333333));
                    showFurnituresPanel.add(furnitureId);
                    furnitureId.setBounds(560, 20, 205, 33);

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
                furniturePanel.add(showFurnituresPanel, "card1");

                //======== addFurniturePanel ========
                {
                    addFurniturePanel.setLayout(null);

                    //---- label58 ----
                    label58.setText("Add your furniture for sale");
                    label58.setFont(new Font("Segoe UI Light", Font.PLAIN, 24));
                    label58.setForeground(Color.magenta);
                    addFurniturePanel.add(label58);
                    label58.setBounds(55, 35, 305, 35);

                    //---- label59 ----
                    label59.setText("Name: ");
                    label59.setFont(new Font("SimSun", Font.BOLD, 22));
                    addFurniturePanel.add(label59);
                    label59.setBounds(70, 95, 105, 35);

                    //---- newFurnitureNameField ----
                    newFurnitureNameField.setText("Sofa");
                    newFurnitureNameField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
                    addFurniturePanel.add(newFurnitureNameField);
                    newFurnitureNameField.setBounds(175, 95, 235, 35);

                    //---- label60 ----
                    label60.setText("Price:");
                    label60.setFont(new Font("SimSun", Font.BOLD, 22));
                    addFurniturePanel.add(label60);
                    label60.setBounds(70, 140, 105, 35);

                    //---- newFurniturePriceField ----
                    newFurniturePriceField.setText("155");
                    newFurniturePriceField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
                    addFurniturePanel.add(newFurniturePriceField);
                    newFurniturePriceField.setBounds(175, 140, 235, 35);

                    //---- label61 ----
                    label61.setText("Description:");
                    label61.setFont(new Font("SimSun", Font.BOLD, 22));
                    addFurniturePanel.add(label61);
                    label61.setBounds(70, 190, 165, 35);

                    //======== scrollPane5 ========
                    {

                        //---- newFurnitureDescField ----
                        newFurnitureDescField.setText("Hell climbing , simulated annealing");
                        newFurnitureDescField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
                        newFurnitureDescField.setWrapStyleWord(true);
                        newFurnitureDescField.setLineWrap(true);
                        scrollPane5.setViewportView(newFurnitureDescField);
                    }
                    addFurniturePanel.add(scrollPane5);
                    scrollPane5.setBounds(80, 235, 530, 160);

                    //---- label62 ----
                    label62.setText("( type of the furniture like Sofa, TV,  etc .. )");
                    label62.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 16));
                    label62.setForeground(new Color(0x5e5454));
                    addFurniturePanel.add(label62);
                    label62.setBounds(440, 100, 465, 25);

                    //---- label63 ----
                    label63.setText("( put your price for this furniture )");
                    label63.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 16));
                    label63.setForeground(new Color(0x5e5454));
                    addFurniturePanel.add(label63);
                    label63.setBounds(440, 145, 465, 25);

                    //---- label64 ----
                    label64.setText("( description like quality, benefits, properities for this furniture )");
                    label64.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 16));
                    label64.setForeground(new Color(0x5e5454));
                    addFurniturePanel.add(label64);
                    label64.setBounds(280, 190, 490, 40);

                    //---- addFurnitureMessageLabel ----
                    addFurnitureMessageLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
                    addFurnitureMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    addFurnitureMessageLabel.setForeground(Color.red);
                    addFurniturePanel.add(addFurnitureMessageLabel);
                    addFurnitureMessageLabel.setBounds(150, 410, 640, 40);

                    //---- addFurnitureSubmitButton ----
                    addFurnitureSubmitButton.setText("ADD");
                    addFurnitureSubmitButton.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
                    addFurnitureSubmitButton.addActionListener(e -> addFurniture());
                    addFurniturePanel.add(addFurnitureSubmitButton);
                    addFurnitureSubmitButton.setBounds(665, 340, 130, 40);

                    //---- addFurnitureButton2 ----
                    addFurnitureButton2.setText("CANCEL");
                    addFurnitureButton2.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
                    addFurnitureButton2.addActionListener(e -> addFurnitureButton2());
                    addFurniturePanel.add(addFurnitureButton2);
                    addFurnitureButton2.setBounds(820, 340, 130, 40);

                    {
                        // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for(int i = 0; i < addFurniturePanel.getComponentCount(); i++) {
                            Rectangle bounds = addFurniturePanel.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                        }
                        Insets insets = addFurniturePanel.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        addFurniturePanel.setMinimumSize(preferredSize);
                        addFurniturePanel.setPreferredSize(preferredSize);
                    }
                }
                furniturePanel.add(addFurniturePanel, "card2");
            }
            mainPanel.addTab("FURNITURES", furniturePanel);

            //======== bookingsPanel ========
            {
                bookingsPanel.setLayout(null);

                //---- label65 ----
                label65.setText("YOUR BOOKINGS");
                label65.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 20));
                label65.setForeground(Color.magenta);
                bookingsPanel.add(label65);
                label65.setBounds(55, 25, 185, 35);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < bookingsPanel.getComponentCount(); i++) {
                        Rectangle bounds = bookingsPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = bookingsPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    bookingsPanel.setMinimumSize(preferredSize);
                    bookingsPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("BOOKING", bookingsPanel);

            //======== salesPanel ========
            {
                salesPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < salesPanel.getComponentCount(); i++) {
                        Rectangle bounds = salesPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = salesPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    salesPanel.setMinimumSize(preferredSize);
                    salesPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("SALES", salesPanel);
        }
        contentPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 984, 505);

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

    private void fillPersonalInfo() {
        idField.setText(String.valueOf((long)tenantData.get("tenant_id")));
        nameField.setText((String)tenantData.get("name"));
        emailField.setText((String)tenantData.get("email"));
        phoneField.setText((String)tenantData.get("phone"));
        ageField.setText(String.valueOf((int)tenantData.get("age")));
        majorField.setText((String)tenantData.get("university_major"));
    }

    public void fillHousesTable() {
        Connection conn;
        try {
            conn = Database.makeConnection();
            ResultSet rs = Database.getQuery(
                    "SELECT `NAME`, `LOCATION`, `RENT`, `WATER_INCLUSIVE`, `ELECTRICITY_INCLUSIVE` FROM `HOUSING` WHERE `AVAILABLE` =  '1'",
                    conn
            );
            Functions.buildTableModel(rs, housesTable);
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getSelectedTab() {
        return mainPanel.getSelectedIndex();
    }

    public void setSelectedTab(int index) {
        mainPanel.setSelectedIndex(index);
    }

    public DefaultTableModel getHousingTableModel() {
        return (DefaultTableModel) housesTable.getModel();
    }

    public void selectHouseByIndex(int index) {
        housesTable.setRowSelectionInterval(index, index);
    }

    public void pressShowHouseButton() {
        showHouse.doClick();
    }

    public void pressBookButton() {
        bookButton.doClick();
    }

    public void selectBookRoom(int floor, int apart) {
        chooseFloor.setSelectedIndex(floor);
        chooseApartment.setSelectedItem("" + apart);
    }

    public DefaultTableModel getFurnitureTableModel() {
        return (DefaultTableModel) furnitureTable.getModel();
    }

    public String getFurnitureIdField() {
        return furnitureId.getText();
    }

    public String getFurnitureNameField() {
        return furnitureName.getText();
    }

    public String getFurnitureDescField() {
        return furnitureDesc.getText();
    }

    public String getFurnitureOwnerName() {
        return furnitureOwner.getText();
    }

    public String getFurnitureOwnerPhone() {
        return furniturePhone.getText();
    }

    public void pressAddNewFurnitureSubmitButton() {
        addFurnitureSubmitButton.doClick();
    }

    public void pressAddFurnitureButton() {
        goAddFurnitureButton.doClick();
    }

    public void setNewFurnitureNameField(String str) {
        newFurnitureNameField.setText(str);
    }

    public void setNewFurnitureDescField(String str) {
        newFurnitureDescField.setText(str);
    }

    public void setNewFurniturePriceField(String str) {
        newFurniturePriceField.setText(str);
    }

    public JPanel getAllFurniturePanel() {
        return showFurnituresPanel;
    }

    public String getAddNewFurnitureMessageLabel() {
        return addFurnitureMessageLabel.getText();
    }

    public void selectFurnitureByIndex(Integer ind) {
        furnitureTable.setRowSelectionInterval(ind, ind);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Amro
    private JTabbedPane mainPanel;
    private JPanel homePanel;
    private JPanel accountPanel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JSeparator separator1;
    private JLabel label7;
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField ageField;
    private JTextField majorField;
    private JButton editProfileButton;
    private JButton saveProfileButton;
    private JLabel accountPanelMessageLabel;
    private JButton changePassowrdButton;
    private JSeparator separator2;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField retypeField;
    private JPanel housesPanel;
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
    private JComboBox<String> chooseFloor;
    private JLabel label25;
    private JLabel label26;
    private JLabel label27;
    private JComboBox<String> chooseApartment;
    private JButton bookButton;
    private JLabel messageLabel;
    private JLabel closeOneHouse;
    private JPanel invoicePanel;
    private JLabel label13;
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
    private JScrollPane scrollPane3;
    private JTable furnitureTable;
    private JLabel label29;
    private JLabel label30;
    private JLabel label31;
    private JLabel label32;
    private JTextField furnitureName;
    private JTextField furnitureOwner;
    private JTextField furniturePhone;
    private JScrollPane scrollPane4;
    private JTextPane furnitureDesc;
    private JButton goAddFurnitureButton;
    private JTextField searchFurnitureField;
    private JLabel searchFurnitureButton;
    private JLabel label57;
    private JTextField furnitureId;
    private JPanel addFurniturePanel;
    private JLabel label58;
    private JLabel label59;
    private JTextField newFurnitureNameField;
    private JLabel label60;
    private JTextField newFurniturePriceField;
    private JLabel label61;
    private JScrollPane scrollPane5;
    private JTextArea newFurnitureDescField;
    private JLabel label62;
    private JLabel label63;
    private JLabel label64;
    private JLabel addFurnitureMessageLabel;
    private JButton addFurnitureSubmitButton;
    private JButton addFurnitureButton2;
    private JPanel bookingsPanel;
    private JLabel label65;
    private JPanel salesPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

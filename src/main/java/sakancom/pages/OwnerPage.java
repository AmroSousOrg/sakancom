/*
 * Created by JFormDesigner on Sun Jul 30 23:14:57 EEST 2023
 */

package sakancom.pages;

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
    }

    private void initHousingPanel() {
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
        requestsPanel = new JPanel();
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
                homePanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border
                . EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border. TitledBorder. CENTER, javax
                . swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,
                12 ), java. awt. Color. red) ,homePanel. getBorder( )) ); homePanel. addPropertyChangeListener (new java. beans
                . PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .
                getPropertyName () )) throw new RuntimeException( ); }} );
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
                myHousingPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < myHousingPanel.getComponentCount(); i++) {
                        Rectangle bounds = myHousingPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = myHousingPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    myHousingPanel.setMinimumSize(preferredSize);
                    myHousingPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("MY HOUSING", myHousingPanel);

            //======== requestsPanel ========
            {
                requestsPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < requestsPanel.getComponentCount(); i++) {
                        Rectangle bounds = requestsPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = requestsPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    requestsPanel.setMinimumSize(preferredSize);
                    requestsPanel.setPreferredSize(preferredSize);
                }
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
                submitAddHouseButton.setBounds(495, 285, 95, 35);

                //---- addHousingMessageLabel ----
                addHousingMessageLabel.setForeground(Color.red);
                addHousingMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                addHousingMessageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                addHousingPanel.add(addHousingMessageLabel);
                addHousingMessageLabel.setBounds(460, 365, 445, 25);

                //---- browseImageButton ----
                browseImageButton.setText("Browse");
                browseImageButton.addActionListener(e -> browseImage());
                addHousingPanel.add(browseImageButton);
                browseImageButton.setBounds(520, 150, 115, browseImageButton.getPreferredSize().height);
                addHousingPanel.add(houseImageLabel);
                houseImageLabel.setBounds(695, 75, 205, 180);

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
        mainPanel.setBounds(0, 0, 925, 480);

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
        buttonGroup1.add(waterYes);
        buttonGroup1.add(waterNo);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(elecYes);
        buttonGroup2.add(elecNo);
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
    private JPanel requestsPanel;
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

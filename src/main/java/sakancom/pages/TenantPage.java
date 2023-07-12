/*
 * Created by JFormDesigner on Tue Jul 11 19:14:02 IDT 2023
 */

package sakancom.pages;

import javax.swing.table.*;
import sakancom.common.Database;
import sakancom.common.Functions;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.*;

/**
 *
 */
public class TenantPage extends JFrame {

    private final HashMap<String, Object> tenantData;

    public TenantPage(HashMap<String, Object> tenantData) {
        this.tenantData = tenantData;
        initComponents();
        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        housesTable.getTableHeader().setReorderingAllowed(false);
        fillPersonalInfo();
        fillHousesTable();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro
        mainPanel = new JTabbedPane();
        panel1 = new JPanel();
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
        label8 = new JLabel();
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
        button1 = new JButton();
        oneHousePanel = new JPanel();
        furniturePanel = new JPanel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {

            //======== panel1 ========
            {
                panel1.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border
                . EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder. CENTER, javax
                . swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,
                12 ), java. awt. Color. red) ,panel1. getBorder( )) ); panel1. addPropertyChangeListener (new java. beans
                . PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .equals (e .
                getPropertyName () )) throw new RuntimeException( ); }} );
                panel1.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel1.getComponentCount(); i++) {
                        Rectangle bounds = panel1.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel1.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel1.setMinimumSize(preferredSize);
                    panel1.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("HOME", panel1);

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
                idField.setText("150251");
                idField.setFont(new Font("SimSun", Font.PLAIN, 20));
                idField.setEnabled(false);
                idField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(idField);
                idField.setBounds(145, 105, 220, idField.getPreferredSize().height);

                //---- nameField ----
                nameField.setText("Amro Sous");
                nameField.setFont(new Font("SimSun", Font.PLAIN, 20));
                nameField.setEnabled(false);
                nameField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(nameField);
                nameField.setBounds(145, 150, 220, 30);

                //---- emailField ----
                emailField.setText("amroosous@gmail.com");
                emailField.setFont(new Font("SimSun", Font.PLAIN, 20));
                emailField.setEnabled(false);
                emailField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(emailField);
                emailField.setBounds(145, 195, 220, 30);

                //---- phoneField ----
                phoneField.setText("0592793930");
                phoneField.setFont(new Font("SimSun", Font.PLAIN, 20));
                phoneField.setEnabled(false);
                phoneField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(phoneField);
                phoneField.setBounds(145, 240, 220, 30);

                //---- ageField ----
                ageField.setText("21");
                ageField.setFont(new Font("SimSun", Font.PLAIN, 20));
                ageField.setEnabled(false);
                ageField.setDisabledTextColor(new Color(0x666666));
                accountPanel.add(ageField);
                ageField.setBounds(145, 285, 220, 30);

                //---- majorField ----
                majorField.setText("Computer Engineer");
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

                //---- label8 ----
                label8.setText("Invalid Phone number and password.");
                label8.setForeground(Color.red);
                label8.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
                accountPanel.add(label8);
                label8.setBounds(415, 400, 540, 30);

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
                            cm.getColumn(1).setMinWidth(130);
                            cm.getColumn(1).setMaxWidth(130);
                            cm.getColumn(2).setResizable(false);
                            cm.getColumn(3).setResizable(false);
                            cm.getColumn(3).setMinWidth(70);
                            cm.getColumn(3).setMaxWidth(70);
                            cm.getColumn(4).setResizable(false);
                            cm.getColumn(4).setMinWidth(160);
                            cm.getColumn(4).setMaxWidth(160);
                            cm.getColumn(5).setResizable(false);
                            cm.getColumn(5).setMinWidth(200);
                            cm.getColumn(5).setMaxWidth(200);
                        }
                        scrollPane1.setViewportView(housesTable);
                    }
                    allHousesPanel.add(scrollPane1);
                    scrollPane1.setBounds(50, 70, 820, 380);

                    //---- textField7 ----
                    textField7.setToolTipText("search by name");
                    textField7.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
                    allHousesPanel.add(textField7);
                    textField7.setBounds(50, 30, 305, textField7.getPreferredSize().height);

                    //---- label9 ----
                    label9.setIcon(new ImageIcon(getClass().getResource("/images/searchIcon.png")));
                    allHousesPanel.add(label9);
                    label9.setBounds(370, 30, 30, 30);

                    //---- button1 ----
                    button1.setText("VIEW");
                    allHousesPanel.add(button1);
                    button1.setBounds(new Rectangle(new Point(880, 110), button1.getPreferredSize()));

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
            }
            mainPanel.addTab("HOUSING", housesPanel);

            //======== furniturePanel ========
            {
                furniturePanel.setLayout(null);

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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Amro
    private JTabbedPane mainPanel;
    private JPanel panel1;
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
    private JLabel label8;
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
    private JButton button1;
    private JPanel oneHousePanel;
    private JPanel furniturePanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


}

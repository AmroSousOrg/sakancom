/*
 * Created by JFormDesigner on Tue Jul 11 19:14:02 IDT 2023
 */

package sakancom.pages;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

/**
 * @author amroo
 */
public class TenantPage extends JFrame {

    private final HashMap<String, Object> tenantData;

    public TenantPage(HashMap<String, Object> tenantData) {
        this.tenantData = tenantData;
        initComponents();
        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro
        mainPanel = new JTabbedPane();
        accountPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        separator1 = new JSeparator();
        label7 = new JLabel();
        textField1 = new JTextField();
        textField2 = new JTextField();
        textField3 = new JTextField();
        textField4 = new JTextField();
        textField5 = new JTextField();
        textField6 = new JTextField();
        button1 = new JButton();
        button2 = new JButton();
        label8 = new JLabel();
        button3 = new JButton();
        separator2 = new JSeparator();
        label10 = new JLabel();
        label11 = new JLabel();
        label12 = new JLabel();
        passwordField1 = new JPasswordField();
        passwordField2 = new JPasswordField();
        passwordField3 = new JPasswordField();
        housesPanel = new JPanel();
        furniturePanel = new JPanel();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {

            //======== accountPanel ========
            {
                accountPanel.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new
                javax . swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  "" , javax
                . swing .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java
                . awt .Font ( "Dialo\u0067", java .awt . Font. BOLD ,12 ) ,java . awt
                . Color .red ) ,accountPanel. getBorder () ) ); accountPanel. addPropertyChangeListener( new java. beans .
                PropertyChangeListener ( ){ @Override public void propertyChange (java . beans. PropertyChangeEvent e) { if( "borde\u0072" .
                equals ( e. getPropertyName () ) )throw new RuntimeException( ) ;} } );
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

                //---- textField1 ----
                textField1.setText("150251");
                textField1.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField1);
                textField1.setBounds(145, 105, 220, textField1.getPreferredSize().height);

                //---- textField2 ----
                textField2.setText("Amro Sous");
                textField2.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField2);
                textField2.setBounds(145, 150, 220, 30);

                //---- textField3 ----
                textField3.setText("amroosous@gmail.com");
                textField3.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField3);
                textField3.setBounds(145, 195, 220, 30);

                //---- textField4 ----
                textField4.setText("0592793930");
                textField4.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField4);
                textField4.setBounds(145, 240, 220, 30);

                //---- textField5 ----
                textField5.setText("21");
                textField5.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField5);
                textField5.setBounds(145, 285, 220, 30);

                //---- textField6 ----
                textField6.setText("Computer Engineer");
                textField6.setFont(new Font("SimSun", Font.PLAIN, 20));
                accountPanel.add(textField6);
                textField6.setBounds(245, 330, 220, 30);

                //---- button1 ----
                button1.setText("Edit");
                button1.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                accountPanel.add(button1);
                button1.setBounds(new Rectangle(new Point(120, 400), button1.getPreferredSize()));

                //---- button2 ----
                button2.setText("Save");
                button2.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
                accountPanel.add(button2);
                button2.setBounds(255, 400, 92, 30);

                //---- label8 ----
                label8.setText("Invalid Phone number and password.");
                label8.setForeground(Color.red);
                label8.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
                accountPanel.add(label8);
                label8.setBounds(415, 400, 540, 30);

                //---- button3 ----
                button3.setText("Change your password");
                button3.setFont(new Font("Segoe UI Historic", Font.PLAIN, 16));
                accountPanel.add(button3);
                button3.setBounds(660, 320, 205, 30);

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

                //---- passwordField1 ----
                passwordField1.setText("rtrtdfgfg");
                accountPanel.add(passwordField1);
                passwordField1.setBounds(660, 155, 205, 30);

                //---- passwordField2 ----
                passwordField2.setText("rtrtdfgfg");
                accountPanel.add(passwordField2);
                passwordField2.setBounds(660, 210, 205, 30);

                //---- passwordField3 ----
                passwordField3.setText("rtrtdfgfg");
                accountPanel.add(passwordField3);
                passwordField3.setBounds(660, 265, 205, 30);

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
            mainPanel.addTab("Account", accountPanel);

            //======== housesPanel ========
            {
                housesPanel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < housesPanel.getComponentCount(); i++) {
                        Rectangle bounds = housesPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = housesPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    housesPanel.setMinimumSize(preferredSize);
                    housesPanel.setPreferredSize(preferredSize);
                }
            }
            mainPanel.addTab("Housing", housesPanel);

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
            mainPanel.addTab("Furniture", furniturePanel);
        }
        contentPane.add(mainPanel);
        mainPanel.setBounds(1, 0, 984, 505);

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
    private JPanel accountPanel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JSeparator separator1;
    private JLabel label7;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton button1;
    private JButton button2;
    private JLabel label8;
    private JButton button3;
    private JSeparator separator2;
    private JLabel label10;
    private JLabel label11;
    private JLabel label12;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JPasswordField passwordField3;
    private JPanel housesPanel;
    private JPanel furniturePanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

package sakancom.pages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OwnerPage extends JFrame {

    private final HashMap<String, Object> ownerData;

    private List<String> services;
    private StringBuilder servicesText;
    private JFileChooser fileChooser;
    private String descriptionText;

    public OwnerPage(HashMap<String, Object> ownerData) {

        this.ownerData = ownerData;
        setTitle("Owner Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();

        services = new ArrayList<>();
        servicesText = new StringBuilder();
        fileChooser = new JFileChooser();
    }

    private void button2(ActionEvent e) {
        // TODO add your code here
    }

    private void Clear(ActionEvent e) {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField6.setText("0");
        textField7.setText("0");
        textField8.setText("0");
        descriptionText = "";
        textArea1.setText("");
        checkBox1.setSelected(false);
        checkBox2.setSelected(false);
        checkBox3.setSelected(false);
        services.clear();
        servicesText.setLength(0);
        clearSelection();
    }

    private void clearSelection() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.cancelSelection();
        //System.out.println("File selection cleared");
    }

    private void chooseFile(ActionEvent e) {
        int option = fileChooser.showOpenDialog(OwnerPage.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveFileToDatabase(selectedFile);
        }
    }

    private void saveFileToDatabase(File file) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_name", "hr", "hr")) {
            String sql = "INSERT INTO files (filename, filedata) VALUES (?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, file.getName());

                byte[] fileData = new byte[(int) file.length()];
                InputStream inputStream = new FileInputStream(file);
                inputStream.read(fileData);

                statement.setBinaryStream(2, inputStream, file.length());

                statement.executeUpdate();

                JOptionPane.showMessageDialog(OwnerPage.this, "File uploaded successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(OwnerPage.this, "Error uploading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addService(ActionEvent e) {
        String selectedChoice = (String) comboBox1.getSelectedItem();
        services.add(selectedChoice);
        servicesText.append(selectedChoice).append(", ");
    }

    private void noDescriptionCheckBox(ActionEvent e) {
        boolean selected = checkBox3.isSelected();
        button3.setEnabled(!selected);
    }

    private void addDescription(ActionEvent e) {
        label5.setVisible(false);
        label6.setVisible(false);
        label7.setVisible(false);
        label8.setVisible(false);
        label9.setVisible(false);
        comboBox1.setVisible(false);
        button5.setVisible(false);
        textField6.setVisible(false);
        textField7.setVisible(false);
        checkBox1.setVisible(false);
        textField8.setVisible(false);
        checkBox2.setVisible(false);
        button4.setVisible(false);
        button1.setEnabled(false);
        Clear.setEnabled(false);
        textArea1.setVisible(true);
        textArea1.setBounds(10,230,315,140);
        scrollPane1.setVisible(true);
        scrollPane1.setViewportView(textArea1);
        scrollPane1.setBounds(new Rectangle(new Point(10, 230), scrollPane1.getPreferredSize()));
        scrollPane1.setBounds(10,230,315,140);
        button6.setVisible(true);
        button6.setBounds(195, 195, 10, 20);
    }

    private void saveDescriptionText(ActionEvent e) {
        descriptionText = textArea1.getText();
        label5.setVisible(true);
        label6.setVisible(true);
        label7.setVisible(true);
        label8.setVisible(true);
        label9.setVisible(true);
        comboBox1.setVisible(true);
        button5.setVisible(true);
        textField6.setVisible(true);
        textField7.setVisible(true);
        checkBox1.setVisible(true);
        textField8.setVisible(true);
        checkBox2.setVisible(true);
        button4.setVisible(true);
        button1.setEnabled(true);
        Clear.setEnabled(true);
        textArea1.setVisible(false);
        scrollPane1.setVisible(false);
        button6.setVisible(false);
    }

    private void RequestBtn(ActionEvent e) {
        String str1 = textField1.getText();
        String str2 = textField2.getText();
        String str3 = textField3.getText();
        String str4 = checkBox3.isSelected() ? "No Description!" : descriptionText;
        String str5 = servicesText.toString();
        String str6 = textField6.getText();
        String str7 = checkBox1.isSelected() ? "Rent Inclusive the Electricity Cost!" : textField7.getText();
        String str8 = checkBox2.isSelected() ? "Rent Inclusive the Water Cost!" : textField8.getText();

        String url = "jdbc:mysql://localhost:3306/database_name";
        String username = "hr";
        String password = "hr";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO form_data (name, is_subscribed) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, str1);
                statement.setString(2, str2);
                statement.setString(3, str3);
                statement.setString(4, str4);
                statement.setString(5, str5);
                statement.setString(6, str6);
                statement.setString(7, str7);
                statement.setString(8, str8);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(OwnerPage.this, "Form data saved successfully!");
                } else {
                    JOptionPane.showMessageDialog(OwnerPage.this, "Failed to save form data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(OwnerPage.this, "Error saving form data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Amro
        mainPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label7 = new JLabel();
        label8 = new JLabel();
        label9 = new JLabel();
        textField1 = new JTextField();
        textField2 = new JTextField();
        textField3 = new JTextField();
        textField6 = new JTextField();
        textField7 = new JTextField();
        textField8 = new JTextField();
        label10 = new JLabel();
        button1 = new JButton();
        Clear = new JButton();
        comboBox1 = new JComboBox<>();
        checkBox1 = new JCheckBox();
        checkBox2 = new JCheckBox();
        button3 = new JButton();
        checkBox3 = new JCheckBox();
        button4 = new JButton();
        button5 = new JButton();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        button6 = new JButton();

        //======== this ========
        setMinimumSize(new Dimension(50, 50));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== mainPanel ========
        {
            mainPanel.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new
            javax. swing. border. EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax
            . swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java
            .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,12 ), java. awt
            . Color. red) ,mainPanel. getBorder( )) ); mainPanel. addPropertyChangeListener (new java. beans.
            PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r" .
            equals (e .getPropertyName () )) throw new RuntimeException( ); }} );
            mainPanel.setLayout(null);

            //---- label1 ----
            label1.setText("Owner Name");
            label1.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label1);
            label1.setBounds(10, 70, 100, 30);

            //---- label2 ----
            label2.setText("Phone");
            label2.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label2);
            label2.setBounds(10, 110, 100, 30);

            //---- label3 ----
            label3.setText("Location");
            label3.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label3);
            label3.setBounds(10, 150, 100, 30);

            //---- label4 ----
            label4.setText("Description");
            label4.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label4);
            label4.setBounds(10, 190, 100, 30);

            //---- label5 ----
            label5.setText("Available Services");
            label5.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label5);
            label5.setBounds(10, 230, 100, 30);

            //---- label6 ----
            label6.setText("Monthly Rent");
            label6.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label6);
            label6.setBounds(10, 270, 100, 30);

            //---- label7 ----
            label7.setText("Electricity Cost");
            label7.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label7);
            label7.setBounds(10, 310, 100, 30);

            //---- label8 ----
            label8.setText("Water Cost");
            label8.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label8);
            label8.setBounds(10, 350, 100, 30);

            //---- label9 ----
            label9.setText("Add Photos");
            label9.setHorizontalAlignment(SwingConstants.LEFT);
            mainPanel.add(label9);
            label9.setBounds(10, 390, 100, 30);
            mainPanel.add(textField1);
            textField1.setBounds(130, 70, 197, 30);
            mainPanel.add(textField2);
            textField2.setBounds(130, 110, 197, 30);
            mainPanel.add(textField3);
            textField3.setBounds(130, 150, 197, 30);

            //---- textField6 ----
            textField6.setText("0");
            mainPanel.add(textField6);
            textField6.setBounds(130, 270, 197, 30);

            //---- textField7 ----
            textField7.setText("0");
            mainPanel.add(textField7);
            textField7.setBounds(130, 310, 60, 30);

            //---- textField8 ----
            textField8.setText("0");
            mainPanel.add(textField8);
            textField8.setBounds(130, 350, 60, 30);

            //---- label10 ----
            label10.setText("Housing Owner");
            label10.setHorizontalAlignment(SwingConstants.CENTER);
            label10.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label10.setBorder(new TitledBorder(null, "", TitledBorder.LEFT, TitledBorder.BOTTOM));
            mainPanel.add(label10);
            label10.setBounds(10, 10, 315, 35);

            //---- button1 ----
            button1.setText("REQUEST");
            button1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button1.setForeground(Color.blue);
            button1.addActionListener(e -> RequestBtn(e));
            mainPanel.add(button1);
            button1.setBounds(70, 445, 100, button1.getPreferredSize().height);

            //---- Clear ----
            Clear.setText("CLEAR");
            Clear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            Clear.setForeground(Color.blue);
            Clear.addActionListener(e -> {
			button2(e);
			Clear(e);
		});
            mainPanel.add(Clear);
            Clear.setBounds(175, 445, 100, Clear.getPreferredSize().height);

            //---- comboBox1 ----
            comboBox1.setMaximumRowCount(50);
            comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
                "Default",
                "Service_A",
                "Service_B",
                "Service_C",
                "Service_D",
                "Service_E",
                "Service_F",
                "Service_G",
                "Service_H"
            }));
            mainPanel.add(comboBox1);
            comboBox1.setBounds(130, 230, 100, 30);

            //---- checkBox1 ----
            checkBox1.setText("Rent Inclusive");
            mainPanel.add(checkBox1);
            checkBox1.setBounds(210, 315, 115, 20);

            //---- checkBox2 ----
            checkBox2.setText("Rent Inclusive");
            mainPanel.add(checkBox2);
            checkBox2.setBounds(210, 355, 115, 20);

            //---- button3 ----
            button3.setText("Type..");
            button3.setForeground(Color.blue);
            button3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button3.addActionListener(e -> addDescription(e));
            mainPanel.add(button3);
            button3.setBounds(130, 190, 60, button3.getPreferredSize().height);

            //---- checkBox3 ----
            checkBox3.setText("No Description");
            checkBox3.setForeground(Color.red);
            checkBox3.addActionListener(e -> noDescriptionCheckBox(e));
            mainPanel.add(checkBox3);
            checkBox3.setBounds(210, 195, 115, checkBox3.getPreferredSize().height);

            //---- button4 ----
            button4.setText("Choose File");
            button4.setForeground(Color.blue);
            button4.addActionListener(e -> chooseFile(e));
            mainPanel.add(button4);
            button4.setBounds(130, 390, 197, button4.getPreferredSize().height);

            //---- button5 ----
            button5.setText("Add Service");
            button5.setForeground(Color.blue);
            button5.addActionListener(e -> addService(e));
            mainPanel.add(button5);
            button5.setBounds(230, 230, 95, button5.getPreferredSize().height);

            //======== scrollPane1 ========
            {

                //---- textArea1 ----
                textArea1.setVisible(false);
                scrollPane1.setViewportView(textArea1);
            }
            mainPanel.add(scrollPane1);
            scrollPane1.setBounds(new Rectangle(new Point(190, 195), scrollPane1.getPreferredSize()));

            //---- button6 ----
            button6.setText("Save Description");
            button6.setVisible(false);
            button6.addActionListener(e -> saveDescriptionText(e));
            mainPanel.add(button6);
            button6.setBounds(195, 195, 10, 20);
        }
        contentPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 425, 495);

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
    private JPanel mainPanel;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JLabel label10;
    private JButton button1;
    private JButton Clear;
    private JComboBox<String> comboBox1;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JButton button3;
    private JCheckBox checkBox3;
    private JButton button4;
    private JButton button5;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JButton button6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

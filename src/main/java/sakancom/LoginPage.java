package sakancom;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {

    public JPanel mainPanel;
    public JPanel registerPanel;
    public JPanel loginPanel;
    public JTextField usernameField;
    public JTextField passwordField;
    public JButton submitButton;
    public JButton createAccountButton;
    public JButton clearButton;
    public JComboBox<String> roleCombo;
    public JLabel errorLabel;

    public static final int TENANT = 0, OWNER = 1, ADMIN = 2;

    private void createUIComponents() {
    }

    public LoginPage() {       // Page Constructor

        setContentPane(mainPanel);
        setTitle("Login Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);

        /*


            ActionListener for the clear button to clear
            all the fields in the page.

        * */
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        /*



            ActionListener for the submit button that handle
            the login operation and give the correct message

        * */
        submitButton.addActionListener(e -> {
            // get inputs
            String table = (roleCombo.getSelectedIndex() == TENANT ? "tenants" :
                    roleCombo.getSelectedIndex() == OWNER ? "owners" : "admin");
            String username = usernameField.getText();
            String password = passwordField.getText();
            ResultSet rs;

            try {
                // search in database
                Connection conn = Database.makeConnection();
                rs = Database.getUser(username, password, table, conn);
                if (rs.next()) {
                    // valid user
                    Application.username = username;
                    Application.password = password;
                    Application.status = true;
                    // destroy this page and navigate to tenant page
                    Application.mainPage = new TenantPage();
                    Application.mainPage.setVisible(true);
                    conn.close();
                    LoginPage.this.dispose(); // close this frame
                }
                else {
                    // invalid user, give message
                    errorLabel.setText("Invalid username and/or password.");
                    conn.close();
                }

            } catch (SQLException ex) {
                errorLabel.setText("Error in Database query or connection.");
            }
        });
    }
}

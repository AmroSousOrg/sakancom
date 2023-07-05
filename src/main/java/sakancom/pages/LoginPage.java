package sakancom.pages;

import sakancom.Application;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginPage extends JFrame {

    public JPanel mainPanel;
    public JPanel registerPanel;
    public JPanel loginPanel;
    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton submitButton;
    public JButton createAccountButton;
    public JButton clearButton;
    public JComboBox<String> roleCombo;
    public JLabel errorLabel;
    public JPanel fieldsPanel;
    public JPanel buttonsPanel;
    public JComboBox<String> registerCombo;
    public JButton registerSubmit;
    public JButton registerClear;
    public JPanel tenantPanel;
    public JTextField tenantName;
    public JTextField tenantPassword;
    public JTextField tenantConfirmPass;
    public JTextField tenantAge;
    public JTextField tenantMajor;
    public JTextField tenantPhone;
    public JTextField tenantEmail;
    public JPanel ownerPanel;
    public JLabel ownerErrorLabel;
    public JTextField ownerName;
    public JTextField ownerPassword;
    public JTextField ownerConfirmPass;
    public JTextField ownerPhone;
    public JTextField ownerEmail;
    public JLabel tenantErrorLabel;

    public static final int TENANT = 1, OWNER = 2, ADMIN = 3;
    public int registerChoice;
    public boolean isLoginPanelOpen;

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
            errorLabel.setText("");
        });

        /*



            ActionListener for the submit button that handle
            the login operation and give the correct message

        * */
        submitButton.addActionListener(e -> {
            // get inputs
            errorLabel.setText("");
            int selectedRole = roleCombo.getSelectedIndex();
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            try {

                boolean validUser;

                if (selectedRole == TENANT) {
                    validUser = Application.openTenantPage(username, password);
                }
                else if (selectedRole == OWNER) {
                    validUser = Application.openOwnerPage(username, password);
                }
                else if (selectedRole == ADMIN) {
                    validUser = Application.openAdminPage(username, password);
                }
                else {
                    errorLabel.setText("Invalid Role.");
                    return;
                }

                if (!validUser) {
                    errorLabel.setText("Invalid username and/or password.");
                    return;
                }

                LoginPage.this.dispose(); // destroy login page window

            } catch (SQLException ex) {
                errorLabel.setText("Error in Database query or connection.");
            }
        });

        /*


            AddItemListener to choose registration panel for
            tenants or owners based on comboBox choice.
         */
        registerCombo.addItemListener(e -> {
            int selected = registerCombo.getSelectedIndex();
            fieldsPanel.removeAll();
            if (selected == 1) {
                // tenant panel
                fieldsPanel.add(tenantPanel);
            }
            else {
                // owner panel
                fieldsPanel.add(ownerPanel);
            }
            fieldsPanel.repaint();
            fieldsPanel.revalidate();
        });

        /*

            ActionListener for create new account button
            that navigate to registration panel

        */
        createAccountButton.addActionListener(e -> {

            mainPanel.removeAll();
            mainPanel.add(registerPanel);
            mainPanel.repaint();
            mainPanel.revalidate();
        });
    }
}

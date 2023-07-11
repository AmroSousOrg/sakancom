package sakancom.pages;

import sakancom.Application;
import sakancom.common.Database;
import sakancom.common.Functions;
import sakancom.common.Validation;
import sakancom.exceptions.InputValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;

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
    public JTextField tenantAge;
    public JTextField tenantMajor;
    public JTextField tenantPhone;
    public JTextField tenantEmail;
    public JPanel ownerPanel;
    public JLabel ownerErrorLabel;
    public JTextField ownerName;
    public JTextField ownerPhone;
    public JTextField ownerEmail;
    public JLabel tenantErrorLabel;
    public JLabel backArrow;
    public JPasswordField tenantPassword;
    public JPasswordField tenantConfirmPass;
    public JPasswordField ownerPassword;
    public JPasswordField ownerConfirmPass;
    public JPanel loginFields;

    public static final int TENANT = 1, OWNER = 2, ADMIN = 3;
    public boolean isLoginPanelOpen;

    private void createUIComponents() {
    }

    public LoginPage() {       // Page Constructor

        isLoginPanelOpen = true;
        setContentPane(mainPanel);
        setTitle("Login Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);

        /*

            ActionListener for the sign in clear button
            to clear all the fields in the page.

        * */
        clearButton.addActionListener(e -> defaultLoginPanel());

        /*

            ActionListener for the submit button that handle
            the login operation and give the correct message

        * */
        submitButton.addActionListener(e -> {
            // get inputs
            errorLabel.setText("");
            int selectedRole = roleCombo.getSelectedIndex();
            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword()).trim();

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
            if (selected == 0) {
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
            isLoginPanelOpen = false;
            mainPanel.repaint();
            mainPanel.revalidate();
            defaultRegisterPanel();
        });

        /*
            create mouse click listener for the back arrow
            label to return to sign in panel
        */
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.removeAll();
                mainPanel.add(loginPanel);
                isLoginPanelOpen = true;
                mainPanel.repaint();
                mainPanel.revalidate();
                defaultLoginPanel();
            }
        });

        /*
            action listener for create account button
        */
        registerSubmit.addActionListener(e -> {
            defaultOwnerErrorLabel();
            defaultTenantErrorLabel();
            int roleSelected = registerCombo.getSelectedIndex();

            if (roleSelected == 0) {
                createTenantAccount();
            } else {
                createOwnerAccount();
            }
        });

        // action listener for clear button for registration panel
        registerClear.addActionListener(
                e -> defaultRegisterPanel());
    }

    private void createTenantAccount() {
        String name, password, rePassword, email, phone, age, universityMajor;
        name = tenantName.getText().trim();
        password = String.valueOf(tenantPassword.getPassword()).trim();
        rePassword = String.valueOf(tenantConfirmPass.getPassword()).trim();
        email = tenantEmail.getText().trim();
        phone = tenantPhone.getText().trim();
        age = tenantAge.getText().trim();
        universityMajor = tenantMajor.getText().trim();

        try {
            Validation.validateName(name);
            Validation.validatePassword(password);
            Validation.validateEmail(email);
            Validation.validatePhone(phone);
            Validation.validateAge(age);
            Validation.validateUniversityMajor(universityMajor);
            if (!password.equals(rePassword))
                throw new InputValidationException("Mismatch password.");
            if (Database.isUserExist("tenant", name))
                throw new InputValidationException("Username is already exist.");

            // validation pass
            // create tenant
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("password", password);
            data.put("email", email);
            data.put("phone", phone);
            data.put("age", age);
            data.put("university_major", universityMajor);
            Database.addTenant(data);
            // success message
            defaultRegisterPanel();
            tenantErrorLabel.setForeground(Color.green);
            tenantErrorLabel.setText("Account created successfully.");

        } catch (InputValidationException e) {
            // display error message.
            tenantErrorLabel.setText(e.getMessage());
        } catch (SQLException e) {
            tenantErrorLabel.setText(e.getMessage());
        }
    }

    private void createOwnerAccount() {

        String name, password, rePassword, email, phone;
        name = ownerName.getText().trim();
        password = String.valueOf(ownerPassword.getPassword()).trim();
        rePassword = String.valueOf(ownerConfirmPass.getPassword()).trim();
        email = ownerEmail.getText().trim();
        phone = ownerPhone.getText().trim();

        try {
            Validation.validateName(name);
            Validation.validatePassword(password);
            Validation.validateEmail(email);
            Validation.validatePhone(phone);
            if (!password.equals(rePassword))
                throw new InputValidationException("Mismatch password.");
            if (Database.isUserExist("owner", name))
                throw new InputValidationException("Username is already exist.");

            // validation pass
            // create tenant
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("password", password);
            data.put("email", email);
            data.put("phone", phone);
            Database.addOwner(data);
            // success message
            defaultRegisterPanel();
            ownerErrorLabel.setForeground(Color.green);
            ownerErrorLabel.setText("Account created successfully.");

        } catch (InputValidationException e) {
            // display error message.
            ownerErrorLabel.setText(e.getMessage());
        } catch (SQLException e) {
            ownerErrorLabel.setText(e.getMessage());
        }
    }

    /*
        this method set up the default state of the panels and clear all
        the fields and labels
    */
    public void defaultLoginPanel() {
        Functions.clearAllChildren(loginFields);
        errorLabel.setText("");
    }

    public void defaultRegisterPanel() {
        Functions.clearAllChildren(tenantPanel);
        Functions.clearAllChildren(ownerPanel);
        defaultTenantErrorLabel();
        defaultOwnerErrorLabel();
    }

    private void defaultOwnerErrorLabel() {
        ownerErrorLabel.setText("");
        ownerErrorLabel.setForeground(Color.red);
    }

    private void defaultTenantErrorLabel() {
        tenantErrorLabel.setText("");
        tenantErrorLabel.setForeground(Color.red);
    }
}

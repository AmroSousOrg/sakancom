package sakancom;

import javax.swing.*;

public class LoginPage extends JFrame {

    private JPanel mainPanel;
    private JPanel registerPanel;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton submitButton;
    private JButton createAccountButton;
    private JButton clearButton;
    private JComboBox comboBox1;

    private void createUIComponents() {
    }

    public LoginPage() {

        setContentPane(mainPanel);
        setTitle("Login Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

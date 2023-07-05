package sakancom.pages;

import javax.swing.*;

public class AdminPage extends JFrame {

    public final String username;
    private JPanel mainPanel;

    public AdminPage(String name) {
        this.username = name;
        setContentPane(mainPanel);
        setTitle("Admin Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

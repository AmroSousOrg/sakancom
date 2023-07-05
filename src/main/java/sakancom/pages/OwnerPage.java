package sakancom.pages;

import javax.swing.*;

public class OwnerPage extends JFrame {

    public final String username;
    private JPanel mainPanel;

    public OwnerPage(String name) {
        this.username = name;
        setContentPane(mainPanel);
        setTitle("Owner Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

package sakancom.pages;

import javax.swing.*;

public class TenantPage extends JFrame {

    public JPanel mainPanel;
    public final String username;
    public TenantPage (String name) {
        this.username = name;
        setContentPane(mainPanel);
        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

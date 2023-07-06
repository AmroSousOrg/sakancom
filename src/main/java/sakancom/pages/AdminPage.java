package sakancom.pages;

import javax.swing.*;
import java.util.HashMap;

public class AdminPage extends JFrame {

    // admin info
    public final String username;
    public final long id = 1;
    public final String phone;
    public final String email;

    private JPanel mainPanel;

    public AdminPage(HashMap<String, Object> hm) {

        this.username = (String)hm.get("name");
        this.phone = (String)hm.get("phone");
        this.email = (String)hm.get("email");

        setContentPane(mainPanel);
        setTitle("Admin Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

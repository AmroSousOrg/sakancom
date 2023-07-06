package sakancom.pages;

import javax.swing.*;
import java.util.HashMap;

public class OwnerPage extends JFrame {

    // owner data
    public final String username;
    public final long id;
    public final String phone;
    public final String email;

    private JPanel mainPanel;

    public OwnerPage(HashMap<String, Object> hm) {

        this.username = (String)hm.get("name");
        this.id = (long)hm.get("owner_id");
        this.phone = (String)hm.get("phone");
        this.email = (String)hm.get("email");

        setContentPane(mainPanel);
        setTitle("Owner Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

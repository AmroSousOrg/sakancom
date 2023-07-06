package sakancom.pages;

import javax.swing.*;
import java.util.HashMap;

public class TenantPage extends JFrame {

    // tenant data
    public final String username;
    public final long id;
    public final int age;
    public final String phone;
    public final String email;
    public final String university_major;

    public JPanel mainPanel;
    public TenantPage (HashMap<String, Object> hm) {

        this.username = (String)hm.get("name");
        this.id = (long)hm.get("tenant_id");
        this.age = (int)hm.get("age");
        this.phone = (String)hm.get("phone");
        this.email = (String)hm.get("email");
        this.university_major = (String)hm.get("university_major");

        setContentPane(mainPanel);
        setTitle("Tenant Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 400);
    }
}

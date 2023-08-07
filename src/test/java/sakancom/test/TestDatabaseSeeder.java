package sakancom.test;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import sakancom.common.Database;
import sakancom.common.Functions;

public class TestDatabaseSeeder {

    public static void fillDatabase(String file)
            throws ParserConfigurationException, IOException, SAXException,
            SQLException, NoSuchAlgorithmException, ParseException {

        File inputFile = new File("src/test/resources/sqlData/testData.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        Element rootNode = doc.getDocumentElement();
        NodeList nList = rootNode.getChildNodes();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                if (element.getTagName().equals("tenants")) insertTenant(element);
                else if (element.getTagName().equals("owners")) insertOwner(element);
                else if (element.getTagName().equals("housing")) insertHousing(element);
                else if (element.getTagName().equals("admin")) insertAdmin(element);
                else if (element.getTagName().equals("furniture")) insertFurniture(element);
                else if (element.getTagName().equals("reservations")) insertReservation(element);
            }
        }
    }

    private static void insertTenant(Element element) throws SQLException, NoSuchAlgorithmException {
        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `tenants` (`tenant_id`, `name`, `password`, `email`, `phone`, `age`, `university_major`)" +
                " values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("tenant_id").item(0).getTextContent();
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String password = element.getElementsByTagName("password").item(0).getTextContent();
        String email = element.getElementsByTagName("email").item(0).getTextContent();
        String phone = element.getElementsByTagName("phone").item(0).getTextContent();
        String age = element.getElementsByTagName("age").item(0).getTextContent();
        String major = element.getElementsByTagName("university_major").item(0).getTextContent();

        stmt.setLong(1, Long.parseLong(id));
        stmt.setString(2, name);
        stmt.setString(3, Functions.sha256(password));
        stmt.setString(4, email);
        stmt.setString(5, phone);
        stmt.setInt(6, Integer.parseInt(age));
        stmt.setString(7, major);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private static void insertOwner(Element element) throws SQLException, NoSuchAlgorithmException {

        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `owners` (`owner_id`, `name`, `password`, `email`, `phone`) values (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("owner_id").item(0).getTextContent();
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String password = element.getElementsByTagName("password").item(0).getTextContent();
        String email = element.getElementsByTagName("email").item(0).getTextContent();
        String phone = element.getElementsByTagName("phone").item(0).getTextContent();
        stmt.setLong(1, Long.parseLong(id));
        stmt.setString(2, name);
        stmt.setString(3, Functions.sha256(password));
        stmt.setString(4, email);
        stmt.setString(5, phone);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private static void insertHousing(Element element) throws SQLException {

        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `housing` (`housing_id`, `name`, `location`, `owner_id`, `services`, `rent`, `water_inclusive`," +
                " `electricity_inclusive`, `floors`, `apart_per_floor`, `available`, `picture`) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("housing_id").item(0).getTextContent();
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String location = element.getElementsByTagName("location").item(0).getTextContent();
        String ownerId = element.getElementsByTagName("owner_id").item(0).getTextContent();
        String services = element.getElementsByTagName("services").item(0).getTextContent();
        String rent = element.getElementsByTagName("rent").item(0).getTextContent();
        String water = element.getElementsByTagName("water_inclusive").item(0).getTextContent();
        String electricity = element.getElementsByTagName("electricity_inclusive").item(0).getTextContent();
        String floors = element.getElementsByTagName("floors").item(0).getTextContent();
        String aparts = element.getElementsByTagName("apart_per_floor").item(0).getTextContent();
        String available = element.getElementsByTagName("available").item(0).getTextContent();
        String picture = element.getElementsByTagName("picture").item(0).getTextContent();

        stmt.setLong(1, Long.parseLong(id));
        stmt.setString(2, name);
        stmt.setString(3, location);
        stmt.setLong(4, Long.parseLong(ownerId));
        stmt.setString(5, services);
        stmt.setInt(6, Integer.parseInt(rent));
        stmt.setInt(7, Integer.parseInt(water));
        stmt.setInt(8, Integer.parseInt(electricity));
        stmt.setInt(9, Integer.parseInt(floors));
        stmt.setInt(10, Integer.parseInt(aparts));
        stmt.setInt(11, Integer.parseInt(available));
        stmt.setString(12, picture);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private static void insertAdmin(Element element) throws SQLException, NoSuchAlgorithmException {

        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `admin` (`admin_id`, `name`, `password`, `email`, `phone`) values (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("admin_id").item(0).getTextContent();
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String password = element.getElementsByTagName("password").item(0).getTextContent();
        String email = element.getElementsByTagName("email").item(0).getTextContent();
        String phone = element.getElementsByTagName("phone").item(0).getTextContent();
        stmt.setLong(1, Long.parseLong(id));
        stmt.setString(2, name);
        stmt.setString(3, Functions.sha256(password));
        stmt.setString(4, email);
        stmt.setString(5, phone);

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private static void insertFurniture(Element element) throws SQLException {

        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `furniture` (`furniture_id`, `tenant_id`, `name`, `description`, `price`) values (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("furniture_id").item(0).getTextContent();
        String tenantId = element.getElementsByTagName("tenant_id").item(0).getTextContent();
        String name = element.getElementsByTagName("name").item(0).getTextContent();
        String description = element.getElementsByTagName("description").item(0).getTextContent();
        String price = element.getElementsByTagName("price").item(0).getTextContent();
        stmt.setLong(1, Long.parseLong(id));
        stmt.setLong(2, Long.parseLong(tenantId));
        stmt.setString(3, name);
        stmt.setString(4, description);
        stmt.setInt(5, Integer.parseInt(price));

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    private static void insertReservation(Element element) throws SQLException, ParseException {

        Connection conn = Database.makeConnection();
        String query = "INSERT INTO `reservations` (`reservation_id`, `tenant_id`, `housing_id`, `reservation_date`," +
                " `floor_num`, `apart_num`, `accepted`) values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = element.getElementsByTagName("reservation_id").item(0).getTextContent();
        String tenantId = element.getElementsByTagName("tenant_id").item(0).getTextContent();
        String housingId = element.getElementsByTagName("housing_id").item(0).getTextContent();
        String date = element.getElementsByTagName("reservation_date").item(0).getTextContent();
        String floor = element.getElementsByTagName("floor_num").item(0).getTextContent();
        String apart = element.getElementsByTagName("apart_num").item(0).getTextContent();
        String accepted = element.getElementsByTagName("accepted").item(0).getTextContent();

        stmt.setLong(1, Long.parseLong(id));
        stmt.setLong(2, Long.parseLong(tenantId));
        stmt.setLong(3, Long.parseLong(housingId));
        stmt.setTimestamp(4, new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse(date).getTime()));
        stmt.setInt(5, Integer.parseInt(floor));
        stmt.setInt(6, Integer.parseInt(apart));
        stmt.setInt(7, Integer.parseInt(accepted));

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public static void deleteAllData() throws SQLException {
        Connection conn = Database.makeConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("delete from `reservations` where 1");
        stmt.executeUpdate("delete from `furniture` where 1");
        stmt.executeUpdate("delete from `tenants` where 1");
        stmt.executeUpdate("delete from `housing` where 1");
        stmt.executeUpdate("delete from `owners` where 1");
        stmt.executeUpdate("delete from `admin` where 1");
        stmt.close();
        conn.close();
    }
}
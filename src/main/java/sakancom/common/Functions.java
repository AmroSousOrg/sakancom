package sakancom.common;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Stream;

/*

    class that define and implement common used functions
    such that encryption methods.

*/
public class Functions {

    /*
        method to encrypt a string using sha256 encryption algorithm
        and return encrypted string as Hex
    */
    public static String sha256(String str) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(
                str.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(encodedHash);
    }

    /*
        method to convert hashed string from byte[] to hex string
    */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /*
        method to convert ResultSet of one row  to HashMap<String, String>
        mapping column name with column value as string
    */
    public static HashMap<String, Object> rsToHashMap(ResultSet rs)
            throws SQLException {

        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        HashMap<String, Object> ret = new HashMap<>();
        if (!rs.isAfterLast()) {
            for (int i = 1; i <= columns; i++) {
                ret.put(md.getColumnName(i), rs.getObject(i));
            }
        }
        return ret;
    }

    /*
        method to clear all fields in a given parent component
    */
    public static void clearAllChildren(JComponent parent) {
        Stream.of(parent.getComponents()).filter(c -> c instanceof JTextComponent)
                .forEach(c -> ((JTextComponent) c).setText(""));
    }

    /**
     *  function to fill JTable with passed ResultSet
     */
    public static void buildTableModel(ResultSet rs, JTable table)
            throws SQLException {

        DefaultTableModel dtm = (DefaultTableModel)table.getModel();
        if(rs == null){dtm.setRowCount(0); return;}
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        int num = 1;
        dtm.setRowCount(0);

        while (rs.next()) {
            Vector<Object> vector = new Vector<>();

            vector.add(num);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            num++;
            dtm.addRow(vector);
        }
    }

    /**
     * this method is used to switch between child panels in CardLayout parent panel
     */
    public static void switchChildPanel(JPanel parentPanel, JPanel childPanel) {
        parentPanel.removeAll();
        parentPanel.add(childPanel);
        parentPanel.repaint();
        parentPanel.revalidate();
    }

    /**
     * fill table using result set from query
     */
    public static void fillTable(String query, JTable table) {
        Connection conn;
        try {
            conn = Database.makeConnection();
            ResultSet rs = Database.getQuery(
                    query,
                    conn
            );
            Functions.buildTableModel(rs, table);
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

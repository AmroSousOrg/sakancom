package sakancom.common;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
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
        method to validate String password with the encrypted one
        if they are equal it returns true otherwise return false
    */
    public static boolean validateEncryptionMatch(String password, String encrypted)
            throws NoSuchAlgorithmException {
        return encrypted.equals(sha256(password));
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

    /*
        write to log file named logger.text
    */
    public static void writeLogFile(String message) {
        try {
            FileWriter writer = new FileWriter("target/logger.txt");
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package sakancom.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    public static boolean validatePassword(String password, String encrypted)
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
}

package sakancom.common;

import sakancom.exceptions.InputValidationException;

public class Validation {

    /*
        method to validate name input for user.
    */
    public static void validateName(String name) throws InputValidationException {
        if (name.isEmpty()) throw new InputValidationException("Invalid name.");
    }

    /*
        method to validate password input for user.
    */
    public static void validatePassword(String password)
            throws InputValidationException {
        if (password.isEmpty()) throw new InputValidationException("Invalid password.");
    }

    /*
        method to validate email input for user.
    */
    public static void validateEmail(String email) throws InputValidationException {
        if (!email.matches("^[a-zA-Z]{1,15}[a-zA-Z0-9_.-]{0,15}@[a-zA-Z0-9._-]{1,15}.com$"))
            throw new InputValidationException("Invalid email.");
    }

    /*
        method to validate phone input for user.
    */
    public static void validatePhone(String phone) throws InputValidationException {
        if (!phone.matches("[0-9]{10}"))
            throw new InputValidationException("Invalid phone.");
    }

    /*
        method to validate age input for user.
    */
    public static void validateAge(String age) throws InputValidationException {
        if (!age.matches("[1-9]{1,3}[0-9]{0,3}") ||
            Integer.parseInt(age) > 150)
            throw new InputValidationException("Invalid age.");
    }

    /*
        method to validate university major input for user.
    */
    public static void validateUniversityMajor(String universityMajor)
            throws InputValidationException{
        if (universityMajor.isEmpty())
            throw new InputValidationException("Invalid university major.");
    }

    /**
     * method to validate if string is numeric
     */
    public static void validateNumeric(String str) throws InputValidationException {
        if (!str.matches("^[1-9]+[0-9]*$"))
            throw new InputValidationException("Not numeric field.");
    }

    /**
     * method that check if string is empty or not
     */
    public static void validateEmpty(String str) throws InputValidationException {
        if (str.isEmpty()) throw new InputValidationException("Empty field.");
    }
}

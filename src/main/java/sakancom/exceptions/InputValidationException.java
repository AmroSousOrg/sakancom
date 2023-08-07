package sakancom.exceptions;

/*
    This class is a checked Exception, used to give feed back for
    the calling function that needs to validate inputs from user,
    this exception thrown by the validating function that written
    in Validation class, we can pass the error message in the constructor.
*/
public class InputValidationException extends Exception {
    public InputValidationException(String message) {
        super(message);
    }
}
package eu.jangos.auth.exception;

/**
 * InvalidArgumentException is a custom exception to indicates that the submited
 * argument is invalid.
 *
 * @author Warkdev
 */
public class InvalidArgumentException extends Exception {

    /**
     * detailed error description for developers
     */
    String developerMessage;

    public InvalidArgumentException(String message, String developerMessage) {
        super(message);
        this.developerMessage = developerMessage;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }   
}

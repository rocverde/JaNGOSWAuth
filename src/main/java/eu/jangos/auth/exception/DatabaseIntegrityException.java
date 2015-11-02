package eu.jangos.auth.exception;

/**
 * DatabaseIntegrityException is a custom exception to indicates that a foreign keyis not into the database.
 * @author Warkdev
 */
public class DatabaseIntegrityException extends Exception{

    public DatabaseIntegrityException(String message) {
        super(message);
    }
    
}

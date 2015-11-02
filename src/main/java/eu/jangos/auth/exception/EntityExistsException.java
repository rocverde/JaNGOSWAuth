package eu.jangos.auth.exception;

/**
 * EntityExistsException is a custom exception to indicates that the account already exists into the database.
 * @author Warkdev
 */
public class EntityExistsException extends Exception{

    public EntityExistsException(String message) {
        super(message);
    }
    
}

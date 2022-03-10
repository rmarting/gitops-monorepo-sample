package sa.com.baj.transformers.exceptions;

import java.io.Serializable;

/**
 * Exception thrown when a user transaction operation fails.
 */
public class UserGreetingException extends Exception implements Serializable {

    public UserGreetingException() {
        super();
    }

    public UserGreetingException(String msg) {
        super(msg);
    }

    public UserGreetingException(String msg, Exception e) {
        super(msg, e);
    }

}

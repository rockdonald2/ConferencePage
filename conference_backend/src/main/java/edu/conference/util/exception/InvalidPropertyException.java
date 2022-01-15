package edu.conference.util.exception;

public class InvalidPropertyException extends RuntimeException {

    public InvalidPropertyException() {
        super();
    }

    public InvalidPropertyException(String msg) {
        super(msg);
    }

    public InvalidPropertyException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

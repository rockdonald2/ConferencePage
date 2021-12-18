package repository;

public class RepositoryException extends RuntimeException {

    public RepositoryException(String msg) {
        super(msg);
    }

    public RepositoryException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

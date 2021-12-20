package model;

import java.io.Serializable;
import java.util.Objects;

public enum Role implements Serializable {
    ADMIN,
    REPRESENTATIVE,
    PRESENTER,
    GUEST;

    public static Role get(final String role) throws ModelException {
        Role ret = null;

        if ("admin".equalsIgnoreCase(role)) {
            ret = ADMIN;
        } else if ("representative".equalsIgnoreCase(role)) {
            ret = REPRESENTATIVE;
        } else if ("presenter".equalsIgnoreCase(role)) {
            ret = PRESENTER;
        } else if ("guest".equalsIgnoreCase(role)) {
            ret = GUEST;
        }

        if (Objects.isNull(ret)) {
            throw new ModelException("Invalid role given.");
        }

        return ret;
    }

    @Override
    public String toString() {
        return this.name();
    }

}

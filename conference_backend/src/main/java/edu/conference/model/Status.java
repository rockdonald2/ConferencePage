package edu.conference.model;

import java.io.Serializable;
import java.util.Objects;

public enum Status implements Serializable {
    NEW,
    CONFIRMED,
    APPROVED,
    REJECTED;

    public static Status get(final String status) throws ModelException {
        Status ret = null;

        if ("new".equalsIgnoreCase(status)) {
            ret = NEW;
        } else if ("confirmed".equalsIgnoreCase(status)) {
            ret = CONFIRMED;
        } else if ("approved".equalsIgnoreCase(status)) {
            ret = APPROVED;
        } else if ("rejected".equalsIgnoreCase(status)) {
            ret = REJECTED;
        }

        if (Objects.isNull(ret)) {
            throw new ModelException("Invalid status given.");
        }

        return ret;
    }

    @Override
    public String toString() {
        return this.name();
    }

}

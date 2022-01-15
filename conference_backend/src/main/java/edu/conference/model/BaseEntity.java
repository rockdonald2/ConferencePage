package edu.conference.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {

    private String uuid;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        if (Objects.isNull(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

}

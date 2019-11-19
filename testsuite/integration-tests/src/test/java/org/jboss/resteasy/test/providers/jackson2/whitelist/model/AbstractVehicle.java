package org.jboss.resteasy.test.providers.jackson2.whitelist.model;

import java.io.Serializable;

/**
 * @author bmaxwell
 */
public abstract class AbstractVehicle implements Serializable {

    private String type;

    protected AbstractVehicle(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return String.format("type; %s", this.getType());
    }
}
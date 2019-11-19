package org.jboss.resteasy.test.providers.jackson2.whitelist.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author bmaxwell
 */
public class TestPolymorphicType implements Serializable {

    private String name;

    // Using JsonTypeInfo.Id.CLASS enables polymorphic type handling.
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    public Serializable vehicle;

    public TestPolymorphicType() {
    }

    public TestPolymorphicType(final Serializable vehicle) {
        this.vehicle = vehicle;
    }

    public Serializable getVehicle() {
        return vehicle;
    }

    public void setVehicle(Serializable vehicle) {
        this.vehicle = vehicle;
    }

    public String toString() {
        return String.format("name: %s vehicle: %s", this.name, this.vehicle);
    }
}
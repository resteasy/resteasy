package org.jboss.resteasy.test.providers.jackson2.whitelist.model.air;

import org.jboss.resteasy.test.providers.jackson2.whitelist.model.AbstractVehicle;

/**
 * @author bmaxwell
 */
public class Aircraft extends AbstractVehicle {

    private int landSpeed;
    private int airSpeed;

    public Aircraft() {
        super("Aircraft");
    }

    public int getLandSpeed() {
        return landSpeed;
    }

    public void setLandSpeed(int landSpeed) {
        this.landSpeed = landSpeed;
    }

    public int getAirSpeed() {
        return airSpeed;
    }

    public void setAirSpeed(int airSpeed) {
        this.airSpeed = airSpeed;
    }

    public String toString() {
        return String.format("%s landSpeed: %d airSpeed: %d", super.toString(), landSpeed, airSpeed);
    }
}
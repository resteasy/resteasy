package org.jboss.resteasy.test.providers.jackson2.whitelist.model.land;

import org.jboss.resteasy.test.providers.jackson2.whitelist.model.AbstractVehicle;

/**
 * @author bmaxwell
 */
public class Automobile extends AbstractVehicle {

    private int speed;

    public Automobile() {
        super("Automobile");
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String toString() {
        return String.format("%s speed: %d", super.toString(), speed);
    }
}
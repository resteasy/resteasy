package org.jboss.resteasy.test.resource.constructor.resource;

public class Item {
    Integer i;

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    @Override
    public String toString() {
        return "Item-" + i;
    }
}

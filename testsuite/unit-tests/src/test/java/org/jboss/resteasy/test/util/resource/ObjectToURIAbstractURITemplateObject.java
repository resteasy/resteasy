package org.jboss.resteasy.test.util.resource;

public abstract class ObjectToURIAbstractURITemplateObject {
    private int id;

    public ObjectToURIAbstractURITemplateObject(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

package org.jboss.resteasy.test.providers.jackson2.resource;

public class ProxyWithGenericReturnTypeJacksonType2 extends ProxyWithGenericReturnTypeJacksonAbstractParent {

    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

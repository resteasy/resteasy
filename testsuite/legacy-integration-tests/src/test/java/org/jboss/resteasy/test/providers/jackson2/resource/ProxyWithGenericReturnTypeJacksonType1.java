package org.jboss.resteasy.test.providers.jackson2.resource;

public class ProxyWithGenericReturnTypeJacksonType1 extends ProxyWithGenericReturnTypeJacksonAbstractParent {

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

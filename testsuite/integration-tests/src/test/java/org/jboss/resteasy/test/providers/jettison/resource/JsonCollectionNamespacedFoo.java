package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foo", namespace = "http://foo.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class JsonCollectionNamespacedFoo {
    @XmlAttribute
    private String test;

    public JsonCollectionNamespacedFoo() {
    }

    public JsonCollectionNamespacedFoo(final String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}

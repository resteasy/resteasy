package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foo", namespace = "http://foo.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbCollectionNamespacedFoo {
    @XmlAttribute
    private String test;

    public JaxbCollectionNamespacedFoo() {
    }

    public JaxbCollectionNamespacedFoo(final String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}

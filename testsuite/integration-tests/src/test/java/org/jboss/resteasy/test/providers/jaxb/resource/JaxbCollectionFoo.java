package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbCollectionFoo {
    @XmlAttribute
    private String test;

    public JaxbCollectionFoo() {
    }

    public JaxbCollectionFoo(final String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}

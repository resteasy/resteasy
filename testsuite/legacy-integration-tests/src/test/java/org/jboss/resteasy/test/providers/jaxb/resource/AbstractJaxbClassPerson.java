package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractJaxbClassPerson {
    @XmlAttribute(name = "id", required = true)
    protected String id;

    @XmlElement
    protected String name;

    public AbstractJaxbClassPerson(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public AbstractJaxbClassPerson() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

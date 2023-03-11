package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class JacksonJaxbCoexistenceXmlProduct {
    @XmlAttribute
    protected String name;

    @XmlAttribute
    protected int id;

    public JacksonJaxbCoexistenceXmlProduct() {
    }

    public JacksonJaxbCoexistenceXmlProduct(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

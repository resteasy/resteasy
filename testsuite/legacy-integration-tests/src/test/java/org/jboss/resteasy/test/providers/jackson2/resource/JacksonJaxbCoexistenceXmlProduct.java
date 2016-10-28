package org.jboss.resteasy.test.providers.jackson2.resource;

import org.jboss.resteasy.annotations.providers.NoJackson;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
@NoJackson
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

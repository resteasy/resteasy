package org.jboss.resteasy.test.providers.jackson.resource;

import org.jboss.resteasy.annotations.providers.NoJackson;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
@NoJackson
public class JacksonXmlProduct {
    @XmlAttribute
    protected String name;

    @XmlAttribute
    protected int id;

    public JacksonXmlProduct() {
    }

    public JacksonXmlProduct(final int id, final String name) {
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

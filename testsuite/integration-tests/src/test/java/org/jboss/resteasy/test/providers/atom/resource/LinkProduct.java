package org.jboss.resteasy.test.providers.atom.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.plugins.providers.atom.Link;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LinkProduct {
    protected int id;

    protected String name;

    private ArrayList<Link> linkList = new ArrayList<Link>();

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementRef
    public List<Link> getLinks() {
        return linkList;
    }
}

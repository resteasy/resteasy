package org.jboss.resteasy.test.providers.atom.resource;

import org.jboss.resteasy.plugins.providers.atom.Link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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

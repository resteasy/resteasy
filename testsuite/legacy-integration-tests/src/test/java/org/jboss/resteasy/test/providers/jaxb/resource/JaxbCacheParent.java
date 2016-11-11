package org.jboss.resteasy.test.providers.jaxb.resource;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parentType")
public class JaxbCacheParent {
    private String name;

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    private List<JaxbCacheChild> children = new ArrayList<JaxbCacheChild>();

    public JaxbCacheParent() {

    }

    public JaxbCacheParent(final String name) {
        this.name = name;
    }

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the children.
     *
     * @return the children.
     */
    public List<JaxbCacheChild> getChildren() {
        return children;
    }

    /**
     * Set the children.
     *
     * @param children The children to set.
     */
    public void setChildren(List<JaxbCacheChild> children) {
        this.children = children;
    }

    public void addChild(JaxbCacheChild child) {
        child.setParent(this);
        this.children.add(child);
    }

    public static JaxbCacheParent createTestParent(String name) {
        JaxbCacheParent parent = new JaxbCacheParent(name);
        parent.addChild(new JaxbCacheChild("JaxbCacheChild 1"));
        parent.addChild(new JaxbCacheChild("JaxbCacheChild 2"));
        parent.addChild(new JaxbCacheChild("JaxbCacheChild 3"));
        return parent;
    }
}

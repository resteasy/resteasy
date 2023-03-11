package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parentType")
public class Parent {
    private String name;

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    private List<Child> children = new ArrayList<Child>();

    public Parent() {

    }

    public Parent(final String name) {
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
    public List<Child> getChildren() {
        return children;
    }

    /**
     * Set the children.
     *
     * @param children The children to set.
     */
    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void addChild(Child child) {
        child.setParent(this);
        this.children.add(child);
    }

    public static Parent createTestParent(String name) {
        Parent parent = new Parent(name);
        parent.addChild(new Child("Child 1"));
        parent.addChild(new Child("Child 2"));
        parent.addChild(new Child("Child 3"));
        return parent;
    }
}

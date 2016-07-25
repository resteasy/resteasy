package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A Child.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "childType")
public class Child {
    private String name;

    @XmlTransient
    private Parent parent;

    public Child() {

    }

    public Child(final String name) {
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
     * Get the parent.
     *
     * @return the parent.
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * Set the parent.
     *
     * @param parent The parent to set.
     */
    public void setParent(Parent parent) {
        this.parent = parent;
    }

    /**
     * FIXME Comment this
     *
     * @param unmarshaller
     * @param parent
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object object) {
        this.parent = (Parent) object;
    }
}

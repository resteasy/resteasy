package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "childType")
public class JaxbCacheChild {
    private String name;

    @XmlTransient
    private JaxbCacheParent parent;

    public JaxbCacheChild() {

    }

    public JaxbCacheChild(final String name) {
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
    public JaxbCacheParent getParent() {
        return parent;
    }

    /**
     * Set the parent.
     *
     * @param parent The parent to set.
     */
    public void setParent(JaxbCacheParent parent) {
        this.parent = parent;
    }

    /**
     * Set parent after unmarshalling
     *
     * @param unmarshaller
     * @param JaxbCacheParent
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object object) {
        this.parent = (JaxbCacheParent) object;
    }
}

package org.jboss.resteasy.test.nextgen.wadl.resources.jaxb;

import javax.xml.bind.annotation.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    @XmlAttribute
    protected int id;
    @XmlElement
    protected String fullname;

    public Customer() {
    }

    public Customer(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullname;
    }

    public void setFullName(String name) {
        this.fullname = name;
    }
}

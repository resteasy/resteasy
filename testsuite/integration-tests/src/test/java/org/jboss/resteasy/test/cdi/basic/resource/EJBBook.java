package org.jboss.resteasy.test.cdi.basic.resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Book is
 * 1) a JAXB class, suitable for transport over the network, and
 * 2) an @Entity class, suitable for JPA storage
 *
 * It implements Serializable so that it can be returned from a JAX-RS resource
 * in its role as EJB.
 */
@Entity
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
public class EJBBook implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private int id;

    @XmlElement
    @NotNull
    @Size(min = 1, max = 25)
    private String name;

    public EJBBook() {
    }

    public EJBBook(final String name) {
        this.name = name;
    }

    public EJBBook(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Book[" + id + "," + name + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof EJBBook)) {
            return false;
        }
        return name.equals(((EJBBook) o).name);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

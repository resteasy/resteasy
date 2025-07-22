package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Book is
 * 1) a JAXB class, suitable for transport over the network, and
 * 2) an @Entity class, suitable for JPA storage
 */
@Entity
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
public class CDIInjectionBook {
    @XmlElement
    private int id;

    @XmlElement
    @NotNull
    @Size(min = 1, max = 25)
    private String name;

    public CDIInjectionBook() {
    }

    public CDIInjectionBook(final String name) {
        this.name = name;
    }

    public CDIInjectionBook(final int id, final String name) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        CDIInjectionBook that = (CDIInjectionBook) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

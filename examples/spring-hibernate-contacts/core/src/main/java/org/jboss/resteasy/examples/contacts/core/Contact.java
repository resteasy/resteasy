package org.jboss.resteasy.examples.contacts.core;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
@XmlRootElement(name = "contact")
@Entity
@Table(name = "Contact")
@Path("/")
public class Contact
{
    private Long id;
    private String name;
    private String email;
    private String telephone;
    @XmlTransient
    private Set<Contact> contactChildren;

    public Contact() {

	this.contactChildren = new LinkedHashSet<Contact>();
    }

    @GET
    @Produces("application/xml")
    @Transient
    public Contact get()
    {
	return this;
    }
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlAttribute(name = "contactId")
    public Long getId()
    {
	return id;
    }

    public void setId(Long id)
    {
	this.id = id;
    }

    @Column(name = "name")
    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    @Column(name = "phone")
    public String getTelephone()
    {
	return telephone;
    }

    public void setTelephone(String telephone)
    {
	this.telephone = telephone;
    }

    @Column(name = "email")
    public String getEmail()
    {
	return email;
    }

    public void setEmail(String email)
    {
	this.email = email;
    }

    @ManyToMany(cascade = { CascadeType.ALL },fetch=FetchType.EAGER)
    @JoinTable(name = "ContactToContactJoinTable", joinColumns = @JoinColumn(name = "parentContactId"), inverseJoinColumns = @JoinColumn(name = "childContactId"))
    @XmlTransient
    public Set<Contact> getContactChildren()
    {
	return contactChildren;
    }
    
    public void setContactChildren(Set<Contact> contactChildren)
    {
	this.contactChildren = contactChildren;
    }
    
    @GET
    @Path("/contacts")
    @Produces("application/xml")
    @XmlTransient
    @Transient
    public Contacts getContacts()
    {
	Contacts contacts = new Contacts();
	contacts.setContacts(getContactChildren());
	return contacts;
    }

}

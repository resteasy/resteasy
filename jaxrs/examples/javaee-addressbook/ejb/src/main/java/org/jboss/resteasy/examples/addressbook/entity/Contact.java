/**
 * 
 */
package org.jboss.resteasy.examples.addressbook.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * A Contact.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Entity
@Table(name = "contact")
@NamedQueries(
{
   @NamedQuery(name = "Contact.findAll", 
               query = "from Contact")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact")
@ProduceMime({"application/xml","application/json"})
@ConsumeMime({"application/xml","application/json"})
public class Contact implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = -7238276332730900553L;

   @Id
   @Column(name = "contact_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @XmlAttribute
   private Long id;

   @Column(name = "version", nullable = false)
   @XmlAttribute
   private int version;

   @Column(name = "first_name")
   private String firstName;

   @Column(name = "last_name")
   private String lastName;

   @Column(name = "middle_name")
   private String middleName;

   @Column(name = "salutation")
   private String salutation;

   @Column(name = "title")
   private String title;

   /**
    * 
    */
   @XmlElementWrapper(name = "emailAddresses")
   @XmlElement(name = "emailAddress", required = true, nillable = true)
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
   private Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>();

   /**
    * 
    */
   @XmlElementWrapper(name = "addresses")
   @XmlElement(name = "address", required = true, nillable = true)
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
   private Set<Address> addresses = new HashSet<Address>();

   /**
    * 
    */
   @XmlElementWrapper(name = "phoneNumbers")
   @XmlElement(name = "phoneNumber", required = true, nillable = true)
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
   private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>();

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long contactId)
   {
      this.id = contactId;
   }

   /**
    * Get the firstName.
    * 
    * @return the firstName.
    */
   public String getFirstName()
   {
      return firstName;
   }

   /**
    * Set the firstName.
    * 
    * @param firstName The firstName to set.
    */
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   /**
    * Get the lastName.
    * 
    * @return the lastName.
    */
   public String getLastName()
   {
      return lastName;
   }

   /**
    * Set the lastName.
    * 
    * @param lastName The lastName to set.
    */
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   /**
    * Get the middleName.
    * 
    * @return the middleName.
    */
   public String getMiddleName()
   {
      return middleName;
   }

   /**
    * Set the middleName.
    * 
    * @param middleName The middleName to set.
    */
   public void setMiddleName(String middleName)
   {
      this.middleName = middleName;
   }

   /**
    * Get the salutation.
    * 
    * @return the salutation.
    */
   public String getSalutation()
   {
      return salutation;
   }

   /**
    * Set the salutation.
    * 
    * @param salutation The salutation to set.
    */
   public void setSalutation(String salutation)
   {
      this.salutation = salutation;
   }

   /**
    * Get the title.
    * 
    * @return the title.
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Set the title.
    * 
    * @param title The title to set.
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * Get the emailAddresses.
    * 
    * @return the emailAddresses.
    */
   @GET
   @Path("emailAddresses/")
   public Set<EmailAddress> getEmailAddresses()
   {
      return emailAddresses;
   }
   
   /**
    * FIXME Comment this
    * 
    * @param emailAddress
    */
   @POST
   @Path("emailAddresses/")
   public void addEmailAddress(EmailAddress emailAddress) {
      emailAddress.setContact(this);
      this.getEmailAddresses().add(emailAddress);
   }
   
   /**
    * FIXME Comment this
    * 
    * @param id
    * @return
    */
   @GET
   @Path("emailAddresses/{emailAddressId}")
   public EmailAddress getEmailAddressById(@PathParam("emailAddressId") Long id) {
      for(EmailAddress emailAddress : this.emailAddresses) {
         if(id.equals(emailAddress.getId())) {
            return emailAddress;
         }
      }
      return null;
   }
   /**
    * Set the emailAddresses.
    * 
    * @param emailAddresses The emailAddresses to set.
    */
   public void setEmailAddresses(Set<EmailAddress> emailAddresses)
   {
      this.emailAddresses = emailAddresses;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(int version)
   {
      this.version = version;
   }

   public Set<Address> getAddresses()
   {
      return this.addresses;
   }

   public void setAddresses(Set<Address> addressIdCollection)
   {
      this.addresses = addressIdCollection;
   }

   public void addAddress(Address address)
   {
      address.setContact(this);
      addresses.add(address);
   }

   public Set<PhoneNumber> getPhoneNumbers()
   {
      return this.phoneNumbers;
   }

   public void setPhoneNumbers(Set<PhoneNumber> phoneNumberCollection)
   {
      this.phoneNumbers = phoneNumberCollection;
   }

   public void addPhoneNumber(PhoneNumber phoneNumber)
   {
      phoneNumber.setContact(this);
      phoneNumbers.add(phoneNumber);
   }

   public String toString()
   {
      StringBuilder b = new StringBuilder();
      b.append("ID: ").append(this.getId()).append(" ");
      //b.append("Full Name: ").append(this.getFullName()).append(" ");
      return b.toString();
   }
}

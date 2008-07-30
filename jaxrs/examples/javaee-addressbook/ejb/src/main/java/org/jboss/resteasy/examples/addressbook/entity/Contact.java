/**
 * 
 */
package org.jboss.resteasy.examples.addressbook.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.PolymorphismType;

/**
 * 
 * A Contact.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Entity
@org.hibernate.annotations.Entity(polymorphism = PolymorphismType.EXPLICIT)
@Table(name = "contact")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact", propOrder =
{"photoData", "emailAddresses", "addresses", "phoneNumbers"})
public class Contact extends ContactInfo
{

   /**
    * 
    */
   private static final long serialVersionUID = -7238276332730900553L;

   @Transient
   private byte[] photoData;

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
   @XmlElementWrapper(name = "addresses", nillable = false)
   @XmlElement(name = "address", required = true, nillable = true)
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
   private Set<Address> addresses = new HashSet<Address>();

   /**
    * 
    */
   @XmlElementWrapper(name = "phoneNumbers", nillable = false)
   @XmlElement(name = "phoneNumber", required = true, nillable = true)
   @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact")
   private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>();

   /**
    * Get the emailAddresses.
    * 
    * @return the emailAddresses.
    */
   //   @GET
   //   @Path("/emailAddresses")
   //   @ProduceMime({"application/xml","application/json"})
   public Set<EmailAddress> getEmailAddresses()
   {
      return emailAddresses;
   }

   /**
    * FIXME Comment this
    * 
    * @param emailAddress
    */
   //   @POST
   //   @Path("/emailAddresses")
   public void addEmailAddress(EmailAddress emailAddress)
   {
      emailAddress.setContact(this);
      this.getEmailAddresses().add(emailAddress);
   }

   //   @GET
   //   @Path("/emailAddresses/{emailAddressId}")
   public EmailAddress getEmailAddressById(Long id)
   {
      for (EmailAddress emailAddress : this.emailAddresses)
      {
         if (id.equals(emailAddress.getId()))
         {
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

   /**
    * Get the photoData.
    * 
    * @return the photoData.
    */
   public byte[] getPhotoData()
   {
      return photoData;
   }

   /**
    * Set the photoData.
    * 
    * @param photoData The photoData to set.
    */
   public void setPhotoData(byte[] photoData)
   {
      this.photoData = photoData;
   }

}

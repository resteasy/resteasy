/**
 * 
 */
package org.jboss.resteasy.examples.addressbook.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.PolymorphismType;

/**
 * 
 * A light-weight version of contact that contains only the basic
 * information for a contact. This class is intended for use in 
 * search results.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Entity
@Table(name = "contact")
// for some reason, you need this annotation here otherwise
// Hibernate will generate try and map a discriminator column.
@MappedSuperclass 
@org.hibernate.annotations.Entity(polymorphism = PolymorphismType.EXPLICIT)
@NamedQueries( 
         {@NamedQuery(name = "ContactInfo.findAll", 
                      query = "from ContactInfo")})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactInfo", propOrder =
{"firstName", "lastName", "middleName", "salutation", "title"})
public class ContactInfo implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = -7238276332730900553L;

   @Id
   @Column(name = "contact_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @XmlAttribute(required = false)
   private Long id;

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
   
   @Version
   @Column(name = "version")
   @XmlAttribute
   private int version;

   /**
    * Get the version.
    * 
    * @return the version.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Set the version.
    * 
    * @param version The version to set.
    */
   public void setVersion(int version)
   {
      this.version = version;
   }

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

   public String toString()
   {
      StringBuilder b = new StringBuilder(getFirstName());
      b.append(" ").append(getLastName());
      return b.toString();
   }

}

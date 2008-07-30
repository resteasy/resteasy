/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Abstract base class to manage elements associated with
 * a {@link Contact} instance.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "abstractContactItem")
@XmlSeeAlso(
{EmailAddress.class, Address.class, PhoneNumber.class})
@MappedSuperclass
public abstract class AbstractContactItem extends Listener
{

   /**
    * 
    */
   @ManyToOne
   @JoinColumn(name = "contact_id", nullable = false)
   @XmlTransient
   private Contact contact;
   
   /**
    * 
    */
   @Enumerated(EnumType.STRING)
   @Column(name = "label", nullable = false)
   @XmlAttribute
   private Label label;
   
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

   public Contact getContact()
   {
      return contact;
   }

   public void setContact(Contact contact)
   {
      this.contact = contact;
   }

   
   /**
    * Get the label.
    * 
    * @return the label.
    */
   public Label getLabel()
   {
      return label;
   }

   /**
    * Set the label.
    * 
    * @param label The label to set.
    */
   public void setLabel(Label label)
   {
      this.label = label;
   }
   /**
    * JAXB Callback method used to reassociate the item with the owning contact.
    * 
    * @param unmarshaller the JAXB {@link Unmarshaller}.
    * @param parent the owning {@link Contact} instance.
    */
   public void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
   {
      setContact((Contact) parent);
   }
}

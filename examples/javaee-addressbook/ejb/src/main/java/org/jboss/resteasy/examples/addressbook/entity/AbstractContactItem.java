/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Abstract base class to manage elements associated with
 * a {@link Contact} instance.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlSeeAlso(
{EmailAddress.class, Address.class, PhoneNumber.class})
@MappedSuperclass
public abstract class AbstractContactItem
{

   /**
    * 
    */
   @ManyToOne
   @JoinColumn(name = "contact_id")
   @XmlTransient
   private Contact contact;

   @XmlTransient
   public Contact getContact()
   {
      return contact;
   }

   public void setContact(Contact contact)
   {
      this.contact = contact;
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

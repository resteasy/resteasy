/**
 * 
 */
package org.jboss.resteasy.examples.addressbook.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * A Contacts.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(name = "contactsType")
public class Contacts implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 4755743806205758274L;

   @XmlElement(name = "contact", required = true, nillable = true)
   private List<Contact> contacts = new ArrayList<Contact>();

   public Contacts()
   {
   }

   public Contacts(List<Contact> contacts)
   {
      this.setContacts(contacts);
   }

   public List<Contact> getContacts()
   {
      return contacts;
   }

   public void setContacts(List<Contact> contacts)
   {
      this.contacts = contacts;
   }

}

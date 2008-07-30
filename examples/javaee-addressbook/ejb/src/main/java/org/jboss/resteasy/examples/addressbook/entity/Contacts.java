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
@XmlRootElement(name = "contacts")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contacts")
public class Contacts implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 4755743806205758274L;

   @XmlElement(name = "contactInfo", required = true, nillable = true)
   private List<ContactInfo> contacts = new ArrayList<ContactInfo>();

   public Contacts()
   {
   }

   public Contacts(List<ContactInfo> contacts)
   {
      this.setContacts(contacts);
   }

   public List<ContactInfo> getContacts()
   {
      return contacts;
   }

   public void setContacts(List<ContactInfo> contacts)
   {
      this.contacts = contacts;
   }

}

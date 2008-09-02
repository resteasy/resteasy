/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.client;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.examples.addressbook.client.entity.Contact;
import org.jboss.resteasy.examples.addressbook.client.entity.Contacts;
import org.jboss.resteasy.examples.addressbook.client.entity.EmailAddresses;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * A AddressBook.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class AddressBook
{

   /**
    * 
    */
   private static final AddressBook instance = new AddressBook();

   /**
    * 
    */
   private ContactService contactService;

   /**
    * 
    * Create a new AddressBook.
    *
    */
   private AddressBook()
   {
      ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      contactService = ProxyFactory.create(ContactService.class, "http://localhost:8080/addressbook");
   }
   
   /**
    * FIXME Comment this
    * 
    * @return
    */
   public static AddressBook instance() 
   {
      return instance;
   }

  

   /**
    * @param id
    * @param contact
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#deleteContact(java.lang.Long, org.jboss.resteasy.examples.addressbook.client.entity.Contact)
    */
   public void deleteContact(Long id)
   {
      contactService.deleteContact(id);
   }

   /**
    * @param contact
    * @return
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#createContact(org.jboss.resteasy.examples.addressbook.client.entity.Contact)
    */
   public ClientResponse<Response> createContact(Contact contact)
   {
      return contactService.createContact(contact);
   }

   /**
    * @param id
    * @return
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#findContactById(java.lang.Long)
    */
   public ClientResponse<Contact> findContactById(Long id)
   {
      return contactService.findContactById(id);
   }

   /**
    * @return
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#findContacts()
    */
   public ClientResponse<Contacts> findContacts()
   {
      return contactService.findContacts();
   }

   /**
    * @param contactId
    * @return
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#getEmailAddresses(java.lang.Long)
    */
   public ClientResponse<EmailAddresses> getEmailAddresses(Long contactId)
   {
      return contactService.getEmailAddresses(contactId);
   }

   /**
    * @param id
    * @param contact
    * @return
    * @see org.jboss.resteasy.examples.addressbook.client.ContactService#updateContact(java.lang.Long, org.jboss.resteasy.examples.addressbook.client.entity.Contact)
    */
   public ClientResponse<Contact> updateContact(Long id, Contact contact)
   {
      return contactService.updateContact(id, contact);
   }

   
}

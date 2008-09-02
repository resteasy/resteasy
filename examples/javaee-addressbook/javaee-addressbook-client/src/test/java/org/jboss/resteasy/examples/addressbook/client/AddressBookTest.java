/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.client;

import org.jboss.resteasy.examples.addressbook.client.entity.Contact;
import org.jboss.resteasy.examples.addressbook.client.entity.Contacts;
import org.jboss.resteasy.examples.addressbook.client.entity.EmailAddress;
import org.jboss.resteasy.examples.addressbook.client.entity.EmailAddresses;
import org.jboss.resteasy.examples.addressbook.client.entity.Label;
import org.jboss.resteasy.client.ClientResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A AddressBookTest.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class AddressBookTest
{

   private AddressBook addressBook = AddressBook.instance();

   @Test(dependsOnMethods = {"createContact"})
   public void testGetContacts()
   {

      Contacts contacts = addressBook.findContacts().getEntity();
      Assert.assertTrue(contacts.getContactInfo().size() > 0);
   }

   @Test
   public void createContact()
   {
      Contact contact = new Contact();
      contact.setFirstName("Bill");
      contact.setLastName("Burke");
      contact.setSalutation("Mr.");
      contact.setTitle("JBoss Guy");
      EmailAddress email = new EmailAddress();
      email.setEmailAddress("bill.burke@redhat.com");
      email.setLabel(Label.WORK);
      EmailAddresses emails = new EmailAddresses();
      emails.getEmailAddress().add(email);
      contact.setEmailAddresses(emails);
      addressBook.createContact(contact);
   }

   @Test(dependsOnMethods = {"createContact"})
   public void updateContact()
   {
      Contact me = addressBook.findContactById(1l).getEntity();
      me.setSalutation("Senior!");
      int initialVersion = me.getVersion();
      Contact updated = addressBook.updateContact(1l, me).getEntity();
      Assert.assertNotSame(updated.getVersion(), initialVersion);
      Assert.assertEquals(updated.getSalutation(), "Senior!");
   }
   
   //@Test(dependsOnMethods = {"updateContact"})
   public void updateWrongContact()
   {
      Contact me = addressBook.findContactById(1l).getEntity();
      me.setSalutation("Senior!");
      ClientResponse<Contact> response = addressBook.updateContact(2l, me);
      Assert.assertEquals(response.getStatus(),500);
//      Contact updated = response.getEntity();
//      Assert.assertNotSame(updated.getVersion(), initialVersion);
//      Assert.assertEquals(updated.getSalutation(), "Senior!");
   }
   
   @Test(dependsOnMethods = {"updateContact"})
   public void deleteContact()
   {
      addressBook.deleteContact(2l);
   }
}

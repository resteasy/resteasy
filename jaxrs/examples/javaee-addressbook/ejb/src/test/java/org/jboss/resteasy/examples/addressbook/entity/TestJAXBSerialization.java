/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.examples.addressbook.error.ErrorDetail;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * A TestJAXBSerialization.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class TestJAXBSerialization
{

   @Test
   public void testWrite() throws Exception
   {
      Contact contact = new Contact();
      contact.setFirstName("Bill");
      contact.setLastName("Burke");
      contact.setSalutation("Mr.");
      contact.setTitle("JBoss Guy");
      EmailAddress email = new EmailAddress();
      email.setEmailAddress("bill.burke@redhat.com");
      email.setLabel(Label.WORK);
      contact.addEmailAddress(email);
      JAXBContext ctx1 = JAXBContext.newInstance(Contact.class);
      Marshaller marshaller = ctx1.createMarshaller();
      File file = new File("target/contact.xml");
      OutputStream out = new FileOutputStream(file);
      marshaller.marshal(contact, out);
      Assert.assertTrue(file.exists());      
   }
   
   @Test
   public void testUnmarshall() throws Exception
   {

      JAXBContext ctx1 = JAXBContext.newInstance(Contact.class);
      Unmarshaller unmarshaller = ctx1.createUnmarshaller();
      unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
      File file = new File("target/contact.xml");
      InputStream in = new FileInputStream(file);
      JAXBElement<Contact> e = unmarshaller.unmarshal(new StreamSource(in), Contact.class);
      Contact contact = e.getValue();
      Assert.assertEquals(contact.getFirstName(), "Bill");
      EmailAddress email = contact.getEmailAddresses().iterator().next();
      Assert.assertNotNull(email);
      Assert.assertNotNull(email.getContact());
      Assert.assertEquals(email.getContact(),contact);
   }
   
   @Test
   public void testCreateErrorDeatil() throws Exception {
      ErrorDetail error = ErrorDetail.create(new Exception(), Status.INTERNAL_SERVER_ERROR);
      JAXBContext ctx1 = JAXBContext.newInstance(ErrorDetail.class);
      Marshaller marshaller = ctx1.createMarshaller();
      File file = new File("target/error.xml");
      OutputStream out = new FileOutputStream(file);
      marshaller.marshal(error, out);
      Assert.assertTrue(file.exists());      
   }
}

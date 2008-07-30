/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.service;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

import org.jboss.resteasy.examples.addressbook.entity.Contact;

/**
 * A ContactPhotoResource.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@ProduceMime({"image/jpeg"})
@ConsumeMime({"image/*"})
public class ContactPhotoResource
{

   private Contact contact;
   
   /**
    * Create a new ContactPhotoResource.
    * 
    * @param contact
    */
   public ContactPhotoResource(Contact contact)
   {
      this.contact = contact;
   }
   
   @GET
   @Path("/photo/{photoId}")
   public byte[] getContactImage() {
      return contact.getPhotoData();
   }


   
   
}

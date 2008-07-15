/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.service;

import javax.ejb.Local;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.examples.addressbook.entity.Contact;
import org.jboss.resteasy.examples.addressbook.entity.Contacts;

@Local
@Path("/contacts/")
@ProduceMime({"application/xml","application/json"})
@ConsumeMime({"application/xml","application/json"})
public interface ContactService
{

   /**
    * FIXME Comment this
    * 
    * @return
    */
   @GET
   @SuppressWarnings("unchecked")
   Contacts findContacts();

   /**
    * FIXME Comment this
    * 
    * @param id
    * @return
    */
   @GET
   @Path("/{contactId}")
   Contact findContactById(@PathParam("contactId") Long id);

   /**
    * FIXME Comment this
    * 
    * @param contact
    * @return
    */
   @POST
   Response createContact(Contact contact);

   /**
    * FIXME Comment this
    * 
    * @param id
    * @param contact
    */
   @DELETE
   @Path("/{contactId}")
   void deleteContact(@PathParam("contactId") Long id, Contact contact);

}
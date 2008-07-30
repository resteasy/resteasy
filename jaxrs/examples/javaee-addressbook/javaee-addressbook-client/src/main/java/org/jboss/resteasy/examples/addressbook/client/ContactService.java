/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.client;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.examples.addressbook.client.entity.Contact;
import org.jboss.resteasy.examples.addressbook.client.entity.Contacts;
import org.jboss.resteasy.examples.addressbook.client.entity.EmailAddresses;
import org.jboss.resteasy.spi.ClientResponse;

/**
 * 
 * A ContactService.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
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
   ClientResponse<Contacts> findContacts();

   /**
    * FIXME Comment this
    * 
    * @param id
    * @return
    */
   @GET
   @Path("/{contactId}")
   ClientResponse<Contact> findContactById(@PathParam("contactId") Long id);

   @GET
   @Path("/{contactId}/emailAddresses")
   ClientResponse<EmailAddresses> getEmailAddresses(@PathParam("contactId") Long contactId) ;
   /**
    * FIXME Comment this
    * 
    * @param contact
    * @return
    */
   @POST
   ClientResponse<Response> createContact(Contact contact);

   /**
    * FIXME Comment this
    * 
    * @param id
    * @param contact
    * @return
    */
   @PUT
   @Path("/{contactId}")
   ClientResponse<Contact> updateContact(@PathParam("contactId") Long id, Contact contact);
   /**
    * FIXME Comment this
    * 
    * @param id
    * @param contact
    */
   @DELETE
   @Path("/{contactId}")
   void deleteContact(@PathParam("contactId") Long id);

}
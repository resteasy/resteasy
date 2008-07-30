/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.examples.addressbook.entity.Contact;
import org.jboss.resteasy.examples.addressbook.entity.Contacts;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * A ContactServiceBean.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Stateless
@Name("contactService")
public class ContactServiceBean implements ContactService
{

   @In
   private EntityManager entityManager;

   /**
    * FIXME Comment this
    * 
    * @return
    */
   @SuppressWarnings("unchecked")
   public Contacts findContacts()
   {
      Query query = entityManager.createNamedQuery("ContactInfo.findAll");
      List results = query.getResultList();
      return new Contacts(results);
   }

   /**
    * FIXME Comment this
    * 
    * @param id
    * @return
    */
   public Contact findContactById(Long id)
   {
      Contact contact = entityManager.find(Contact.class, id);
      if(contact == null) {
         Response response = Response.status(Status.NOT_FOUND).entity(
         "The requested ID was not found.").type("text/plain").build();
         throw new WebApplicationException(response);
      }
      return contact;
   }

  

   /**
    * FIXME Comment this
    * 
    * @param id
    * @param contact
    */
   public Contact updateContact(Long id, Contact contact)
   {
      if (!id.equals(contact.getId()))
      {
         Response response = Response.status(Status.CONFLICT).entity(
               "The requested ID does not match the entity body.").type("text/plain").build();
         throw new WebApplicationException(response);
      }
      return entityManager.merge(contact);
   }

   /**
    * FIXME Comment this
    * 
    * @param contact
    * @return
    */
   public Response createContact(Contact contact)
   {
      entityManager.persist(contact);
      UriBuilder path = UriBuilder.fromResource(ContactService.class);
      path.path(contact.getId().toString());
      Response response = Response.created(path.build()).build();
      return response;
   }

   public void deleteContact(Long id)
   {
      Contact contact = entityManager.find(Contact.class, id);
      entityManager.remove(contact);
   }
}

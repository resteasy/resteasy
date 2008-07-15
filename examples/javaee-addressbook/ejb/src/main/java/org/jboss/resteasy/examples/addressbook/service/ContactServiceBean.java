/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

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

   @Context
   private UriInfo uriInfo;
   /**
    * FIXME Comment this
    * 
    * @return
    */
   @SuppressWarnings("unchecked")
   public Contacts findContacts()
   {
      Query query = entityManager.createNamedQuery("Contact.findAll");
      query.setFirstResult(0);
      query.setMaxResults(10);
      return new Contacts(query.getResultList());
   }
   
   /**
    * FIXME Comment this
    * 
    * @param id
    * @return
    */
   public Contact findContactById(Long id) {
      return entityManager.find(Contact.class, id);
   }
   
   /**
    * FIXME Comment this
    * 
    * @param contact
    * @return
    */
   public Response createContact(Contact contact) {
      entityManager.persist(contact);
      UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
      uriBuilder.path(contact.getId().toString());
      Response response = Response.created(uriBuilder.build()).build();
      return response;
   }
   

   public void deleteContact(Long id, 
                             Contact contact) {
      entityManager.remove(contact);
   }
}

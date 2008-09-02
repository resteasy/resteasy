package org.jboss.resteasy.examples.contacts.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ClientResponse;

import org.jboss.resteasy.examples.contacts.core.Contact;
import org.jboss.resteasy.examples.contacts.core.Contacts;


/**
 * @author <a href="mailto:obrand@yahoo.com">Olivier Brand</a>
 * Jun 28, 2008
 * 
 */
@Path("/contactservice")
public interface ContactClient
{
    @GET
    @Path("contacts")
    @Produces("application/xml")
    ClientResponse<Contacts> getContacts();

    @GET
    @Path("contacts/{id}")
    @Produces("text/plain")
    @Consumes("text/plain")
    ClientResponse<Contact> getContact(@PathParam("id")Long id);

    @GET
    @Path("contacts/{id}/contacts")
    @Produces("text/plain")
    ClientResponse<Contacts> getContactsOfContact();
}

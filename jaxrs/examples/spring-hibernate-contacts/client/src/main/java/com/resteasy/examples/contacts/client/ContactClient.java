package com.resteasy.examples.contacts.client;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;

import org.resteasy.spi.ClientResponse;

import com.resteasy.examples.contacts.core.Contact;
import com.resteasy.examples.contacts.core.Contacts;


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
    @ProduceMime("application/xml")
    ClientResponse<Contacts> getContacts();

    @GET
    @Path("contacts/{id}")
    @ProduceMime("text/plain")
    @ConsumeMime("text/plain")
    ClientResponse<Contact> getContact(@PathParam("id")Long id);

    @GET
    @Path("contacts/{id}/contacts")
    @ProduceMime("text/plain")
    ClientResponse<Contacts> getContactsOfContact();
}

package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@Path(ContactsResource.CONTACTS_URL)
public class ContactsResource {
   public static final String CONTACTS_URL = "/contacts";
   @Autowired
   ContactService service;

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data")
   public Contacts getAll() {
      return service.getAll();
   }

   @POST
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data")
   public Response saveContact(@Context UriInfo uri, Contact contact)
            throws URISyntaxException {
      service.save(contact);
      URI newURI = UriBuilder.fromUri(uri.getPath()).path(contact.getLastName()).build();
      return Response.created(newURI).build();
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data/{lastName}")
   public Contact get(@PathParam("lastName") String lastName) {
      return service.getContact(lastName);
   }
}

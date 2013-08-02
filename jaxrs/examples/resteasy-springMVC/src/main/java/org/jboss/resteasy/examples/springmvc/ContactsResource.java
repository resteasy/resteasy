package org.jboss.resteasy.examples.springmvc;

import org.jboss.resteasy.annotations.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
public class ContactsResource
{
   public static final String CONTACTS_URL = "/contacts";
   @Autowired
   ContactService service;

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data")
   public Contacts getAll()
   {
      return service.getAll();
   }

   @PUT
   @POST
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data")
   public Response saveContact(@Context UriInfo uri, Contact contact)
         throws URISyntaxException
   {
      service.save(contact);
      URI newURI = UriBuilder.fromUri(uri.getPath()).path(contact.getLastName()).build();
      return Response.created(newURI).build();
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("data/{lastName}")
   public Contact get(@PathParam("lastName") String lastName)
   {
      return service.getContact(lastName);
   }

   @POST
   @PUT
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.TEXT_HTML)
   public ModelAndView saveContactForm(@Form Contact contact)
         throws URISyntaxException
   {
      service.save(contact);
      return viewAll();
   }
   
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ModelAndView viewAll()
   {
      // forward to the "contacts" view, with a request attribute named
      // "contacts" that has all of the existing contacts
      return new ModelAndView("contacts", "contacts", service.getAll());
   }
}

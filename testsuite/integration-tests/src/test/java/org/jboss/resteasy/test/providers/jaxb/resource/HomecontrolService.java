package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ObjectFactory;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.UserType;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;

@Provider
@Path("users")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class HomecontrolService {

   @POST
   public JAXBElement<UserType> demo(UserType type) {
      type.setId(type.getId() + " DemoService_visited");
      return new ObjectFactory().createUser(type);
   }

   @GET
   public Response get () {
      return Response.ok().build();
   }
}

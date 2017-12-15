package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ObjectFactory;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.UserType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
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

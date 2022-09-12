package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/jaxb")
public class Jackson2JAXBResource {

   @GET
   @Produces("application/json")
   public Jackson2XmlResourceWithJAXB getJAXBResource() {
      Jackson2XmlResourceWithJAXB resourceWithJAXB = new Jackson2XmlResourceWithJAXB();
      resourceWithJAXB.setAttr1("XXX");
      resourceWithJAXB.setAttr2("YYY");
      return resourceWithJAXB;
   }


   @GET
   @Path(("/json"))
   @Produces("application/json")
   public Jackson2XmlResourceWithJacksonAnnotation getJacksonAnnotatedResource() {
      Jackson2XmlResourceWithJacksonAnnotation resource = new Jackson2XmlResourceWithJacksonAnnotation();
      resource.setAttr1("XXX");
      resource.setAttr2("YYY");
      return resource;
   }

}

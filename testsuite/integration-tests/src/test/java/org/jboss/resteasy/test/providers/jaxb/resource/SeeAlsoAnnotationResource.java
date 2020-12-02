package org.jboss.resteasy.test.providers.jaxb.resource;

import org.junit.Assert;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

@Path("/see")
public class SeeAlsoAnnotationResource {
   @Path("/intf")
   @PUT
   @Consumes(MediaType.APPLICATION_XML)
   public void put(SeeAlsoAnnotationFooIntf foo) {
      Assert.assertTrue("The input parameter for the resource has wrong type", foo instanceof SeeAlsoAnnotationRealFoo);
      Assert.assertEquals("The foo object has unexpected content", ((SeeAlsoAnnotationRealFoo) foo).getName(), "bill");
   }

   @Path("base")
   @PUT
   @Consumes(MediaType.APPLICATION_XML)
   public void put(SeeAlsoAnnotationBaseFoo foo) {
      Assert.assertTrue("The input parameter for the resource has wrong type", foo instanceof SeeAlsoAnnotationRealFoo);
      Assert.assertEquals("The foo object has unexpected content", ((SeeAlsoAnnotationRealFoo) foo).getName(), "bill");
   }
}

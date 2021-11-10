package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.junit.Assert;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;

@Path("/")
public class JaxbCollectionResource {
   @Path("/array")
   @Produces("application/xml")
   @Consumes("application/xml")
   @POST
   public JaxbCollectionFoo[] naked(JaxbCollectionFoo[] foo) {
      Assert.assertEquals("The unmarshalled array doesn't contain 1 item, which is expected", 1, foo.length);
      Assert.assertEquals("The unmarshalled array doesn't contain correct element value", foo[0].getTest(), "hello");
      return foo;
   }

   @Path("/list")
   @POST
   @Produces("application/xml")
   @Consumes("application/xml")
   @Wrapped(element = "list", namespace = "", prefix = "")
   public List<JaxbCollectionFoo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<JaxbCollectionFoo> list) {
      Assert.assertEquals("The unmarshalled list doesn't contain 1 item, which is expected", 1, list.size());
      Assert.assertEquals("The unmarshalled list doesn't contain correct element value", list.get(0).getTest(), "hello");
      return list;
   }


}

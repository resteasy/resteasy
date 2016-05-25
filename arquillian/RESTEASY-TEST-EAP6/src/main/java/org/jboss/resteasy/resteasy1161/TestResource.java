package org.jboss.resteasy.resteasy1161;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("/")
public class TestResource
{
   @Inject
   private TestSubResource subResource;

   @Path("/sub")
   public TestSubResource getSubResouce()
   { 
      System.out.println("subResource: " + subResource);
      return subResource; 
   }
}
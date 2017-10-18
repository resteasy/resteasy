package org.jboss.resteasy.test.providers.priority.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("")
public class ProviderPriorityResource {

   @GET
   @Path("exception")
   public Response exception() throws Exception {
      throw new ProviderPriorityTestException();
   }
   
   @GET
   @Path("paramconverter/{foo}")
   public String paramConverter(@PathParam("foo") ProviderPriorityFoo foo) throws Exception {
      return foo.getFoo();
   }
}

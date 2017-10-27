package org.jboss.resteasy.test.providers.priority.resource;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

@RequestScoped
@Path("")
public class ProviderPriorityResource {

   @Inject ProviderPriorityFooParamConverterProviderCCC paramConvertrProvider;
   @Inject ProviderPriorityExceptionMapperCCC exceptionMapper;
   
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
   
   
   @GET
   @Path("register")
   public Response register() throws Exception {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      factory.registerProviderInstance(paramConvertrProvider);
      factory.registerProviderInstance(exceptionMapper);
      return Response.ok("ok").build();
   }
}

package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Assert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class ContextService {
   @GET
   @Path("/forward")
   public void forward(@Context HttpRequest request) {
      request.forward("/WEB-INF/index.html");
   }

   @GET
   @Path("/test")
   @Produces("text/plain")
   public String get(@Context UriInfo uri) {
      return uri.getBaseUri().toString();
   }

   @GET
   @Produces("text/plain")
   public String getBase(@Context UriInfo uri) {
      return uri.getBaseUri().toString();
   }

   @GET
   @Path("/test/servletcontext")
   @Produces("text/plain")
   public String get(@Context ServletContext context) {
      Assert.assertNotNull("ServletConfig was not injected", context);
      return "ok";
   }

   @GET
   @Path("/test/servletconfig")
   @Produces("text/plain")
   public String get(@Context ServletConfig config) {
      Assert.assertNotNull("ServletConfig was not injected", config);
      Assert.assertNotNull("ServletConfig don't contain servlet.greeting parameter", config.getInitParameter("servlet.greeting"));
      Assert.assertTrue("ServletConfig has some unexpected parameters", config.getInitParameterNames().hasMoreElements());
      return "ok";
   }

   @GET
   @Produces("application/json")
   @Path("stuff")
   public String getJson() {
      return "json";
   }

   @GET
   @Produces("application/xml")
   @Path("stuff")
   public String getXml() {
      return "xml";
   }
}

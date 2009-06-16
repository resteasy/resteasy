package org.jboss.resteasy.tests.context;

import org.junit.Assert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class ContextService
{
   @GET
   @Path("/test")
   @Produces("text/plain")
   public String get(@Context UriInfo uri)
   {
      System.out.println(uri.getBaseUri());
      return uri.getBaseUri().toString();
   }

   @GET
   @Produces("text/plain")
   public String getBase(@Context UriInfo uri)
   {
      System.out.println(uri.getBaseUri());
      return uri.getBaseUri().toString();
   }

   @GET
   @Path("/test/servletcontext")
   @Produces("text/plain")
   public String get(@Context ServletContext context)
   {
      Assert.assertNotNull(context);
      return "ok";
   }

   @GET
   @Path("/test/servletconfig")
   @Produces("text/plain")
   public String get(@Context ServletConfig config)
   {
      Assert.assertNotNull(config);
      return "ok";
   }
}

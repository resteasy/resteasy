package org.jboss.resteasy.springmvc.test.resources;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import java.util.Date;

@Path("/basic")
@Component
public class BasicResourceImpl
{

   @Context
   private HttpHeaders headers;

   @Context
   private UriInfo uri;

   /**
    * really simple test
    */
   @GET
   @Produces("text/plain")
   public String getBasicString()
   {
      return "org/jboss/resteasy/springmvc/test";
   }

   @GET
   @Produces("application/xml")
   @Path("object")
   public BasicJaxbObject getBasicObject()
   {
      return new BasicJaxbObject("something", new Date());
   }

   /**
    * WOOHOO!  SpringMVC ModelAndView in action
    */
   @GET
   @Produces("application/custom")
   @Path("/custom-rep")
   public ModelAndView getCustomRepresentation()
   {
      // MyCustomView is auto created
      return new ModelAndView("myCustomView");
   }

   /** */
   @GET
   @Produces("text/plain")
   @Path("/header")
   public String getContentTypeHeader()
   {
      return this.headers.getAcceptableMediaTypes().get(0).toString();
   }

   /**
    * the dao knows the path via an @Context inject value
    */
   @GET
   @Produces("text/plain")
   @Path("/url")
   public String getURL()
   {
      return uri.getPath();
   }
}

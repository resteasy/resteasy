package org.jboss.resteasy.springmvc.test.client;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/basic")
public interface BasicResource
{

   /** test basic setup -- implemented in BasicResourceImpl **/
   @GET
   @Produces("text/plain")
   String getBasicString();

   @GET
   @Produces("application/xml")
   @Path("object")
   BasicJaxbObject getBasicObject();

   @GET
   @Produces("application/json")
   @Path("object")
   Response getWrongContentTypeBasicObject();

   /** test Spring MVC ModelAndView **/
   @GET
   @Produces("application/custom")
   @Path("/custom-rep")
   String getSpringMvcValue();

   /**
    * test singleton with custom registration -- implemented in CounterResource,
    * with bean named singletonScopedResource
    **/
   @GET
   @Produces("text/plain")
   @Path("/singleton/count")
   Integer getSingletonCount();

   /**
    * test prototype with custom registration -- implemented in CounterResource,
    * with bean named prototypeScopedResource
    **/
   @GET
   @Produces("text/plain")
   @Path("/prototype/count")
   Integer getPrototypeCount();

   /**
    * test getting context header via setting of an @Context object in an @Autowired
    * constructor
    **/
   @GET
   @Produces("text/plain")
   @Path("/header")
   String getContentTypeHeader();

   /**
    * test Spring @Context injection into a separate java object by spring -
    * This is extended functionality, not JAX-RS core
    **/
   @GET
   @Produces("text/plain")
   @Path("/url")
   String getURL();

   /**
    * test Spring @Controllers along with
    **/
   @GET
   @Produces("text/plain")
   @Path("interceptor-test")
   Integer getSpringInterceptorCount(@QueryParam("type") String type);

   @GET
   @Produces("text/plain")
   @Path("bogus-rul-test")
   Response testBogusUrl();

   @GET
   @Produces("application/xml")
   @Path("spring/object/xml")
   BasicJaxbObject testSpringXml();

}

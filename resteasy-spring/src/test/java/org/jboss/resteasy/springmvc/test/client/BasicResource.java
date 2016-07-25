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
   public String getBasicString();

   @GET
   @Produces("application/xml")
   @Path("object")
   public BasicJaxbObject getBasicObject();

   @GET
   @Produces("application/json")
   @Path("object")
   public Response getWrongContentTypeBasicObject();

   /** test Spring MVC ModelAndView **/
   @GET
   @Produces("application/custom")
   @Path("/custom-rep")
   public String getSpringMvcValue();

   /**
    * test singleton with custom registration -- implemented in CounterResource,
    * with bean named singletonScopedResource
    **/
   @GET
   @Produces("text/plain")
   @Path("/singleton/count")
   public Integer getSingletonCount();

   /**
    * test prototype with custom registration -- implemented in CounterResource,
    * with bean named prototypeScopedResource
    **/
   @GET
   @Produces("text/plain")
   @Path("/prototype/count")
   public Integer getPrototypeCount();

   /**
    * test getting context header via setting of an @Context object in an @Autowired
    * constructor
    **/
   @GET
   @Produces("text/plain")
   @Path("/header")
   public String getContentTypeHeader();

   /**
    * test Spring @Context injection into a separate java object by spring -
    * This is extended functionality, not JAX-RS core
    **/
   @GET
   @Produces("text/plain")
   @Path("/url")
   public String getURL();

   /**
    * test Spring @Controllers along with
    **/
   @GET
   @Produces("text/plain")
   @Path("interceptor-test")
   public Integer getSpringInterceptorCount(@QueryParam("type") String type);

   @GET
   @Produces("text/plain")
   @Path("bogus-rul-test")
   public Response testBogusUrl();
   
   @GET
   @Produces("application/xml")
   @Path("spring/object/xml")
   public BasicJaxbObject testSpringXml();

}

package org.jboss.resteasy.cdi.test.basic;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.cdi.test.Cat;
import org.jboss.resteasy.cdi.test.Dog;
import org.jboss.resteasy.cdi.test.SubResource;


@Path("/resource")
@Produces("text/plain")
public class TestResource
{
   @Inject
   private Cat cat;
   private Cat constructorCat;
   private Cat initializerCat;
   @Context
   private UriInfo uriInfo;
   @QueryParam("foo") 
   private String fieldQuery;
   private UriInfo setterUriInfo;
   @Inject
   private SubResource subResource;
   
   public TestResource()
   {
   }

   @Inject
   public TestResource(Cat cat)
   {
      constructorCat = cat;
   }
   
   @Inject
   public void init(Cat cat)
   {
      initializerCat = cat;
   }

   @GET
   @Path("/fieldInjection")
   public boolean fieldInjection()
   {
      return cat != null;
   }
   
   @GET
   @Path("/jaxrsFieldInjection")
   public boolean jaxrsFieldInjection()
   {
      return uriInfo != null;
   }
   
   @GET
   @Path("/jaxrsFieldInjection2")
   public String jaxrsFieldInjection2()
   {
      return fieldQuery;
   }
   
   @GET
   @Path("/jaxrsSetterInjection")
   public boolean jaxrsSetterInjection()
   {
      return setterUriInfo != null;
   }
   
   @GET
   @Path("/constructorInjection")
   public boolean constructorInjection()
   {
      return constructorCat != null;
   }
   
   @GET
   @Path("/initializerInjection")
   public boolean initializerInjection()
   {
      return initializerCat != null;
   }
   
   @GET
   @Path("/jaxrsMethodInjection")
   public String jaxrsMethodInjection(@QueryParam("foo") String query)
   {
      return query;
   }
   
   @GET
   @Path("/toString")
   @Override
   public String toString()
   {
      return super.toString();
   }
   
   @GET
   @Path("/providers")
   public Dog testProviders()
   {
      return new Dog();
   }
   
   @Context
   public void setSetterUriInfo(UriInfo setterUriInfo)
   {
      this.setterUriInfo = setterUriInfo;
   }
   
   @Path("/subResource")
   public SubResource subResource()
   {
      return subResource;
   }
}

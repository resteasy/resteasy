package org.jboss.resteasy.cdi.test.ejb;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.cdi.test.Dog;
import org.jboss.resteasy.cdi.test.SubResource;

@Local
@Path("/statefulEjbResourceWithAnnotationsOnLocalInterface")
@Produces("text/plain")
public interface StatefulSessionBeanResource1Local
{
   @GET
   @Path("/fieldInjection")
   public boolean fieldInjection();
   
   @GET
   @Path("/ejbFieldInjection")
   public boolean ejbFieldInjection();
   
   @GET
   @Path("/jaxrsFieldInjection")
   public boolean jaxrsFieldInjection();
   
   @GET
   @Path("/jaxrsFieldInjection2")
   public String jaxrsFieldInjection2();
   
   @GET
   @Path("/jaxrsSetterInjection")
   public boolean jaxrsSetterInjection();
   
   @GET
   @Path("/constructorInjection")
   public boolean constructorInjection();
   
   @GET
   @Path("/initializerInjection")
   public boolean initializerInjection();
   
   @GET
   @Path("/jaxrsMethodInjection")
   public String jaxrsMethodInjection(@QueryParam("foo") String query);
   
   @GET
   @Path("/toString")
   public int getId();
   
   @GET
   @Path("/providers")
   public Dog testProviders();
   
   public void setSetterUriInfo(UriInfo setterUriInfo);
   
   @Path("/subResource")
   public SubResource subResource();

   public void remove();
}

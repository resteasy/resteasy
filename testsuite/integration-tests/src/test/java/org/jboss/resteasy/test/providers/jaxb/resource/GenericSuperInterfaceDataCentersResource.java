package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/datacenters")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface GenericSuperInterfaceDataCentersResource {

   @GET
   @Formatted
   GenericSuperInterfaceDataCenters list();

   @POST
   @Formatted
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   Response add(GenericSuperInterfaceDataCenter dataCenter);

   @DELETE
   @Path("{id}")
   Response remove(@PathParam("id") String id);

   @DELETE
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("{id}")
   Response remove(@PathParam("id") String id, GenericSuperInterfaceAction action);

   /**
    * Sub-resource locator method, returns individual GenericSuperInterfaceDataCenterResource on which the
    * remainder of the URI is dispatched.
    *
    * @param id the GenericSuperInterfaceDataCenter ID
    * @return matching subresource if found
    */
   @Path("{id}")
   GenericSuperInterfaceDataCenterResource getDataCenterSubResource(@PathParam("id") String id);
}
